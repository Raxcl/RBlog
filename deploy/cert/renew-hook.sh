#!/usr/bin/env bash
# acme.sh 每次成功续签后调用（由 setup-cert.sh 注册成 --reloadcmd）。
# 顺序：校验新证书 SAN → 分发到 nginx 证书路径 + reload → 下发腾讯云 CDN。
# CERT_TARGETS 为空时中间那步整个跳过 —— 线上默认就是这样，源站另有 HTTP-01 自动
# 续签，不该被这里动到。任何一步失败都以非 0 退出，日志留在 $STATE_DIR/renew.log。
set -uo pipefail

STATE_DIR="${RBLOG_CERT_STATE:-$HOME/.rblog-cert}"
LIVE_DIR="$STATE_DIR/live"
HOOK_DIR="$STATE_DIR/hook"
LOG="$STATE_DIR/renew.log"
NGINX_TEST_TIMEOUT=60
NGINX_RELOAD_TIMEOUT=60
CDN_PUSH_TIMEOUT=600

log() { printf '%s %s\n' "$(date '+%F %T')" "$*" | tee -a "$LOG"; }
die() { log "[X] $*"; exit 1; }

# OpenSSL 1.1.1 才有 x509 -ext；线上是 CentOS 7 的 1.0.2k，只能退回解析 -text。
cert_sans() {
  local f=$1 out
  out=$(openssl x509 -in "$f" -noout -ext subjectAltName 2>/dev/null)
  [ -n "$out" ] || out=$(openssl x509 -in "$f" -noout -text 2>/dev/null \
    | sed -n '/X509v3 Subject Alternative Name:/,/X509v3\|Signature Algorithm/p')
  # 两条路都带 "X509v3 Subject Alternative Name:" 头，统一去掉再抹平空白，
  # 结果形如 DNS:raxcl.cn,DNS:*.raxcl.cn
  printf '%s\n' "$out" | grep -v 'X509v3\|Signature Algorithm' | tr -d ' \t\r\n'
}

load_env() {
  local f
  for f in "${RBLOG_CERT_ENV:-}" "$STATE_DIR/cert.env" "$(dirname "$0")/cert.env"; do
    [ -n "$f" ] && [ -f "$f" ] && { set -a; . "$f"; set +a; log "[i] 配置来自 $f"; return 0; }
  done
  die "找不到 cert.env（找过 \$RBLOG_CERT_ENV、$STATE_DIR/cert.env、脚本同目录）"
}

log "===== 续签后处理开始 ====="
mkdir -p "$STATE_DIR"
load_env
# resolved.env 由 setup-cert.sh 探测生成，覆盖 cert.env 里的空值
[ -f "$STATE_DIR/resolved.env" ] && { set -a; . "$STATE_DIR/resolved.env"; set +a; }

[ -s "$LIVE_DIR/fullchain.cer" ] || die "$LIVE_DIR/fullchain.cer 不存在或为空"
[ -s "$LIVE_DIR/privkey.key" ]  || die "$LIVE_DIR/privkey.key 不存在或为空"

EXP=$(openssl x509 -in "$LIVE_DIR/fullchain.cer" -noout -enddate 2>/dev/null | cut -d= -f2)
log "[i] 新证书到期时间: ${EXP:-未知}"

# ── 0. 校验 SAN ──
# nginx -t 只查配置语法，不查证书是否覆盖 server_name；CDN 那边更是要等到边缘握手
# 才暴露。签出一张少域名的证书如果不拦住，就会静默覆盖掉本来正常的证书。
SANS=$(cert_sans "$LIVE_DIR/fullchain.cer")
# 用数组：普通变量展开会把 *.raxcl.cn 里的 * 当 glob 做路径名展开
need=("DNS:${CERT_PRIMARY:-}")
for san in ${CERT_SANS:-}; do need+=("DNS:$san"); done
for want in "${need[@]}"; do
  [ -n "${want#DNS:}" ] || continue
  case "$SANS" in
    *"$want"*) log "[好] SAN 覆盖 ${want#DNS:}" ;;
    *) die "新证书 SAN 不含 ${want#DNS:}，拒绝下发。实际 SAN: ${SANS:-读不到}
       多半是签发时 -d 参数被改过，看 $STATE_DIR/setup.log" ;;
  esac
done

[ -n "${CERT_TARGETS:-}" ] || [ -n "${CDN_DOMAINS:-}" ] \
  || die "CERT_TARGETS 和 CDN_DOMAINS 都是空的，没有事情可做"

distributed=0

# ── 1. 分发到 nginx 证书路径（CERT_TARGETS 为空则整段跳过）──
if [ -n "${CERT_TARGETS:-}" ]; then
  for pair in $CERT_TARGETS; do
    cert_path="${pair%%:*}"
    key_path="${pair##*:}"
    [ -n "$cert_path" ] && [ -n "$key_path" ] || { log "[!] 跳过非法配对: $pair"; continue; }

    mkdir -p "$(dirname "$cert_path")" "$(dirname "$key_path")" || die "无法创建目录 $cert_path"
    # 先备份旧的，再原子替换（写到同目录临时文件后 mv，避免 nginx reload 读到半个文件）
    for f in "$cert_path" "$key_path"; do
      [ -f "$f" ] && cp -a "$f" "$f.prev" 2>/dev/null
    done
    install -m 644 "$LIVE_DIR/fullchain.cer" "$cert_path.tmp" || die "写 $cert_path 失败"
    install -m 600 "$LIVE_DIR/privkey.key"  "$key_path.tmp"  || die "写 $key_path 失败"
    mv -f "$cert_path.tmp" "$cert_path" || die "替换 $cert_path 失败"
    mv -f "$key_path.tmp"  "$key_path"  || die "替换 $key_path 失败"
    log "[好] 已更新 $cert_path"
    distributed=$((distributed + 1))
  done
  [ "$distributed" -gt 0 ] || die "一个证书路径都没写成"
else
  log "[=] CERT_TARGETS 为空，不动源站证书（源站另有自己的自动续签）"
fi

# ── 2. 校验并 reload nginx ──
if [ "$distributed" -eq 0 ]; then
  log "[=] 没改动源站证书文件，跳过 nginx reload"
elif [ -n "${NGINX_CONTAINER:-}" ]; then
  log "[i] 校验 nginx 配置（容器 $NGINX_CONTAINER）..."
  if ! timeout "$NGINX_TEST_TIMEOUT" docker exec "$NGINX_CONTAINER" nginx -t >>"$LOG" 2>&1; then
    # 此刻还没 reload，nginx 内存里跑的仍是旧证书，把磁盘文件恢复成 .prev 就回到一致状态。
    # 再 reload 反而可能把有问题的配置推上去，所以不 reload。
    restored=0
    for pair in $CERT_TARGETS; do
      cert_path="${pair%%:*}"; key_path="${pair##*:}"
      if [ -f "$cert_path.prev" ] && [ -f "$key_path.prev" ]; then
        mv -f "$cert_path.prev" "$cert_path" && mv -f "$key_path.prev" "$key_path" \
          && restored=$((restored + 1))
      fi
    done
    if [ "$restored" -gt 0 ]; then
      die "nginx -t 失败，已回滚 $restored 组证书到上一版；nginx 未 reload，仍在用旧证书正常服务"
    fi
    die "nginx -t 失败，且没有 .prev 备份可回滚（首次运行？）；nginx 未 reload，仍在用内存里的旧证书服务
       请手工检查这些文件：$CERT_TARGETS
       nginx -t 的输出见 $LOG"
  fi
  log "[i] reload nginx ..."
  timeout "$NGINX_RELOAD_TIMEOUT" docker exec "$NGINX_CONTAINER" nginx -s reload >>"$LOG" 2>&1 \
    || die "nginx reload 失败"
  log "[好] nginx 已 reload"
else
  log "[!] NGINX_CONTAINER 未设置，跳过 reload（证书文件已更新，需自行让 nginx 重新加载）"
fi

# ── 3. 下发到腾讯云 CDN ──
if [ -n "${CDN_DOMAINS:-}" ]; then
  if [ -z "${TENCENT_SECRET_ID:-}" ] || [ -z "${TENCENT_SECRET_KEY:-}" ]; then
    log "[!] 未配置 TENCENT_SECRET_ID/KEY，跳过 CDN 下发"
  else
    log "[i] 下发到 CDN: $CDN_DOMAINS"
    export TENCENT_SECRET_ID TENCENT_SECRET_KEY
    if timeout "$CDN_PUSH_TIMEOUT" python3 "$HOOK_DIR/push-to-cdn.py" \
         --cert "$LIVE_DIR/fullchain.cer" --key "$LIVE_DIR/privkey.key" \
         --domains "$CDN_DOMAINS" >>"$LOG" 2>&1; then
      log "[好] CDN 下发完成"
    else
      # CDN 下发失败不牵连源站：源站证书已经生效，CDN 可以人工补跑
      log "[X] CDN 下发失败（源站证书已生效）。手动补跑："
      log "    python3 $HOOK_DIR/push-to-cdn.py --cert $LIVE_DIR/fullchain.cer --key $LIVE_DIR/privkey.key --domains '$CDN_DOMAINS'"
      log "===== 续签后处理结束（CDN 失败）====="
      exit 2
    fi
  fi
else
  log "[=] CDN_DOMAINS 为空，跳过 CDN 下发"
fi

log "===== 续签后处理结束（成功）====="
