#!/usr/bin/env bash
# RBlog CDN 证书自动化：acme.sh + DNSPod DNS-01 给 CDN 域名签 Let's Encrypt 证书，
# 下发到腾讯云 CDN，之后由 acme.sh 自带 cron 每 60 天自动续，不用再手工换。
# 源站 raxcl.cn / admin.raxcl.cn 另有 HTTP-01 自动续签，默认不碰（MANAGE_ORIGIN=0）。
#
# 用法：bash setup-cert.sh [选项]
#   --check  只读诊断，不改任何东西。先跑这个，看探测结果对不对再往下走
#   --force  已经签过也重签（换了凭据、加了域名时用）
set -uo pipefail

REPO_DIR=$(cd "$(dirname "$0")" && pwd)
STATE_DIR="${RBLOG_CERT_STATE:-$HOME/.rblog-cert}"
LIVE_DIR="$STATE_DIR/live"
HOOK_DIR="$STATE_DIR/hook"
LOG="$STATE_DIR/setup.log"
ACME_DIR="$HOME/.acme.sh"
ACME_SH="$ACME_DIR/acme.sh"
ACME_CA=letsencrypt
KEYLENGTH=2048
RENEW_DAYS=21
INSTALL_TIMEOUT=240
# --install-cert 会同步跑一遍 reloadcmd（即 renew-hook.sh），而钩子自己的预算是
# nginx -t 60 + reload 60 + CDN 下发 600 = 720s。这里必须比它大，否则钩子还在正常
# 干活就被掐死，报一个跟真实原因无关的"--install-cert 失败"。
INSTALL_CERT_TIMEOUT=780
ISSUE_TIMEOUT=300
DOCKER_TIMEOUT=60

CHECK_ONLY=0
FORCE=0
for a in "$@"; do
  case "$a" in
    --check) CHECK_ONLY=1 ;;
    --force) FORCE=1 ;;
    -h|--help) sed -n '2,8p' "$0"; exit 0 ;;
    *) echo "未知参数: $a"; exit 64 ;;
  esac
done

mkdir -p "$STATE_DIR"
# 进度输出走 stderr：stdout 只留给函数返回值（resolve_targets 等用 $(...) 取结果），
# 否则日志行会被当成证书路径捕获进去
log() { printf '%s %s\n' "$(date '+%F %T')" "$*" | tee -a "$LOG" >&2; }
die() { log "[X] $*"; exit 1; }
warn() { log "[!] $*"; }
ok() { log "[好] $*"; }
skip() { log "[=] $*"; }

# ────────────────────────── 配置加载 ──────────────────────────
load_env() {
  local f
  for f in "${RBLOG_CERT_ENV:-}" "$STATE_DIR/cert.env" "$REPO_DIR/cert.env"; do
    if [ -n "$f" ] && [ -f "$f" ]; then
      set -a; . "$f"; set +a
      log "[i] 配置来自 $f"
      return 0
    fi
  done
  die "找不到 cert.env。先执行：
  mkdir -p $STATE_DIR
  cp $REPO_DIR/cert.env.example $STATE_DIR/cert.env
  chmod 600 $STATE_DIR/cert.env
  然后填入 TENCENT_SECRET_ID / TENCENT_SECRET_KEY / CERT_EMAIL"
}

# ────────────────────────── nginx 容器探测 ──────────────────────────
detect_nginx_container() {
  [ -n "${NGINX_CONTAINER:-}" ] && { printf '%s\n' "$NGINX_CONTAINER"; return 0; }
  command -v docker >/dev/null 2>&1 || return 1
  local found
  found=$(timeout "$DOCKER_TIMEOUT" docker ps --format '{{.Names}}|{{.Image}}|{{.Ports}}' 2>/dev/null \
          | awk -F'|' 'tolower($2) ~ /nginx/ && $3 ~ /443/ {print $1}')
  local n
  n=$(printf '%s\n' "$found" | grep -c . || true)
  [ "$n" -eq 0 ] && return 1
  if [ "$n" -gt 1 ]; then
    warn "探测到多个 nginx 容器，取第一个：$(printf '%s ' $found)"
    warn "如需指定，在 cert.env 里设 NGINX_CONTAINER"
  fi
  printf '%s\n' "$found" | head -1
}

# 容器内路径 -> 宿主机路径（按 docker 挂载最长前缀匹配）
container_to_host() {
  local c=$1 p=$2 best_src="" best_dst="" src dst
  while IFS=$'\t' read -r src dst; do
    [ -z "${dst:-}" ] && continue
    case "$p" in
      "$dst"|"$dst"/*) [ "${#dst}" -gt "${#best_dst}" ] && { best_src=$src; best_dst=$dst; } ;;
    esac
  done < <(timeout "$DOCKER_TIMEOUT" docker inspect "$c" \
             --format '{{range .Mounts}}{{.Source}}{{"\t"}}{{.Destination}}{{"\n"}}{{end}}' 2>/dev/null)
  [ -n "$best_src" ] || return 1
  case "$best_src" in
    /var/lib/docker/volumes/*) warn "$p 落在命名卷 $best_src 上，续期能写但不易备份" ;;
  esac
  printf '%s%s\n' "$best_src" "${p#"$best_dst"}"
}

# 从 nginx -T 里抓 ssl_certificate / ssl_certificate_key 配对（容器内路径）
# 先剥注释再把 ';' 换成换行做归一化：nginx 允许两条指令写在同一行，
# 不归一化的话行首锚定会漏抓，探测就静默失败了
detect_container_cert_pairs() {
  timeout "$DOCKER_TIMEOUT" docker exec "$1" nginx -T 2>/dev/null \
    | sed 's/#.*//' | tr ';' '\n' \
    | awk '/^[[:space:]]*ssl_certificate[[:space:]]/{c=$2; sub(/;$/,"",c); cert=c}
           /^[[:space:]]*ssl_certificate_key[[:space:]]/{k=$2; sub(/;$/,"",k);
             if(cert!=""){print cert "|" k; cert=""}}' \
    | tr -d '"\047' | sort -u
}

resolve_targets() {
  local container=$1 pairs host_pairs="" cp hp ck hk
  pairs=$(detect_container_cert_pairs "$container")
  [ -n "$pairs" ] || { warn "nginx 配置里没抓到 ssl_certificate 指令"; return 1; }
  while IFS='|' read -r cp ck; do
    [ -n "${cp:-}" ] || continue
    hp=$(container_to_host "$container" "$cp") || { warn "$cp 不在任何挂载里，无法从宿主机写入"; return 1; }
    hk=$(container_to_host "$container" "$ck") || { warn "$ck 不在任何挂载里，无法从宿主机写入"; return 1; }
    log "      容器 $cp"
    log "        -> 宿主 $hp"
    host_pairs="$host_pairs $hp:$hk"
  done <<< "$pairs"
  printf '%s\n' "${host_pairs# }"
}

# ────────────────────────── acme.sh ──────────────────────────
install_acme() {
  if [ -x "$ACME_SH" ]; then
    skip "acme.sh 已安装（$("$ACME_SH" --version 2>/dev/null | head -1)）"
    return 0
  fi
  log "[i] 安装 acme.sh ..."
  local t0=$SECONDS
  if timeout 150 bash -c 'curl -sSL --connect-timeout 10 https://get.acme.sh -o /tmp/get-acme.sh && sh /tmp/get-acme.sh --install' >>"$LOG" 2>&1; then
    ok "get.acme.sh 安装成功（$((SECONDS - t0))s）"
  else
    warn "get.acme.sh 失败（$((SECONDS - t0))s），改走 gitee 镜像"
    t0=$SECONDS
    timeout "$INSTALL_TIMEOUT" bash -c 'rm -rf /tmp/acme.sh-src /tmp/acme.sh.tar.gz
      && curl -sSL --connect-timeout 10 -o /tmp/acme.sh.tar.gz https://gitee.com/neilpang/acme.sh/repository/archive/master.tar.gz
      && mkdir -p /tmp/acme.sh-src && tar xzf /tmp/acme.sh.tar.gz -C /tmp/acme.sh-src --strip-components=1
      && cd /tmp/acme.sh-src && sh ./acme.sh --install' >>"$LOG" 2>&1 \
      || die "acme.sh 两种方式都装不上，看 $LOG"
    ok "gitee 镜像安装成功（$((SECONDS - t0))s）"
  fi
  [ -x "$ACME_SH" ] || die "acme.sh 装完但 $ACME_SH 不存在"
}

DNS_PLUGIN=dns_tencent
# install_acme 只看主程序在不在。线上那份 acme.sh 是"只拷一个文件"式装的，
# 整个 dnsapi/ 都没有 —— HTTP-01 webroot 用不到插件，所以一直没人发现。
# 缺插件时 acme.sh 不会明确报错，而是打印 "Cannot find DNS API hook for: dns_tencent"
# 叫你手动加 TXT 记录，然后以退出码 3 失败，现场完全看不出是插件没装。
ensure_dns_plugin() {
  local f="$ACME_DIR/dnsapi/$DNS_PLUGIN.sh" url t0
  if [ -s "$f" ] && grep -q "${DNS_PLUGIN}_add() {" "$f"; then
    skip "DNS 插件 $DNS_PLUGIN 已就位"
    return 0
  fi
  log "[i] 补装 DNS 插件 $DNS_PLUGIN ..."
  mkdir -p "$ACME_DIR/dnsapi" || die "建不出 $ACME_DIR/dnsapi"
  t0=$SECONDS
  # gitee 在前：这台服务器拉 raw.githubusercontent.com 不通
  for url in "https://gitee.com/neilpang/acme.sh/raw/master/dnsapi/$DNS_PLUGIN.sh" \
             "https://raw.githubusercontent.com/acmesh-official/acme.sh/master/dnsapi/$DNS_PLUGIN.sh"; do
    if timeout 90 curl -fsSL --connect-timeout 10 -o "$f.tmp" "$url" >>"$LOG" 2>&1 \
       && grep -q "${DNS_PLUGIN}_add() {" "$f.tmp"; then
      mv -f "$f.tmp" "$f" && chmod 644 "$f"
      ok "DNS 插件已装到 $f（$((SECONDS - t0))s）"
      return 0
    fi
    warn "$url 没拿到可用插件，换下一个源"
    rm -f "$f.tmp"
  done
  die "装不上 DNS 插件 $DNS_PLUGIN，手动放一份到 $f
       来源：https://gitee.com/neilpang/acme.sh/raw/master/dnsapi/$DNS_PLUGIN.sh"
}

acme_cert_file() {
  local d
  for d in "$ACME_DIR/${CERT_PRIMARY}_ecc" "$ACME_DIR/${CERT_PRIMARY}"; do
    [ -f "$d/${CERT_PRIMARY}.cer" ] && { printf '%s\n' "$d/${CERT_PRIMARY}.cer"; return 0; }
  done
  return 1
}

# 已经有一套 HTTP-01 自动续签在跑（源站的 raxcl.cn / admin.raxcl.cn 就是）。
# acme.sh 按主域名定位证书目录，撞名的话 --issue 会把 Le_Webroot 从路径就地改写成
# dns_tencent，那套原本不依赖任何云凭据的续签就被顶掉了，而且现场看不出是谁干的。
guard_no_hijack() {
  local d conf wr
  for d in "$ACME_DIR/${CERT_PRIMARY}_ecc" "$ACME_DIR/${CERT_PRIMARY}"; do
    conf="$d/${CERT_PRIMARY}.conf"
    [ -f "$conf" ] || continue
    wr=$(sed -n "s/^Le_Webroot='\(.*\)'$/\1/p" "$conf" | head -1)
    case "$wr" in
      ''|dns_tencent) return 0 ;;
    esac
    die "$conf 已存在，用的是 '$wr'（HTTP-01 webroot），不是 dns_tencent。
       继续签发会把它改写成 DNS-01，顶掉现有那套正在工作的自动续签。
       请换一个 CERT_PRIMARY（当前 '$CERT_PRIMARY'）；确实要接管就先手动备份那个目录。"
  done
  return 0
}

# $1=待查域名 $2=空格分隔的完整域名列表；被列表里的通配符覆盖就打出那个通配符并返回 0
covered_by_wildcard() {
  local n=$1 list=$2 w rest prefix
  for w in $list; do
    [ "$w" = "$n" ] && continue
    case "$w" in \*.*) ;; *) continue ;; esac
    rest=".${w#\*.}"
    case "$n" in *"$rest") ;; *) continue ;; esac
    prefix="${n%"$rest"}"
    # 通配符只覆盖一层标签：a.example.com 命中 *.example.com，a.b.example.com 不命中
    case "$prefix" in ''|*.*) continue ;; esac
    printf '%s\n' "$w"
    return 0
  done
  return 1
}

# Let's Encrypt 不接受同一张证书里既有 *.example.com 又有 a.example.com，
# 也不接受重复域名，返回 400 "is redundant with a wildcard domain in the same request"。
# 那个错误要等真正下单才出现（DNS-01 已经跑过一轮），所以这里本地先拦。
# 结果写进全局 SAN_PROBLEM：签发前 die，--check 时只打印。
SAN_PROBLEM=""
check_san_list() {
  SAN_PROBLEM=""
  local all="$CERT_PRIMARY ${CERT_SANS:-}" n w dup
  for n in $all; do
    if w=$(covered_by_wildcard "$n" "$all"); then
      SAN_PROBLEM="$n 已经被同一张证书里的通配符 $w 覆盖，Let's Encrypt 会直接拒单。
       把 $n 从 CERT_PRIMARY / CERT_SANS 里去掉。当前列表：$all"
      return 1
    fi
  done
  dup=$(printf '%s\n' $all | sort | uniq -d | head -1)
  if [ -n "$dup" ]; then
    SAN_PROBLEM="$dup 在域名列表里出现了多次，去掉重复项。当前列表：$all"
    return 1
  fi
  return 0
}

days_left() {
  local f=$1 end now
  end=$(openssl x509 -in "$f" -noout -enddate 2>/dev/null | cut -d= -f2) || return 1
  [ -n "$end" ] || return 1
  now=$(date +%s)
  end=$(date -d "$end" +%s 2>/dev/null) || return 1
  echo $(( (end - now) / 86400 ))
}

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

issue_cert() {
  # dns_tencent 插件读的是 Tencent_SecretId / Tencent_SecretKey（首字母大写），
  # bash 变量名区分大小写，写成全大写它会认为凭据没配而直接签发失败
  export Tencent_SecretId="$TENCENT_SECRET_ID"
  export Tencent_SecretKey="$TENCENT_SECRET_KEY"

  guard_no_hijack

  local cer="" left="?" cmd=() san
  if cer=$(acme_cert_file); then
    left=$(days_left "$cer" 2>/dev/null || echo "?")
    log "[i] 已有证书 $cer，剩余 ${left} 天"
    if [ "$FORCE" -eq 0 ] && [ "$left" != "?" ] && [ "$left" -gt "$RENEW_DAYS" ]; then
      skip "剩余 ${left} 天 > ${RENEW_DAYS} 天，不重签（要强制加 --force）"
      return 0
    fi
    cmd=(--renew -d "$CERT_PRIMARY" --force)
    log "[i] 重签 ..."
  else
    cmd=(--issue --server "$ACME_CA" --dns dns_tencent -d "$CERT_PRIMARY")
    for san in $CERT_SANS; do cmd+=( -d "$san" ); done
    cmd+=(--keylength "$KEYLENGTH")
    [ -n "${CERT_EMAIL:-}" ] && cmd+=( -m "$CERT_EMAIL" )
    log "[i] 首次签发 $CERT_PRIMARY${CERT_SANS:+ + $CERT_SANS} ..."
  fi

  local t0=$SECONDS rc=0 out err
  out=$(mktemp)
  timeout "$ISSUE_TIMEOUT" "$ACME_SH" "${cmd[@]}" >"$out" 2>&1; rc=$?
  cat "$out" >>"$LOG"
  if [ "$rc" -ne 0 ]; then
    # 别按关键词摘错误行（上次 "Cannot find DNS API hook" 一个词都没命中，等于没给信息），
    # 直接倒本次输出的尾部原文
    err=$(tail -15 "$out")
    rm -f "$out"
    die "签发/续签失败（退出码 $rc，耗时 $((SECONDS - t0))s）。acme.sh 输出的最后几行：
$err

       全文：$LOG"
  fi
  rm -f "$out"
  ok "证书签发完成（$((SECONDS - t0))s）"
}

# acme.sh 会把 Tencent_SecretId/SecretKey 存进 account.conf，而它默认 644 全局可读。
# cron 续签全靠这份文件，每次重装 acme.sh 都会复现，所以每跑一次都收紧一遍。
harden_acme_secrets() {
  local f="$ACME_DIR/account.conf" m
  [ -f "$f" ] || return 0
  m=$(stat -c '%a' "$f" 2>/dev/null)
  [ "$m" = "600" ] && return 0
  if chmod 600 "$f"; then
    ok "account.conf 权限从 ${m:-?} 收紧到 600（里面有云凭据）"
  else
    warn "收紧 $f 权限失败，里面有云凭据，手动 chmod 600"
  fi
}

# --check 的凭据段。SecretKey 一个字符都不回显。
# 别写成 ${VAR:+已设置}${VAR:-未设置}：":-" 只在变量为空/未设置时才替换成后备值，
# 变量已设置时它展开的就是变量本身 —— 那个写法会把 SecretKey 全量打进 stdout，
# 重定向进日志就是持久化泄露。
report_credentials() {
  if [ -n "${TENCENT_SECRET_ID:-}" ]; then
    # SecretId 不是秘密（腾讯云控制台里本来就全量可见），留前 8 位是为了核对配的是哪把 key
    echo "  TENCENT_SECRET_ID:  [OK] $(printf '%s' "$TENCENT_SECRET_ID" | cut -c1-8)……（共 ${#TENCENT_SECRET_ID} 字符）"
  else
    echo "  TENCENT_SECRET_ID:  [!!] 未设置"
  fi
  if [ -n "${TENCENT_SECRET_KEY:-}" ]; then
    echo "  TENCENT_SECRET_KEY: [OK] 已设置（共 ${#TENCENT_SECRET_KEY} 字符，内容不回显）"
  else
    echo "  TENCENT_SECRET_KEY: [!!] 未设置"
  fi
  if [ -n "${CERT_EMAIL:-}" ]; then
    echo "  CERT_EMAIL:         [OK] $CERT_EMAIL"
  else
    # ACME 里 contact 是可选的，实测不填也能正常签发，所以这不是错误 ——
    # 只是收不到 Let's Encrypt 的到期提醒邮件，而我们有 cron + 本报告兜底。
    echo "  CERT_EMAIL:         [--] 未设置（不影响签发，只是收不到 Let's Encrypt 到期提醒邮件）"
  fi
}

# 列同机上所有 acme.sh 证书和它们的验证方式，标注跟 CERT_PRIMARY 撞名的那个。
# 撞名很危险：acme.sh 按主域名定位目录，--issue 会把现有那张的 Le_Webroot 从路径
# 就地改写成 dns_tencent，静默顶掉原本不依赖云凭据的 HTTP-01 续签。
list_acme_certs() {
  echo "  同机上其它 acme.sh 证书（别撞名，撞了会被 DNS-01 改写）："
  local d n dom wr
  for d in "$ACME_DIR"/*/; do
    [ -d "$d" ] || continue
    n=$(basename "$d")
    dom="${n%_ecc}"
    # conf 文件名是不带 _ecc 后缀的主域名。以它存在与否判断这是不是一个证书目录 ——
    # ~/.acme.sh/ 下还有 dnsapi / ca / deploy 这些程序目录，硬编码黑名单迟早漏。
    [ -f "$d/$dom.conf" ] || continue
    wr=$(sed -n "s/^Le_Webroot='\(.*\)'$/\1/p" "$d/$dom.conf" 2>/dev/null | head -1)
    printf '    %-28s 验证方式=%s%s\n' "$dom" "${wr:-?}" \
      "$([ "$dom" = "$CERT_PRIMARY" ] && echo '   <<< 和 CERT_PRIMARY 撞名')"
  done
}

# 一行一个 SAN，缩进 4 格。必须保证输出以换行结尾：cert_sans 结尾用 tr -d 抹掉了
# 换行，而 GNU sed 不会给缺尾换行的最后一行补上（4.2.2 和 4.9 都实测如此），直接
# 管道出去会和下一段的标题粘成一行。所以先用命令替换收（它会剥掉尾部换行），
# 再 printf 补恰好一个。
print_cert_sans() {
  local sans
  sans=$(cert_sans "$1" | tr ',' '\n' | sed '/^$/d; s/^/    /')
  [ -n "$sans" ] && printf '%s\n' "$sans"
  return 0
}

install_to_staging() {
  mkdir -p "$LIVE_DIR" "$HOOK_DIR"
  # 把钩子和推送脚本复制到 STATE_DIR，这样 acme.sh 存的 reloadcmd 是稳定绝对路径，
  # 不受仓库目录被 git pull / 挪位置影响
  install -m 755 "$REPO_DIR/renew-hook.sh"  "$HOOK_DIR/renew-hook.sh"  || die "复制 renew-hook.sh 失败"
  install -m 755 "$REPO_DIR/push-to-cdn.py" "$HOOK_DIR/push-to-cdn.py" || die "复制 push-to-cdn.py 失败"

  log "[i] 注册证书安装路径 + 续签钩子 ..."
  local rc=0 out err
  out=$(mktemp)
  timeout "$INSTALL_CERT_TIMEOUT" "$ACME_SH" --install-cert -d "$CERT_PRIMARY" \
    --key-file       "$LIVE_DIR/privkey.key" \
    --fullchain-file "$LIVE_DIR/fullchain.cer" \
    --reloadcmd      "$HOOK_DIR/renew-hook.sh" >"$out" 2>&1; rc=$?
  cat "$out" >>"$LOG"
  if [ "$rc" -ne 0 ]; then
    err=$(tail -15 "$out")
    rm -f "$out"
    die "--install-cert 失败（退出码 $rc，超过 ${INSTALL_CERT_TIMEOUT}s 也算失败）。最后几行：
$err

       全文：$LOG"
  fi
  rm -f "$out"
  ok "已注册：$LIVE_DIR/{fullchain.cer,privkey.key} + 钩子 $HOOK_DIR/renew-hook.sh"
}

write_resolved() {
  local container=$1 targets=$2
  {
    printf '# setup-cert.sh 自动探测生成，可手改；重跑 setup-cert.sh 会覆盖\n'
    printf 'NGINX_CONTAINER=%s\n' "$container"
    printf "CERT_TARGETS='%s'\n" "$targets"
  } > "$STATE_DIR/resolved.env"
  ok "探测结果已写入 $STATE_DIR/resolved.env"
}

ensure_cron() {
  local svc
  for svc in crond cron; do
    if systemctl is-active "$svc" >/dev/null 2>&1; then
      ok "定时服务 $svc 在运行"
      if crontab -l 2>/dev/null | grep -q 'acme.sh'; then
        ok "acme.sh 的 cron 已注册：$(crontab -l | grep 'acme.sh' | head -1)"
      else
        warn "没找到 acme.sh 的 crontab 条目，手动补：$ACME_SH --install-cronjob"
      fi
      return 0
    fi
  done
  warn "crond/cron 都没在运行，自动续签不会触发。尝试启动..."
  systemctl enable --now crond >/dev/null 2>&1 || systemctl enable --now cron >/dev/null 2>&1 \
    || die "启动定时服务失败，请手动：systemctl enable --now crond"
  ok "定时服务已启动"
}

# ────────────────────────── 线上证书体检 ──────────────────────────
probe_live_cert() {
  local d=$1 out issuer end left="?" ts now mark="??"
  out=$(timeout 25 bash -c "echo | openssl s_client -connect $d:443 -servername $d 2>/dev/null" \
        | openssl x509 -noout -issuer -enddate 2>/dev/null)
  if [ -z "$out" ]; then
    printf '[--] %-20s 取不到证书（域名不通 / 443 未开 / 无解析）\n' "$d"
    return
  fi
  issuer=$(printf '%s\n' "$out" | grep '^issuer=' | sed 's/^issuer=//; s/.*CN=//')
  end=$(printf '%s\n' "$out" | grep '^notAfter=' | cut -d= -f2-)
  if ts=$(date -d "$end" +%s 2>/dev/null); then
    now=$(date +%s); left=$(( (ts - now) / 86400 ))
    if   [ "$left" -lt 0 ];            then mark="[XX]"
    elif [ "$left" -le "$RENEW_DAYS" ]; then mark="[!!]"
    else                                     mark="[OK]"
    fi
  fi
  printf '%s %-20s 剩 %-6s 到期 %s  签发 %s\n' "$mark" "$d" "$left" "$end" "$issuer"
}

# ────────────────────────── 主流程 ──────────────────────────
main() {
  log "===== setup-cert.sh 开始（CHECK_ONLY=$CHECK_ONLY FORCE=$FORCE）====="
  load_env
  : "${CERT_PRIMARY:?cert.env 里 CERT_PRIMARY 不能为空}"
  MANAGE_ORIGIN="${MANAGE_ORIGIN:-0}"
  # 体检探测用的根域名，没写就从主域名砍掉第一段标签推
  APEX_DOMAIN="${APEX_DOMAIN:-${CERT_PRIMARY#*.}}"
  command -v openssl >/dev/null 2>&1 || die "没有 openssl 命令"

  # 域名列表自相矛盾时 CA 会拒单，这里比 docker 探测更早失败；--check 时留给报告打印
  check_san_list
  [ "$CHECK_ONLY" -eq 1 ] || [ -z "$SAN_PROBLEM" ] \
    || die "证书域名列表有问题：$SAN_PROBLEM"

  local container="" targets="" d n wr dom m
  if command -v docker >/dev/null 2>&1; then
    container=$(detect_nginx_container) || container=""
    if [ -n "$container" ]; then
      ok "nginx 容器 = $container"
      if [ -n "${CERT_TARGETS:-}" ]; then
        targets="$CERT_TARGETS"
        log "[i] 使用 cert.env 里手写的 CERT_TARGETS"
      else
        targets=$(resolve_targets "$container") || { warn "证书路径自动探测失败"; targets=""; }
      fi
    else
      warn "没探测到发布 443 端口的 nginx 容器"
      if [ "$MANAGE_ORIGIN" = "1" ] && [ "$CHECK_ONLY" -eq 0 ]; then
        timeout "$DOCKER_TIMEOUT" docker ps --format '  {{.Names}}\t{{.Image}}\t{{.Ports}}' 2>&1 | tee -a "$LOG"
        die "MANAGE_ORIGIN=1 但找不到 nginx 容器，请在 cert.env 里手动设置 NGINX_CONTAINER"
      fi
    fi
  else
    warn "没有 docker 命令，跳过 nginx 探测"
    [ "$MANAGE_ORIGIN" = "1" ] && die "MANAGE_ORIGIN=1 需要 docker"
  fi

  if [ "$CHECK_ONLY" -eq 1 ]; then
    echo ""
    echo "════════ 只读诊断报告（未修改任何东西）════════"
    echo ""
    echo "── 运行模式 ──"
    if [ "$MANAGE_ORIGIN" = "1" ]; then
      echo "  MANAGE_ORIGIN=1  接管源站 nginx + 下发 CDN"
    else
      echo "  MANAGE_ORIGIN=0  只下发 CDN，不碰源站证书"
    fi
    echo "  证书主域名: $CERT_PRIMARY"
    echo "  额外域名:   ${CERT_SANS:-（无）}"
    [ -z "$SAN_PROBLEM" ] || echo "  [XX] 域名列表有问题：$SAN_PROBLEM"
    echo ""
    echo "── nginx 容器 ──"
    if [ -n "$container" ]; then
      echo "  容器名: $container"
      timeout "$DOCKER_TIMEOUT" docker ps --filter "name=$container" --format '  镜像: {{.Image}}  端口: {{.Ports}}  状态: {{.Status}}' 2>&1
    else
      echo "  [--] 没探测到"
    fi
    echo ""
    echo "── 证书落盘路径（宿主机）──"
    if [ -n "$targets" ]; then
      local c_path k_path w sans
      for pair in $targets; do
        c_path="${pair%%:*}"; k_path="${pair##*:}"
        if [ -f "$c_path" ]; then
          d=$(days_left "$c_path" 2>/dev/null || echo "?")
          w=$([ -w "$c_path" ] && echo "可写" || echo "不可写(需 root)")
          echo "  [OK] $c_path  剩 ${d} 天  $w"
          sans=$(cert_sans "$c_path")
          echo "       SAN: ${sans:-读不到}"
        else
          echo "  [--] $c_path  （文件还不存在）"
        fi
        if [ -f "$k_path" ]; then echo "       $k_path"; else echo "       $k_path （不存在）"; fi
      done
      [ "$MANAGE_ORIGIN" = "1" ] || echo "  (= MANAGE_ORIGIN=0，以上路径只读展示，不会被改写)"
    else
      echo "  [--] 没探测到"
      [ "$MANAGE_ORIGIN" = "1" ] && echo "  [!!] MANAGE_ORIGIN=1 需要手写 CERT_TARGETS"
    fi
    echo ""
    echo "── acme.sh ──"
    if [ -x "$ACME_SH" ]; then
      echo "  [OK] 已安装 $("$ACME_SH" --version 2>/dev/null | head -1)"
      if [ -s "$ACME_DIR/dnsapi/$DNS_PLUGIN.sh" ]; then
        echo "  [OK] DNS 插件 $DNS_PLUGIN 已就位"
      else
        echo "  [XX] 缺 DNS 插件 $ACME_DIR/dnsapi/$DNS_PLUGIN.sh"
        echo "       签发会以 \"Cannot find DNS API hook\" + 退出码 3 失败；正式跑会自动补装"
      fi
      if [ -f "$ACME_DIR/account.conf" ]; then
        m=$(stat -c '%a' "$ACME_DIR/account.conf" 2>/dev/null)
        if [ "$m" = "600" ]; then
          echo "  [OK] account.conf 权限 600"
        else
          echo "  [!!] account.conf 权限 ${m:-?}，里面有云凭据；正式跑会收紧到 600"
        fi
      fi
      if cer=$(acme_cert_file); then
        echo "  已签证书: $cer  剩 $(days_left "$cer" 2>/dev/null || echo '?') 天"
        print_cert_sans "$cer"
      else
        echo "  [--] 还没签过 $CERT_PRIMARY"
      fi
      list_acme_certs
      crontab -l 2>/dev/null | grep 'acme.sh' | sed 's/^/  cron: /' || echo "  [!!] 无 acme.sh cron 条目"
    else
      echo "  [--] 未安装"
    fi
    echo ""
    echo "── 凭据 ──"
    report_credentials
    command -v python3 >/dev/null 2>&1 && echo "  python3:            [OK] $(python3 -V 2>&1)" || echo "  python3:            [!!] 未安装（CDN 下发需要）"
    echo ""
    echo "── 线上实际证书 ──"
    for d in "$APEX_DOMAIN" "www.$APEX_DOMAIN" "admin.$APEX_DOMAIN" ${CDN_DOMAINS:-}; do
      probe_live_cert "$d"
    done
    echo ""
    echo "════════ 诊断结束 ════════"
    if [ "$MANAGE_ORIGIN" = "1" ] && [ -z "$targets" ]; then exit 3; fi
    exit 0
  fi

  [ -n "${TENCENT_SECRET_ID:-}" ] && [ -n "${TENCENT_SECRET_KEY:-}" ] \
    || die "cert.env 里 TENCENT_SECRET_ID / TENCENT_SECRET_KEY 为空（DNS-01 验证必须）"

  if [ "$MANAGE_ORIGIN" = "1" ]; then
    [ -n "$container" ] || die "MANAGE_ORIGIN=1 但没有 nginx 容器"
    [ -n "$targets" ]   || die "MANAGE_ORIGIN=1 但没有证书落盘路径"
    write_resolved "$container" "$targets"
  else
    # 显式写空，免得上一轮留下的 resolved.env 把源站卷进来
    write_resolved "" ""
    [ -n "${CDN_DOMAINS:-}" ] || die "MANAGE_ORIGIN=0 且 CDN_DOMAINS 为空，没有事情可做"
    log "[i] MANAGE_ORIGIN=0：不动源站证书，只下发 CDN（$CDN_DOMAINS）"
  fi

  install_acme
  ensure_dns_plugin
  issue_cert
  harden_acme_secrets
  install_to_staging
  ensure_cron

  log "[i] 执行一次续签后处理（分发证书 + reload nginx + 下发 CDN）..."
  if bash "$HOOK_DIR/renew-hook.sh"; then
    ok "证书已生效"
  else
    rc=$?
    warn "续签后处理退出码 $rc，看 $STATE_DIR/renew.log"
    [ "$rc" -eq 2 ] || exit "$rc"
  fi

  echo ""
  log "===== 完成。线上证书现状 ====="
  for d in "$APEX_DOMAIN" "www.$APEX_DOMAIN" "admin.$APEX_DOMAIN" ${CDN_DOMAINS:-}; do
    probe_live_cert "$d"
  done
  log "CDN 配置下发到边缘要几分钟，之后 https://${CDN_DOMAINS:-cdn} 才会用上新证书"
}

# 被测试 source 时不自动跑主流程
if [ "${BASH_SOURCE[0]}" = "$0" ]; then
  main "$@"
fi
