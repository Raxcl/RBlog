#!/usr/bin/env python3
"""把证书下发到腾讯云 CDN 加速域名。

只用 python 标准库（TC3-HMAC-SHA256 自己签），服务器上不需要 pip install 任何东西。
CDN 的 UpdateDomainConfig 接受 CertInfo 里直接塞证书内容 + 私钥，所以不必先上传到
SSL 证书服务换 CertId。

用法：
  python3 push-to-cdn.py --cert fullchain.cer --key privkey.key --domains cdn.raxcl.cn
  python3 push-to-cdn.py --check-only --domains cdn.raxcl.cn   # 只回读现状不改配置
"""
import argparse
import hashlib
import hmac
import io
import json
import os
import sys
import time
import urllib.error
import urllib.request
from datetime import datetime, timezone


CDN_HOST = 'cdn.tencentcloudapi.com'
CDN_SERVICE = 'cdn'
CDN_VERSION = '2018-06-06'


class TencentAPIError(RuntimeError):
    pass


# _hmac 用导入时绑定的真实 sha256；sign_request 走 hashlib.sha256，
# 这样单测能 patch hashlib.sha256 注入官方算例的 payload 摘要而不破坏 HMAC 链
_SHA256 = hashlib.sha256


def _hmac(key: bytes, msg: str) -> bytes:
    return hmac.new(key, msg.encode('utf-8'), _SHA256).digest()


def sign_request(secret_id, secret_key, action, body, timestamp):
    """按 TC3-HMAC-SHA256 算出请求头（纯函数，不发网络请求，可单测）。"""
    date = datetime.fromtimestamp(timestamp, timezone.utc).strftime('%Y-%m-%d')
    content_type = 'application/json; charset=utf-8'

    canonical_headers = (f'content-type:{content_type}\n'
                         f'host:{CDN_HOST}\n'
                         f'x-tc-action:{action.lower()}\n')
    signed_headers = 'content-type;host;x-tc-action'
    canonical_request = ('POST\n/\n\n'
                         f'{canonical_headers}\n{signed_headers}\n'
                         f'{hashlib.sha256(body.encode("utf-8")).hexdigest()}')

    credential_scope = f'{date}/{CDN_SERVICE}/tc3_request'
    string_to_sign = ('TC3-HMAC-SHA256\n'
                      f'{timestamp}\n{credential_scope}\n'
                      f'{hashlib.sha256(canonical_request.encode("utf-8")).hexdigest()}')

    signing_key = _hmac(_hmac(_hmac(('TC3' + secret_key).encode('utf-8'), date),
                              CDN_SERVICE), 'tc3_request')
    signature = hmac.new(signing_key, string_to_sign.encode('utf-8'),
                         hashlib.sha256).hexdigest()

    return {
        'Authorization': (f'TC3-HMAC-SHA256 Credential={secret_id}/{credential_scope}, '
                          f'SignedHeaders={signed_headers}, Signature={signature}'),
        'Content-Type': content_type,
        'Host': CDN_HOST,
        'X-TC-Action': action,
        'X-TC-Timestamp': str(timestamp),
        'X-TC-Version': CDN_VERSION,
    }


def tc3_cdn(secret_id, secret_key, action, payload, timeout=30):
    """调腾讯云 CDN API，返回 Response 体；出错抛 TencentAPIError。"""
    body = json.dumps(payload)
    headers = sign_request(secret_id, secret_key, action, body, int(time.time()))

    req = urllib.request.Request(f'https://{CDN_HOST}', data=body.encode('utf-8'),
                                 headers=headers, method='POST')
    try:
        with urllib.request.urlopen(req, timeout=timeout) as resp:
            out = json.loads(resp.read().decode('utf-8'))
    except urllib.error.HTTPError as e:
        detail = e.read().decode('utf-8', errors='replace')[:500]
        raise TencentAPIError(f'{action} -> HTTP {e.code}\n{detail}') from None
    except urllib.error.URLError as e:
        raise TencentAPIError(f'{action} -> 网络错误: {e.reason}') from None

    r = out.get('Response', {})
    if 'Error' in r:
        raise TencentAPIError(f"{action} 报错 {r['Error'].get('Code')}: "
                              f"{r['Error'].get('Message')} (RequestId={r.get('RequestId')})")
    return r


def describe_domain(secret_id, secret_key, domain):
    """回读单个加速域名的 HTTPS 相关配置。"""
    out = tc3_cdn(secret_id, secret_key, 'DescribeDomainsConfig', {
        'Filters': [{'Name': 'domain', 'Value': [domain]}],
        'Offset': 0, 'Limit': 1})
    domains = out.get('Domains') or []
    if not domains:
        raise TencentAPIError(f'CDN 上找不到加速域名 {domain}（域名不在这个账号下？）')
    return domains[0]


def summarize(domain, cfg):
    """把回读到的配置压成一行人类可读的状态。"""
    billing = (cfg.get('HttpsBilling') or {}).get('Switch')
    https = cfg.get('Https') or {}
    cert = https.get('CertInfo') or {}
    redirect = (cfg.get('ForceRedirect') or {}).get('Switch')
    return (f'{domain}: HTTPS服务总闸={billing} HTTPS={https.get("Switch")} '
            f'强制跳转={redirect} CertId={cert.get("CertId") or "-"} '
            f'备注={cert.get("Remark") or cert.get("CertName") or "-"} '
            f'到期={cert.get("CertExpiration") or "-"}')


def deploy_cert(secret_id, secret_key, domain, cert_pem, key_pem, cert_name):
    """下发证书到指定加速域名，返回下发前的 CertId 供生效确认比对。"""
    cfg = describe_domain(secret_id, secret_key, domain)
    print('  ' + summarize(domain, cfg))
    prev_cert_id = ((cfg.get('Https') or {}).get('CertInfo') or {}).get('CertId')

    # HTTPS 服务总闸（HttpsBilling）不开的话，边缘对 https 请求一律返回 514，
    # 光设 Https.Switch + 证书没用
    if (cfg.get('HttpsBilling') or {}).get('Switch') != 'on':
        print('  [改] 开 HTTPS 服务总闸 HttpsBilling ...', flush=True)
        tc3_cdn(secret_id, secret_key, 'UpdateDomainConfig',
                {'Domain': domain, 'HttpsBilling': {'Switch': 'on'}})
        time.sleep(2)

    # Https 是整块替换，不是按字段合并：只传 CertInfo 会把 Switch 重置成 off，
    # CertId 跟着一起清空，边缘立刻回落到 *.cdn.myqcloud.com 的默认证书 —— 站点当场就废。
    # 所以 Switch 每次都要显式带 'on'，不管原来是什么状态。
    print('  [改] 下发新证书（Https.Switch 一并置 on）...', flush=True)
    tc3_cdn(secret_id, secret_key, 'UpdateDomainConfig', {
        'Domain': domain,
        'Https': {'Switch': 'on',
                  'CertInfo': {'CertName': cert_name,
                               'Certificate': cert_pem, 'PrivateKey': key_pem}}})

    # 配置写下去是异步的，回读确认 Switch 没被意外关掉。带着 HTTPS=off 报告成功
    # 比直接失败糟得多。
    for waited in range(12):
        time.sleep(5)
        https = describe_domain(secret_id, secret_key, domain).get('Https') or {}
        if https.get('Switch') == 'on':
            print(f'  [好] Https.Switch=on 已确认（{(waited + 1) * 5}s）')
            return prev_cert_id
        print(f'  ... 等 Https.Switch 变 on，当前 {https.get("Switch")}'
              f'（{(waited + 1) * 5}s）', flush=True)
    raise TencentAPIError(
        f'{domain} 下发后 Https.Switch 仍不是 on，去控制台确认 HTTPS 配置')


def wait_deploy(secret_id, secret_key, domain, prev_cert_id, timeout=180, interval=10,
                grace=30):
    """轮询回读，直到确认新证书部署到位。

    判定用 CertId + SslStatus，不能用我们传进去的 CertName：腾讯云会把它覆写成域名
    本身，Remark 也是域名，那样条件永远不成立，每次都白等满超时再误报一次"没生效"。

    另外内容完全相同的证书重推时，腾讯云会复用同一个 CertId 且 SslStatus 一直是
    deployed —— 那不是失败，边缘挂的本来就是这张证书，等过 grace 秒就如实说明。"""
    t0 = time.monotonic()
    saw_deploying = False
    while time.monotonic() - t0 < timeout:
        https = describe_domain(secret_id, secret_key, domain).get('Https') or {}
        cert = https.get('CertInfo') or {}
        cid, status = cert.get('CertId'), https.get('SslStatus')
        elapsed = int(time.monotonic() - t0)
        expire = cert.get('ExpireTime') or '-'
        if cid and status == 'deployed' and cid != prev_cert_id:
            print(f'  [好] 边缘已生效（{elapsed}s）CertId={cid} 到期={expire}')
            return True
        if cid and status != 'deployed':
            saw_deploying = True
        elif saw_deploying and cid and status == 'deployed':
            print(f'  [好] 边缘已生效（{elapsed}s）CertId={cid} 到期={expire}')
            return True
        elif cid and status == 'deployed' and elapsed >= grace:
            print(f'  [=] CertId 和 SslStatus 在 {elapsed}s 内都没变过'
                  f'（{cid} / deployed，到期={expire}），边缘挂的就是这张证书，视为已生效')
            return True
        print(f'  ... 等待下发中（{elapsed}s，CertId={cid or "-"} '
              f'SslStatus={status or "-"}）', flush=True)
        time.sleep(interval)
    print(f'  [!] {timeout}s 内没等到边缘确认，稍后用 --check-only 复核', flush=True)
    return False


def main():
    p = argparse.ArgumentParser(description='下发证书到腾讯云 CDN')
    p.add_argument('--cert', help='fullchain 证书文件路径')
    p.add_argument('--key', help='私钥文件路径')
    p.add_argument('--domains', required=True, help='CDN 加速域名，逗号或空格分隔')
    p.add_argument('--cert-name', help='证书备注名，默认自动生成')
    p.add_argument('--check-only', action='store_true', help='只回读现状，不改任何配置')
    p.add_argument('--no-wait', action='store_true', help='下发后不轮询确认')
    args = p.parse_args()

    secret_id = os.environ.get('TENCENT_SECRET_ID') or os.environ.get('TENCENT_SecretId') or ''
    secret_key = os.environ.get('TENCENT_SECRET_KEY') or os.environ.get('TENCENT_SecretKey') or ''
    if not secret_id or not secret_key:
        sys.exit('缺少凭据：请设置环境变量 TENCENT_SECRET_ID / TENCENT_SECRET_KEY')

    domains = [d for d in args.domains.replace(',', ' ').split() if d]
    if not domains:
        sys.exit('--domains 为空')

    t0 = time.monotonic()

    if args.check_only:
        print('=== 只读回读 CDN HTTPS 现状 ===')
        for d in domains:
            try:
                print('  ' + summarize(d, describe_domain(secret_id, secret_key, d)))
            except TencentAPIError as e:
                print(f'  {d}: 读取失败 -> {e}')
        return

    if not args.cert or not args.key:
        sys.exit('下发证书需要 --cert 和 --key')
    with open(args.cert, encoding='utf-8') as f:
        cert_pem = f.read()
    with open(args.key, encoding='utf-8') as f:
        key_pem = f.read()
    if '-----BEGIN CERTIFICATE-----' not in cert_pem:
        sys.exit(f'{args.cert} 看起来不是 PEM 证书')
    if 'PRIVATE KEY-----' not in key_pem:
        sys.exit(f'{args.key} 看起来不是 PEM 私钥')

    cert_name = args.cert_name or f'rblog-acme-{int(time.time())}'
    print(f'=== 下发证书到 CDN（{len(domains)} 个域名）===')
    failed, unconfirmed = [], []
    for d in domains:
        print(f'\n--- {d} ---')
        try:
            prev_cert_id = deploy_cert(secret_id, secret_key, d, cert_pem, key_pem, cert_name)
            if not args.no_wait and not wait_deploy(secret_id, secret_key, d, prev_cert_id):
                unconfirmed.append(d)
        except TencentAPIError as e:
            print(f'  [X] {e}')
            failed.append(d)

    # 边缘确认超时不算失败：下发接口已成功、Https.Switch=on 也硬校验过了，
    # 传播延迟不是我们能左右的。如实说出来，但别让 renew-hook 因此报"CDN 下发失败"。
    tail = ''
    if failed:
        tail += ' 失败: ' + ', '.join(failed)
    if unconfirmed:
        tail += ' 已下发但边缘确认超时: ' + ', '.join(unconfirmed)
    print(f'\n完成，总耗时 {int(time.monotonic() - t0)} 秒。' + (tail or '全部成功'))
    sys.exit(1 if failed else 0)


def _force_utf8(stream):
    """reconfigure 是 3.7 才有的；服务器上是 CentOS 7 的 Python 3.6.8，而且 locale
    常常是 C，直接 print 中文会 UnicodeEncodeError，报错还盖住真正要下发的那条信息。"""
    if hasattr(stream, 'reconfigure'):
        stream.reconfigure(encoding='utf-8', errors='replace')
        return stream
    return io.TextIOWrapper(stream.buffer, encoding='utf-8',
                            errors='replace', line_buffering=True)


if __name__ == '__main__':
    sys.stdout = _force_utf8(sys.stdout)
    sys.stderr = _force_utf8(sys.stderr)
    main()
