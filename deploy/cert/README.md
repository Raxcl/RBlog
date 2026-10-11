# RBlog 证书自动化

一套脚本，把「每 3 个月手动给 CDN 换证书」变成完全不用管。

## 它解决什么

只有 `cdn.raxcl.cn` 需要这套东西。2026-10-11 上服务器实测，四个域名的真实情况是：

| 域名 | 解析到 | HTTPS 终止在 | 证书 | 到期 |
|---|---|---|---|---|
| `raxcl.cn` + `www` | 42.194.225.242 | 服务器 nginx/1.27.2 容器 | Let's Encrypt | 2027-01-08 |
| `admin.raxcl.cn` | 42.194.225.242 | 服务器 nginx/1.27.2 容器 | Let's Encrypt | 2027-01-08 |
| `cdn.raxcl.cn` | CNAME → `cdn.dnsv1.com.cn` | **腾讯云 CDN 边缘** | Let's Encrypt | 2027-01-09 |
| `api.raxcl.cn` | 无解析记录 | — | — | 已弃用，README 里提到但别当活域名 |

**源站那三个域名早就自动化了**，而且是不依赖任何云凭据的那种：`~/.acme.sh/raxcl.cn_ecc`
和 `~/.acme.sh/admin.raxcl.cn_ecc` 各有一套 HTTP-01 + webroot 续签（webroot 分别是
`/mnt/raxcl/blog_view` 和 `/mnt/raxcl/blog_admin`），reloadcmd 是
`docker exec nginx nginx -s reload`，跟着 acme.sh 的 cron `12 14 * * *` 走。

真正没人管的只有 `cdn.raxcl.cn`：它的证书在 CDN 边缘，换证书必须调腾讯云 API。
原来那张 TrustAsia DV 证书 **2026-08-10 就过期了**，过期两个月没人发现，
`curl https://cdn.raxcl.cn/` 直接 exit 35 —— 而首页 HTML 里确实引用了这个域名。

所以这套脚本的定位是**只管 CDN**（`MANAGE_ORIGIN=0`）：

- acme.sh 用 **DNSPod DNS-01** 给 `cdn.raxcl.cn` 单签一张 Let's Encrypt 证书
- 通过腾讯云 CDN API 下发到边缘，回读确认生效
- acme.sh 自带 cron 每 60 天续一次，续完自动跑钩子重新下发
- 源站证书一个字节都不碰

### 为什么不干脆把源站也接管过来

一张 `*.raxcl.cn` 通配符证书理论上能覆盖全部域名，一开始就是这么设计的。上服务器
`--check` 之后放弃了，两个理由：

1. **会顶掉正在工作的东西。** acme.sh 按主域名定位证书目录，`CERT_PRIMARY=raxcl.cn`
   和现有的 HTTP-01 证书撞名，`--issue` 会把 `Le_Webroot` 从 webroot 路径就地改写成
   `dns_tencent`，那套续签当场就废了，而且现场看不出是谁干的。脚本里有
   `guard_no_hijack` 拦这种情况，但别指望它，源头就别配错。
2. **接管是降级不是升级。** 源站现在的续签链路零外部依赖；换成 DNS-01 之后，
   API 密钥一挂、权限一改、腾讯云一抽风，源站证书就跟着续签不了。用一个更脆弱的
   链路去替换一个更结实的链路，没有道理。

真要接管，把 `MANAGE_ORIGIN=1` 并给 `CERT_PRIMARY` 换一个不撞名的值。默认不建议。

### 为什么不用通配符

`CERT_PRIMARY=cdn.raxcl.cn` 再配 `CERT_SANS='*.raxcl.cn'` 会被 Let's Encrypt 直接拒单：

```
400 "Domain name cdn.raxcl.cn" is redundant with a wildcard domain in the same request
```

而且这个错要等 DNS-01 跑完一轮才返回，光看日志很难想到是配置自相矛盾。
`check_san_list` 现在会在本地拦住，`cert.env.example` 里也写明了别再往 `CERT_SANS`
加通配符。

## 首次启用

服务器上没装 git，仓库是 scp 上去的，放在 `/root/rblog-cert`：

```bash
# 从本地推上去（在本地仓库根目录执行）
scp deploy/cert/{setup-cert.sh,renew-hook.sh,push-to-cdn.py,cert.env.example} \
    rblog:/root/rblog-cert/
```

```bash
# 1. 建配置（放在 HOME 下，不在仓库目录里，天然不会被 commit）
mkdir -p ~/.rblog-cert
cp /root/rblog-cert/cert.env.example ~/.rblog-cert/cert.env
chmod 600 ~/.rblog-cert/cert.env
vi ~/.rblog-cert/cert.env      # 填 TENCENT_SECRET_ID / TENCENT_SECRET_KEY
```

```bash
# 2. 只读体检，不改任何东西。先看这份输出对不对再往下走
cd /root/rblog-cert
bash setup-cert.sh --check
```

`--check` 会打印：运行模式、域名列表有没有自相矛盾、nginx 容器、证书落盘路径、
acme.sh 与 DNS 插件是否就位、`account.conf` 权限、同机上其它 acme.sh 证书（撞名会标出来）、
凭据是否齐，以及各域名的线上实际证书和剩余天数。

```bash
# 3. 确认无误后正式跑
bash setup-cert.sh
```

```bash
# 4. 验签（CDN 边缘下发要一两分钟，别急）
openssl s_client -connect cdn.raxcl.cn:443 -servername cdn.raxcl.cn </dev/null 2>/dev/null \
  | openssl x509 -noout -issuer -subject -enddate
```

## 日常

**不用做任何事。** acme.sh 的 cron（`12 14 * * *`）每天跑一次 `--cron`，
`cdn.raxcl.cn` 到期前自动续，续完自动执行 `~/.rblog-cert/hook/renew-hook.sh` 重新下发 CDN。

想确认还活着：

```bash
bash setup-cert.sh --check                       # 只读体检
crontab -l | grep acme                           # cron 还在不在
tail -50 ~/.rblog-cert/renew.log                 # 上次续签干了什么
~/.acme.sh/acme.sh --cron --home ~/.acme.sh      # 手动跑一次，没到期会打印 Skipped
```

幂等，随时可以重跑 `bash setup-cert.sh`。证书还很新的时候它会打印
`= 剩余 N 天 > 21 天，不重签` 然后跳过。

## 文件

| 文件 | 作用 |
|---|---|
| `cert.env.example` | 配置模板，复制成 `~/.rblog-cert/cert.env` 用 |
| `setup-cert.sh` | 主脚本：探测 → 装 acme.sh + DNS 插件 → 签发 → 注册钩子 → 体检 |
| `renew-hook.sh` | 续签后处理：校验 SAN → 分发证书 → nginx -t + reload → 下发 CDN |
| `push-to-cdn.py` | 腾讯云 CDN 证书下发，纯标准库，服务器上不用 pip install |
| `test_cert_scripts.py` | 离线测试，不联网不要凭据不要真 docker |

跑测试（79 条，需要 openssl，其余都不需要 —— 不联网、不要凭据、不要真 docker）：

```bash
python3 deploy/cert/test_cert_scripts.py
```

覆盖的几块：

- **签名**：拿腾讯云《签名方法 v3》文档里 `DescribeInstances` 算例的中间摘要做基准，
  断言本实现算出的 CanonicalRequest 哈希等于官方值 `7019a55b...`。签名写错在线上只会
  得到一句 `AuthFailure.SignatureFailure`，极难定位，所以在离线阶段钉死。
- **CDN 下发**：`Https` payload 必须每次都显式带 `Switch='on'`（整块替换语义，漏了会
  把线上 HTTPS 关掉）；不许顺手改 `ForceRedirect`；`wait_deploy` 靠 CertId + SslStatus
  判定生效，且幂等重推时如实说"没变过"而不是白等满超时。
- **域名列表闸门**：复现 CA 拒单的那组配置、通配符只覆盖一层标签、`apex + *.apex`
  这种合法组合不能误拦、出厂默认配置本身必须过得去。
- **撞名闸门**：撞上 HTTP-01 证书必须拒绝，`cdn.raxcl.cn` 这种全新主域名放行。
- **残缺 acme.sh**：缺 `dnsapi/dns_tencent.sh` 时能补装，下回来的是 HTML 404 页时
  必须拒绝落盘；`account.conf` 全局可读时收紧到 600，已经是 600 就安静跳过。
- **nginx 探测**：用假 docker 喂 `nginx -T` / `docker inspect` 的固定输出，验证容器识别、
  证书路径配对、引号剥离、同行多指令、注释行忽略、容器路径→宿主机路径的最长前缀映射。
- **续签钩子**：用 openssl 现签自签证书端到端跑，验证正常分发 + reload；SAN 不覆盖时
  **拒绝覆写**且不 reload；`nginx -t` 失败时回滚到 `.prev` 且不 reload；
  `CERT_TARGETS` 为空时源站一个字节都不动。
- **OpenSSL 1.0.2 兼容**：线上是 CentOS 7 的 1.0.2k，没有 `x509 -ext`，SAN 提取必须
  有 `-text` 回退，否则闸门会把好证书也判成不合格。
- **Python 3.6 兼容**：`sys.stdout.reconfigure` 是 3.7 才有的，且不能在模块级改写
  `sys.stdout`（会污染用 `spec_from_file_location` 导入它的测试运行器）。
- **超时预算**：`--install-cert` 会同步跑 reloadcmd，外层超时必须大于钩子自己的
  `60 + 60 + 600` 预算，否则钩子还在正常干活就被掐死。
- **报告不回显凭据**：`--check` 的凭据段里 SecretKey 一个字符都不打（只报长度），
  SecretId 只留前 8 位 —— 它不是秘密，腾讯云控制台里本来就全量可见，留前缀是为了
  核对配的是哪把 key。这里曾经出过事故：写成 `${VAR:+已设置}${VAR:-[!!] 未设置}`，
  而 `:-` 只在变量为空/未设置时才替换成后备值，**变量已设置时它展开的就是变量本身**，
  于是"已设置"后面紧跟着完整的 SecretKey。`--check` 是排错时最常跑的命令、输出还常被
  重定向进日志，等于把云凭据持久化到一个没人当秘密看的地方。测试里留了一条用旧写法
  复现泄露的用例，防止有人嫌新写法啰嗦又改回去。
  顺带：acme.sh 证书清单以「目录里有没有同名的 `<主域名>.conf`」为判据，
  `dnsapi` / `ca` / `deploy` 这些程序目录不会被误列成证书（这份清单是用来看有没有撞名的，
  噪音多了真问题反而容易被划过去）。
- **行尾**：所有文件必须是 LF，CRLF 在服务器上会报 `$'\r': command not found`。

脚本运行时的状态都在 `~/.rblog-cert/`（仓库外，不会被 git 碰到）：

```
~/.rblog-cert/
├── cert.env       你填的配置（600 权限）
├── resolved.env   setup-cert.sh 探测生成的容器名 + 证书路径
├── live/          acme.sh 装出来的证书中转站
├── hook/          renew-hook.sh + push-to-cdn.py 的副本（acme.sh 记的是这个绝对路径）
├── setup.log      安装/签发日志
└── renew.log      每次续签的处理日志
```

钩子放在 `~/.rblog-cert/hook/` 而不是仓库里，是因为 acme.sh 会把 `--reloadcmd`
的绝对路径记进它自己的配置；仓库目录被挪动或改名都不影响续签。

## 排错

**签发失败，日志里 `Cannot find DNS API hook for: dns_tencent`**
acme.sh 是残缺安装。线上那份就是"只拷了 acme.sh 一个文件"式的，整个 `dnsapi/`
目录都不存在 —— HTTP-01 webroot 用不到插件，所以一直没人发现。缺插件时 acme.sh
不会明确报错，而是叫你手动加 TXT 记录然后以退出码 3 失败。
`setup-cert.sh` 现在会自动从 gitee 补装（`raw.githubusercontent.com` 在这台机器上不通）。

**签发失败，日志里 `is redundant with a wildcard domain in the same request`**
`CERT_SANS` 里加了覆盖 `CERT_PRIMARY` 的通配符。去掉即可；`check_san_list` 现在会在
本地拦住，正常不该再看到这个错。

**签发失败，日志里 `AuthFailure.UnauthorizedOperation` 或 DNSPod 相关报错**
密钥没有解析记录权限。给它加 `QcloudDNSPodFullAccess`，下发 CDN 还要 `QcloudCDNFullAccess`。

**签发失败，日志里 `You don't specify tencent api SecretId and SecretKey yet`**
`TENCENT_SECRET_ID` / `TENCENT_SECRET_KEY` 没填，或者 cert.env 没被读到。
注意 acme.sh 的 `dns_tencent` 插件读的是 `Tencent_SecretId` / `Tencent_SecretKey`
（首字母大写），`setup-cert.sh` 会自己转换，手工调 acme.sh 时别写成全大写。

**`--check` 报「没探测到 nginx 容器」**
在 cert.env 里手写 `NGINX_CONTAINER=<容器名>`。容器列表看 `docker ps`。
只在 `MANAGE_ORIGIN=1` 时才需要。

**`--check` 报「不在任何挂载里」**
nginx 的证书路径是打进镜像的，没有 volume 挂载，宿主机写不进去。
在 docker-compose 里给它加一条挂载，或者在 cert.env 里手写
`CERT_TARGETS='<宿主机cert路径>:<宿主机key路径>'`。同样只在 `MANAGE_ORIGIN=1` 时才需要。

**CDN 还是旧证书**
`renew.log` 里找 CDN 那段的报错。手动补跑：

```bash
set -a; . ~/.rblog-cert/cert.env; set +a
python3 ~/.rblog-cert/hook/push-to-cdn.py \
  --cert ~/.rblog-cert/live/fullchain.cer \
  --key  ~/.rblog-cert/live/privkey.key \
  --domains cdn.raxcl.cn
# 只想看现状不改配置，加 --check-only
```

**边缘 https 报证书不匹配 / 回落到 `*.cdn.myqcloud.com`**
说明 CDN 上的 `Https.Switch` 被关掉了。`--check-only` 能看到当前状态。
`push-to-cdn.py` 现在每次下发都显式带 `Switch='on'`，并在下发后回读确认，
确认不到会直接抛错而不是报成功 —— 带着 HTTPS 关闭的状态报成功比失败糟得多。

**续签钩子跑了但 nginx 没生效**
只在 `MANAGE_ORIGIN=1` 时相关。钩子会先 `nginx -t` 校验，失败就自动回滚到 `.prev`
备份并保留旧证书继续服务，然后以非 0 退出。看 `renew.log` 里 `nginx -t` 的输出。

## 安全

这个仓库是**公开的** `github.com/Raxcl/RBlog`。

- `cert.env` 放在 `~/.rblog-cert/`，不在仓库目录里；仓库内的 `deploy/cert/cert.env`
  也已进 `.gitignore`。**永远不要 commit 真实密钥** —— SecretKey 泄露等于整个云账号沦陷。
- 凭据在服务器上存了**两份**：`~/.rblog-cert/cert.env`（给 push-to-cdn.py 用）和
  `~/.acme.sh/account.conf`（acme.sh 自己存的，cron 续签全靠它）。两个都要 600，
  `setup-cert.sh` 每次跑都会检查并收紧 `account.conf`，`--check` 也会报告它的权限。
- 建议单独建一个最小权限子账号，只给 `QcloudDNSPodFullAccess` + `QcloudCDNFullAccess`。
  当前复用的是 COS 那对密钥，权限偏大。
- 私钥中转目录 `~/.rblog-cert/live/privkey.key` 是 600，分发到 nginx 时也用
  `install -m 600`，不会把私钥写成全局可读。
- `--check` 的报告**不回显凭据**：SecretKey 只报长度、SecretId 只留前 8 位。
  排错时最常跑的就是 `--check`，输出还常被重定向进日志，凭据一旦打进去就等于持久化
  到一个没人当秘密看的地方。

顺带核实了一件跟证书无关的事：`deploy/.env` 是**被 git 跟踪的**，而仓库是公开的。
2026-10-11 实际核对过，**不是活跃泄露**：

- `TOKEN_SECRETKEY` 的值以 `please-` 开头，是占位符，不是真密钥。
- `MYSQL_ROOT_PASSWORD` / `MYSQL_PASSWORD` 长得像真口令（18 位、大小写混合），
  但和线上跑着的**不是同一个** —— 比对方式是各自算 sha256 取前 16 位，
  仓库里是 `2ef16468f04b1675`，线上 mysql 容器里是 `20de1683f1700151`。
  线上那个容器已经 Up 23 个月，说明仓库里这对口令从来没上线过。

（核实方法留在这里，免得下次又凭"看着像真口令"误判一次：
`docker inspect <mysql容器> --format '{{range .Config.Env}}{{println .}}{{end}}'`
取到线上值后本地 `sha256sum` 比对，全程不打印明文。）

结论是**不用紧急处理**，但公开仓库里放一个长得像真口令的值仍是坏习惯 ——
万一同一个口令在别处复用过，那就是真泄露了。要收拾的话：把它换成明显的占位符
（如 `CHANGE_ME`），并 `git rm --cached deploy/.env` 加进 `.gitignore`，
只留 `deploy/explame.env.explame` 作模板。
