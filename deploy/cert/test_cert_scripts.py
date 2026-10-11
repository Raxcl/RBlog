#!/usr/bin/env python3
"""deploy/cert 下脚本的离线测试。

    python3 deploy/cert/test_cert_scripts.py

不联网、不需要腾讯云凭据、不需要真 docker（用假的顶替）。
覆盖两块最容易写错又最难在线上排查的逻辑：
  1. TC3-HMAC-SHA256 签名（拿腾讯云官方算例的中间摘要做基准）
  2. setup-cert.sh 里的 nginx 容器探测 / 证书路径解析 / 容器路径→宿主机路径映射
"""
import hashlib
import hmac
import importlib.util
import io
import json
import os
import re
import shutil
import stat
import subprocess
import sys
import tempfile
import unittest
import urllib.error
import urllib.request
from unittest import mock

if hasattr(sys.stdout, 'reconfigure'):        # 3.7+；服务器上 3.6.8 没有
    sys.stdout.reconfigure(encoding='utf-8', errors='replace')

HERE = os.path.dirname(os.path.abspath(__file__))
SETUP_SH = os.path.join(HERE, 'setup-cert.sh')

_spec = importlib.util.spec_from_file_location('push_to_cdn', os.path.join(HERE, 'push-to-cdn.py'))
ptc = importlib.util.module_from_spec(_spec)
_spec.loader.exec_module(ptc)

# 腾讯云《签名方法 v3》文档里 DescribeInstances 的算例中间值
OFFICIAL_PAYLOAD_HASH = '35e9c5b0e3ae67532d3c9f17ead6c90222632e5b1ff7f6e89887f1398934f064'
OFFICIAL_CR_HASH = '7019a55be8395899b900fb5564e4200d984910f34794a27cb3fb7d10ff6a1e84'
OFFICIAL_TIMESTAMP = 1551113065
OFFICIAL_DATE = '2019-02-25'


def to_posix(p):
    """Windows 路径转成 git bash 认的形式，让测试在 Windows 和 Linux 都能跑。"""
    p = os.path.abspath(p).replace('\\', '/')
    if os.name == 'nt':
        m = re.match(r'^([A-Za-z]):/(.*)$', p)
        if m:
            return f'/{m.group(1).lower()}/{m.group(2)}'
    return p


class TestSigning(unittest.TestCase):

    def _sign_with_official_vector(self):
        """用官方算例的 payload 摘要驱动 sign_request，抓出它算的 StringToSign。"""
        captured, real_new, real_sha = [], hmac.new, hashlib.sha256
        calls = {'n': 0}

        def spy(key, msg=None, digestmod=None):
            captured.append(msg)
            return real_new(key, msg, digestmod)

        def fake_sha(data=b''):
            calls['n'] += 1
            if calls['n'] == 1:          # 第一次是 payload 摘要，换成官方算例的值
                class D:
                    @staticmethod
                    def hexdigest():
                        return OFFICIAL_PAYLOAD_HASH
                return D()
            return real_sha(data)

        with mock.patch.object(ptc, 'CDN_HOST', 'cvm.tencentcloudapi.com'), \
             mock.patch.object(ptc, 'CDN_SERVICE', 'cvm'), \
             mock.patch.object(hmac, 'new', spy), \
             mock.patch.object(hashlib, 'sha256', fake_sha):
            headers = ptc.sign_request('AKIDtest', 'secrettest', 'DescribeInstances',
                                       '{"Limit": 1}', OFFICIAL_TIMESTAMP)
        return headers, captured

    def test_canonical_request_hash_matches_official_vector(self):
        """最关键的一条：拼出来的 CanonicalRequest 哈希必须等于官方文档的值。"""
        _, captured = self._sign_with_official_vector()
        string_to_sign = captured[-1].decode('utf-8')   # 第 4 次 HMAC 的输入
        lines = string_to_sign.split('\n')
        self.assertEqual(lines[0], 'TC3-HMAC-SHA256')
        self.assertEqual(lines[1], str(OFFICIAL_TIMESTAMP))
        self.assertEqual(lines[2], f'{OFFICIAL_DATE}/cvm/tc3_request')
        self.assertEqual(lines[3], OFFICIAL_CR_HASH,
                         'CanonicalRequest 拼接结构与腾讯云官方算例不一致')

    def test_authorization_header_shape(self):
        headers, _ = self._sign_with_official_vector()
        auth = headers['Authorization']
        self.assertTrue(auth.startswith('TC3-HMAC-SHA256 Credential=AKIDtest/'))
        self.assertIn(f'/{OFFICIAL_DATE}/cvm/tc3_request, ', auth)
        self.assertIn('SignedHeaders=content-type;host;x-tc-action, ', auth)
        sig = auth.split('Signature=')[1]
        self.assertRegex(sig, r'^[0-9a-f]{64}$')

    def test_cdn_defaults(self):
        self.assertEqual(ptc.CDN_HOST, 'cdn.tencentcloudapi.com')
        self.assertEqual(ptc.CDN_SERVICE, 'cdn')
        self.assertEqual(ptc.CDN_VERSION, '2018-06-06')

    def test_action_header_case_and_signed_headers(self):
        """X-TC-Action 请求头保持原大小写；SignedHeaders 是固定三元组。

        Action 值在 CanonicalHeaders 里要小写，这点由
        test_canonical_request_hash_matches_official_vector 覆盖 —— 官方算例的
        CR 哈希包含 x-tc-action:describeinstances，能对上就说明小写化正确。
        """
        headers = ptc.sign_request('id', 'key', 'DescribeDomainsConfig', '{}', OFFICIAL_TIMESTAMP)
        self.assertEqual(headers['X-TC-Action'], 'DescribeDomainsConfig')
        self.assertEqual(headers['X-TC-Version'], '2018-06-06')
        self.assertIn('SignedHeaders=content-type;host;x-tc-action, ', headers['Authorization'])
        self.assertEqual(headers['Host'], ptc.CDN_HOST)

    def test_signature_depends_on_key_and_body(self):
        a = ptc.sign_request('id', 'key1', 'X', '{}', OFFICIAL_TIMESTAMP)['Authorization']
        b = ptc.sign_request('id', 'key2', 'X', '{}', OFFICIAL_TIMESTAMP)['Authorization']
        c = ptc.sign_request('id', 'key1', 'X', '{"a":1}', OFFICIAL_TIMESTAMP)['Authorization']
        self.assertNotEqual(a, b, '换 SecretKey 签名必须变')
        self.assertNotEqual(a, c, '换请求体签名必须变')


class TestTransport(unittest.TestCase):

    def _mock_resp(self, payload):
        m = mock.MagicMock()
        m.__enter__.return_value.read.return_value = json.dumps(payload).encode()
        return m

    def test_returns_response_body(self):
        with mock.patch.object(urllib.request, 'urlopen',
                               return_value=self._mock_resp({'Response': {'Domains': [{'Domain': 'cdn.raxcl.cn'}],
                                                                          'RequestId': 'r1'}})):
            out = ptc.tc3_cdn('id', 'key', 'DescribeDomainsConfig', {})
        self.assertEqual(out['Domains'][0]['Domain'], 'cdn.raxcl.cn')

    def test_raises_on_api_error(self):
        with mock.patch.object(urllib.request, 'urlopen',
                               return_value=self._mock_resp({'Response': {
                                   'Error': {'Code': 'AuthFailure.SignatureFailure', 'Message': '签名错误'},
                                   'RequestId': 'r2'}})):
            with self.assertRaises(ptc.TencentAPIError) as cm:
                ptc.tc3_cdn('id', 'key', 'DescribeDomainsConfig', {})
        self.assertIn('AuthFailure.SignatureFailure', str(cm.exception))

    def test_raises_on_http_error_with_body(self):
        err = urllib.error.HTTPError('https://cdn.tencentcloudapi.com', 400, 'Bad Request', {},
                                     io.BytesIO(b'{"Response":{"Error":{"Code":"InvalidParameter"}}}'))
        with mock.patch.object(urllib.request, 'urlopen', side_effect=err):
            with self.assertRaises(ptc.TencentAPIError):
                ptc.tc3_cdn('id', 'key', 'DescribeDomainsConfig', {})

    def test_raises_on_network_error(self):
        with mock.patch.object(urllib.request, 'urlopen',
                               side_effect=urllib.error.URLError('dns fail')):
            with self.assertRaises(ptc.TencentAPIError) as cm:
                ptc.tc3_cdn('id', 'key', 'DescribeDomainsConfig', {})
        self.assertIn('网络错误', str(cm.exception))

    def test_describe_domain_rejects_unknown_domain(self):
        with mock.patch.object(urllib.request, 'urlopen',
                               return_value=self._mock_resp({'Response': {'Domains': []}})):
            with self.assertRaises(ptc.TencentAPIError) as cm:
                ptc.describe_domain('id', 'key', 'not-exist.raxcl.cn')
        self.assertIn('找不到加速域名', str(cm.exception))


class TestSetupScriptSource(unittest.TestCase):
    """对 setup-cert.sh 源码做静态断言，守住几个写错了不会立刻报错的坑。"""

    @classmethod
    def setUpClass(cls):
        with open(SETUP_SH, encoding='utf-8') as f:
            cls.src = f.read()

    def test_acme_dns_tencent_env_var_names(self):
        """dns_tencent 插件只认 Tencent_SecretId / Tencent_SecretKey（首字母大写）。

        写成 TENCENT_SecretId 不会报语法错，只会在签发时被插件当成"凭据没配"而失败，
        排查起来很绕，所以在这里静态锁死。
        """
        self.assertIn('export Tencent_SecretId=', self.src)
        self.assertIn('export Tencent_SecretKey=', self.src)
        self.assertNotIn('TENCENT_SecretId', self.src)
        self.assertNotIn('TENCENT_SecretKey', self.src)

    def test_uses_letsencrypt_not_zerossl(self):
        """acme.sh 默认 CA 是 ZeroSSL（要额外注册邮箱），显式锁 Let's Encrypt。"""
        self.assertIn('ACME_CA=letsencrypt', self.src)
        self.assertIn('--server "$ACME_CA"', self.src)

    def test_main_guarded_when_sourced(self):
        self.assertIn('if [ "${BASH_SOURCE[0]}" = "$0" ]; then', self.src)

    def test_progress_log_goes_to_stderr(self):
        """log() 必须走 stderr，否则 $(resolve_targets) 会把日志当证书路径捕获。"""
        self.assertRegex(self.src, r'log\(\)\s*\{[^\n]*>&2')

    def test_install_cert_timeout_exceeds_hook_budget(self):
        """--install-cert 会同步跑一遍 reloadcmd（renew-hook.sh）。外层超时若小于钩子
        自己的预算，钩子还在正常干活就被掐死，报一个跟真实原因无关的失败 —— 线上就
        这么翻过车（外层写死 120s，钩子最坏 60+60+600=720s）。"""
        with open(os.path.join(HERE, 'renew-hook.sh'), encoding='utf-8') as f:
            hook = f.read()

        def const(src, name):
            m = re.search(rf'(?m)^{name}=(\d+)', src)
            self.assertIsNotNone(m, f'{name} 没定义')
            return int(m.group(1))

        budget = sum(const(hook, n) for n in
                     ('NGINX_TEST_TIMEOUT', 'NGINX_RELOAD_TIMEOUT', 'CDN_PUSH_TIMEOUT'))
        outer = const(self.src, 'INSTALL_CERT_TIMEOUT')
        self.assertGreater(outer, budget,
                           f'INSTALL_CERT_TIMEOUT={outer} 必须大于钩子预算 {budget}')
        self.assertRegex(self.src, r'timeout "\$INSTALL_CERT_TIMEOUT" "\$ACME_SH" --install-cert',
                         '定义了 INSTALL_CERT_TIMEOUT 却没用在 --install-cert 上')


class TestSummarize(unittest.TestCase):

    def test_full_config(self):
        cfg = {'HttpsBilling': {'Switch': 'on'},
               'Https': {'Switch': 'on', 'CertInfo': {'CertId': 'abc', 'CertName': 'n',
                                                      'CertExpiration': '2027-01-08'}},
               'ForceRedirect': {'Switch': 'on'}}
        line = ptc.summarize('cdn.raxcl.cn', cfg)
        self.assertIn('cdn.raxcl.cn', line)
        self.assertIn('HTTPS服务总闸=on', line)
        self.assertIn('CertId=abc', line)
        self.assertIn('到期=2027-01-08', line)

    def test_empty_config_does_not_crash(self):
        line = ptc.summarize('d', {})
        self.assertIn('HTTPS服务总闸=None', line)
        self.assertIn('CertId=-', line)


class TestCdnHttpsSwitch(unittest.TestCase):
    """两个线上真实事故。

    1. 腾讯云的 Https 是整块替换，不是按字段合并。原代码在 HTTPS 已开着时只发
       CertInfo 不发 Switch，CDN 就把 Switch 重置成 off、CertId 一起清空，边缘立刻
       回落到 *.cdn.myqcloud.com 默认证书 —— https://cdn.raxcl.cn 当场失效。
    2. wait_deploy 拿自定义 cert_name 比对回读的 CertName/Remark，但腾讯云会把这两个
       字段覆写成域名本身，条件永远不成立，每次续签都白等满超时再误报一次"没生效"。
    """

    DOMAIN = 'cdn.raxcl.cn'
    CERT_NAME = 'rblog-acme-1791708480'

    def cfg(self, switch='on', cert_id='OLD123', ssl='deployed', billing='on'):
        return {'HttpsBilling': {'Switch': billing},
                'ForceRedirect': {'Switch': 'off'},
                'Https': {'Switch': switch, 'SslStatus': ssl,
                          'CertInfo': {'CertId': cert_id,
                                       # 腾讯云把这两个字段覆写成域名，不是我们传的值
                                       'CertName': self.DOMAIN,
                                       'Remark': self.DOMAIN,
                                       'ExpireTime': '2027-01-09 15:49:26'}}}

    def stub(self, states):
        """按顺序返回 states 里的配置（用尽后停在最后一个），并记录所有 API 调用。"""
        calls, idx = [], [0]

        def fake_tc3(sid, skey, action, payload):
            calls.append((action, payload))
            if action == 'DescribeDomainsConfig':
                cfg = states[min(idx[0], len(states) - 1)]
                idx[0] += 1
                return {'Domains': [cfg]}
            return {}

        return fake_tc3, calls

    def https_updates(self, calls):
        return [p for a, p in calls
                if a == 'UpdateDomainConfig' and 'Https' in p]

    def test_switch_on_sent_even_when_https_already_on(self):
        """复现事故 1 的条件：HTTPS 本来就开着（旧代码此时会省掉 Switch）。"""
        fake, calls = self.stub([self.cfg(cert_id='OLD123'), self.cfg(cert_id='NEW999')])
        with mock.patch.object(ptc, 'tc3_cdn', side_effect=fake), \
             mock.patch.object(ptc.time, 'sleep', lambda *a, **k: None):
            prev = ptc.deploy_cert('sid', 'skey', self.DOMAIN, 'CERT', 'KEY', self.CERT_NAME)
        updates = self.https_updates(calls)
        self.assertEqual(len(updates), 1, f'应该只发一次 Https 更新: {calls}')
        self.assertEqual(updates[0]['Https'].get('Switch'), 'on',
                         'Https payload 不带 Switch 会被整块替换成 off，线上 HTTPS 直接关掉')
        self.assertEqual(prev, 'OLD123', '要把旧 CertId 返回给 wait_deploy 做生效比对')

    def test_raises_when_switch_stays_off(self):
        """回读一直是 off 就必须抛错 —— 带着 HTTPS 关闭的状态报成功比失败糟得多。"""
        fake, _ = self.stub([self.cfg(cert_id='OLD123')]
                            + [self.cfg(cert_id='NEW999', switch='off')] * 15)
        with mock.patch.object(ptc, 'tc3_cdn', side_effect=fake), \
             mock.patch.object(ptc.time, 'sleep', lambda *a, **k: None):
            with self.assertRaises(ptc.TencentAPIError):
                ptc.deploy_cert('sid', 'skey', self.DOMAIN, 'CERT', 'KEY', self.CERT_NAME)

    def test_never_touches_force_redirect(self):
        """开 HTTP->HTTPS 强制跳转是用户没要求的行为变更，线上原本就是 off。"""
        fake, calls = self.stub([self.cfg(cert_id='OLD123'), self.cfg(cert_id='NEW999')])
        with mock.patch.object(ptc, 'tc3_cdn', side_effect=fake), \
             mock.patch.object(ptc.time, 'sleep', lambda *a, **k: None):
            ptc.deploy_cert('sid', 'skey', self.DOMAIN, 'CERT', 'KEY', self.CERT_NAME)
        touched = [p for a, p in calls
                   if a == 'UpdateDomainConfig' and 'ForceRedirect' in p]
        self.assertEqual(touched, [], '不该顺手改 ForceRedirect')

    def test_wait_deploy_confirms_via_certid_not_certname(self):
        """复现事故 2：CertName/Remark 都是域名，跟我们传的 cert_name 永不相等。"""
        fake, _ = self.stub([self.cfg(cert_id='NEW999')])
        with mock.patch.object(ptc, 'tc3_cdn', side_effect=fake), \
             mock.patch.object(ptc.time, 'sleep', lambda *a, **k: None):
            self.assertTrue(ptc.wait_deploy('sid', 'skey', self.DOMAIN, 'OLD123'))

    def test_wait_deploy_keeps_polling_while_certid_unchanged(self):
        fake, calls = self.stub([self.cfg(cert_id='OLD123'),
                                 self.cfg(cert_id='OLD123'),
                                 self.cfg(cert_id='NEW999')])
        with mock.patch.object(ptc, 'tc3_cdn', side_effect=fake), \
             mock.patch.object(ptc.time, 'sleep', lambda *a, **k: None):
            self.assertTrue(ptc.wait_deploy('sid', 'skey', self.DOMAIN, 'OLD123'))
        reads = sum(1 for a, _ in calls if a == 'DescribeDomainsConfig')
        self.assertEqual(reads, 3, 'CertId 没变就该继续等，别提前宣布生效')

    def test_wait_deploy_gives_up_without_lying(self):
        fake, _ = self.stub([self.cfg(cert_id='OLD123')])
        with mock.patch.object(ptc, 'tc3_cdn', side_effect=fake), \
             mock.patch.object(ptc.time, 'sleep', lambda *a, **k: None):
            self.assertFalse(ptc.wait_deploy('sid', 'skey', self.DOMAIN, 'OLD123',
                                             timeout=0.05, interval=0.01))

    def test_wait_deploy_accepts_identical_cert_repush(self):
        """实测：内容相同的证书重推，腾讯云复用同一个 CertId 且 SslStatus 一直是
        deployed。这时"CertId 必须变化"永远不成立，但它不是失败 —— 边缘挂的就是这张
        证书。grace 过后要如实认定已生效，而不是白等满超时再误报。"""
        fake, _ = self.stub([self.cfg(cert_id='SAME1')])
        buf = io.StringIO()
        with mock.patch.object(ptc, 'tc3_cdn', side_effect=fake), \
             mock.patch.object(ptc.time, 'sleep', lambda *a, **k: None), \
             mock.patch('sys.stdout', buf):
            ok = ptc.wait_deploy('sid', 'skey', self.DOMAIN, 'SAME1', grace=0)
        self.assertTrue(ok, '幂等重推不该判成失败')
        self.assertIn('[=]', buf.getvalue(), '要说清是"没变过所以视为生效"，别谎称新部署')

    def test_wait_deploy_watches_deploying_then_deployed(self):
        """真续签时 CertId 可能不变（同一张证书重新部署），但状态会走 deploying。"""
        fake, _ = self.stub([self.cfg(cert_id='SAME1', ssl='deploying'),
                             self.cfg(cert_id='SAME1', ssl='deployed')])
        with mock.patch.object(ptc, 'tc3_cdn', side_effect=fake), \
             mock.patch.object(ptc.time, 'sleep', lambda *a, **k: None):
            self.assertTrue(ptc.wait_deploy('sid', 'skey', self.DOMAIN, 'SAME1'))


# ──────────────── setup-cert.sh 的 bash 逻辑（用假 docker 顶替）────────────────
FAKE_DOCKER = '''#!/usr/bin/env bash
case "$1" in
  ps)      cat "$FAKE_PS" ;;
  exec)    cat "$FAKE_NGINX_T" ;;
  inspect) cat "$FAKE_MOUNTS" ;;
  *)       exit 0 ;;
esac
'''

PS_FIXTURE = '''rblog-nginx|nginx:1.27.2|0.0.0.0:80->80/tcp, 0.0.0.0:443->443/tcp
rblog-view|raxcl/rblog-view:latest|0.0.0.0:8081->80/tcp
rblog-api|raxcl/rblog-api:latest|0.0.0.0:8090->8090/tcp
'''

# 故意混进：多余空格、双引号包裹的路径、非 ssl 的干扰行
NGINX_T_FIXTURE = '''# configuration file /etc/nginx/nginx.conf:
events { worker_connections 1024; }
http {
    # configuration file /etc/nginx/conf.d/raxcl.cn.conf:
    server {
        listen 443 ssl;
        server_name raxcl.cn www.raxcl.cn;
        ssl_certificate /etc/nginx/certs/raxcl.cn/fullchain.cer;
        ssl_certificate_key /etc/nginx/certs/raxcl.cn/privkey.key;
        ssl_protocols TLSv1.2 TLSv1.3;
    }
    # configuration file /etc/nginx/conf.d/admin.conf:
    server {
        listen 443 ssl;
        server_name admin.raxcl.cn;
        ssl_certificate     "/etc/nginx/certs/admin.raxcl.cn/fullchain.cer";
        ssl_certificate_key "/etc/nginx/certs/admin.raxcl.cn/privkey.key";
    }
    server {
        listen 80;
        server_name raxcl.cn;
        return 301 https://$host$request_uri;
    }
}
'''

MOUNTS_FIXTURE = ('/www/wwwroot/RBlog/deploy/nginx/certs\t/etc/nginx/certs\n'
                  '/www/wwwroot/RBlog/deploy/nginx/conf.d\t/etc/nginx/conf.d\n')


class TestNginxDiscovery(unittest.TestCase):

    def setUp(self):
        self.tmp = tempfile.mkdtemp(prefix='rblog-cert-test-')
        self.fakebin = os.path.join(self.tmp, 'bin')
        self.state = os.path.join(self.tmp, 'state')
        os.makedirs(self.fakebin)
        os.makedirs(self.state)

        def write(name, content):
            p = os.path.join(self.tmp, name)
            with open(p, 'w', encoding='utf-8', newline='\n') as f:
                f.write(content)
            return p

        self.ps = write('ps.txt', PS_FIXTURE)
        self.nginx_t = write('nginx_t.txt', NGINX_T_FIXTURE)
        self.mounts = write('mounts.txt', MOUNTS_FIXTURE)

        docker = os.path.join(self.fakebin, 'docker')
        with open(docker, 'w', encoding='utf-8', newline='\n') as f:
            f.write(FAKE_DOCKER)
        os.chmod(docker, os.stat(docker).st_mode | stat.S_IXUSR | stat.S_IXGRP | stat.S_IXOTH)

    def tearDown(self):
        shutil.rmtree(self.tmp, ignore_errors=True)

    def run_bash(self, snippet):
        script = (
            'set -uo pipefail\n'
            f'export PATH="{to_posix(self.fakebin)}:$PATH"\n'
            f'export RBLOG_CERT_STATE="{to_posix(self.state)}"\n'
            f'export FAKE_PS="{to_posix(self.ps)}"\n'
            f'export FAKE_NGINX_T="{to_posix(self.nginx_t)}"\n'
            f'export FAKE_MOUNTS="{to_posix(self.mounts)}"\n'
            f'source "{to_posix(SETUP_SH)}"\n'
            f'{snippet}\n'
        )
        return subprocess.run(['bash', '-c', script], capture_output=True,
                              text=True, encoding='utf-8', errors='replace', timeout=120)

    def test_detect_nginx_container(self):
        """只认镜像名含 nginx 且发布了 443 的容器，不能被 8081/8090 干扰。"""
        p = self.run_bash('detect_nginx_container')
        self.assertEqual(p.returncode, 0, p.stderr)
        self.assertEqual(p.stdout.strip(), 'rblog-nginx')

    def test_detect_container_cert_pairs(self):
        """nginx -T 输出里抓出证书/私钥配对，去掉引号，去重排序。"""
        p = self.run_bash('detect_container_cert_pairs rblog-nginx')
        self.assertEqual(p.returncode, 0, p.stderr)
        self.assertEqual(p.stdout.strip().splitlines(), [
            '/etc/nginx/certs/admin.raxcl.cn/fullchain.cer|/etc/nginx/certs/admin.raxcl.cn/privkey.key',
            '/etc/nginx/certs/raxcl.cn/fullchain.cer|/etc/nginx/certs/raxcl.cn/privkey.key',
        ])

    def test_detect_pairs_handles_same_line_directives(self):
        """nginx 允许两条指令写同一行，归一化后必须还能抓到。"""
        with open(self.nginx_t, 'w', encoding='utf-8', newline='\n') as f:
            f.write('server { listen 443 ssl; '
                    'ssl_certificate /etc/nginx/certs/a/fullchain.cer; '
                    'ssl_certificate_key /etc/nginx/certs/a/privkey.key; }\n')
        p = self.run_bash('detect_container_cert_pairs rblog-nginx')
        self.assertEqual(p.returncode, 0, p.stderr)
        self.assertEqual(p.stdout.strip(),
                         '/etc/nginx/certs/a/fullchain.cer|/etc/nginx/certs/a/privkey.key')

    def test_detect_pairs_ignores_commented_out_directives(self):
        """注释掉的 ssl_certificate 不能被当成真实路径抓走。"""
        with open(self.nginx_t, 'w', encoding='utf-8', newline='\n') as f:
            f.write('# ssl_certificate /etc/nginx/certs/old/fullchain.cer;\n'
                    '# ssl_certificate_key /etc/nginx/certs/old/privkey.key;\n'
                    'ssl_certificate /etc/nginx/certs/new/fullchain.cer;\n'
                    'ssl_certificate_key /etc/nginx/certs/new/privkey.key;\n')
        p = self.run_bash('detect_container_cert_pairs rblog-nginx')
        self.assertEqual(p.returncode, 0, p.stderr)
        self.assertEqual(p.stdout.strip(),
                         '/etc/nginx/certs/new/fullchain.cer|/etc/nginx/certs/new/privkey.key')

    def test_container_to_host_prefix_mapping(self):
        p = self.run_bash('container_to_host rblog-nginx /etc/nginx/certs/raxcl.cn/fullchain.cer')
        self.assertEqual(p.returncode, 0, p.stderr)
        self.assertEqual(p.stdout.strip(),
                         '/www/wwwroot/RBlog/deploy/nginx/certs/raxcl.cn/fullchain.cer')

    def test_container_to_host_picks_longest_prefix(self):
        """嵌套挂载要选最长前缀，否则会映射到外层目录去。"""
        with open(self.mounts, 'a', encoding='utf-8', newline='\n') as f:
            f.write('/mnt/deep\t/etc/nginx/certs/raxcl.cn\n')
        p = self.run_bash('container_to_host rblog-nginx /etc/nginx/certs/raxcl.cn/privkey.key')
        self.assertEqual(p.returncode, 0, p.stderr)
        self.assertEqual(p.stdout.strip(), '/mnt/deep/privkey.key')

    def test_container_to_host_fails_when_not_mounted(self):
        p = self.run_bash('container_to_host rblog-nginx /etc/nginx/inside-image/fullchain.cer')
        self.assertNotEqual(p.returncode, 0)
        self.assertEqual(p.stdout.strip(), '')

    def test_resolve_targets_end_to_end(self):
        """探测结果必须是宿主机路径，且 stdout 里不能混进日志行。"""
        p = self.run_bash('resolve_targets rblog-nginx')
        self.assertEqual(p.returncode, 0, p.stderr)
        targets = p.stdout.strip()
        self.assertEqual(len(targets.split()), 2, f'应该正好两组路径，实际: {targets}')
        self.assertNotIn('[i]', targets, 'log() 的输出污染了返回值')
        self.assertNotIn('容器', targets)
        expected = ('/www/wwwroot/RBlog/deploy/nginx/certs/admin.raxcl.cn/fullchain.cer:'
                    '/www/wwwroot/RBlog/deploy/nginx/certs/admin.raxcl.cn/privkey.key '
                    '/www/wwwroot/RBlog/deploy/nginx/certs/raxcl.cn/fullchain.cer:'
                    '/www/wwwroot/RBlog/deploy/nginx/certs/raxcl.cn/privkey.key')
        self.assertEqual(targets, expected)

    def test_resolve_targets_rejects_unmounted_cert(self):
        """证书路径不在任何挂载里 -> 从宿主机写不进去，必须失败而不是静默产出错路径。"""
        with open(self.nginx_t, 'w', encoding='utf-8', newline='\n') as f:
            f.write('server {\n'
                    '  ssl_certificate /baked/in/fullchain.cer;\n'
                    '  ssl_certificate_key /baked/in/privkey.key;\n'
                    '}\n')
        p = self.run_bash('resolve_targets rblog-nginx')
        self.assertNotEqual(p.returncode, 0)
        self.assertEqual(p.stdout.strip(), '')
        self.assertIn('不在任何挂载里', p.stderr)


HOOK_FAKE_DOCKER = '''#!/usr/bin/env bash
echo "$*" >> "$FAKE_DOCKER_LOG"
case "$*" in
  *"nginx -t"*)      exit "${FAKE_NGINX_T_RC:-0}" ;;
  *"nginx -s reload"*) exit "${FAKE_NGINX_RELOAD_RC:-0}" ;;
esac
exit 0
'''


class TestRenewHook(unittest.TestCase):
    """renew-hook.sh 是唯一会覆写生产证书的地方，必须测到"该拒绝时真的拒绝"。"""

    def setUp(self):
        self.tmp = tempfile.mkdtemp(prefix='rblog-hook-test-')
        self.fakebin = os.path.join(self.tmp, 'bin')
        self.state = os.path.join(self.tmp, 'state')
        self.live = os.path.join(self.state, 'live')
        self.hook = os.path.join(self.state, 'hook')
        self.certs = os.path.join(self.tmp, 'nginx-certs')
        for d in (self.fakebin, self.state, self.live, self.hook,
                  os.path.join(self.certs, 'a'), os.path.join(self.certs, 'b')):
            os.makedirs(d, exist_ok=True)

        self.docker_log = os.path.join(self.tmp, 'docker.log')
        docker = os.path.join(self.fakebin, 'docker')
        with open(docker, 'w', encoding='utf-8', newline='\n') as f:
            f.write(HOOK_FAKE_DOCKER)
        os.chmod(docker, os.stat(docker).st_mode | stat.S_IXUSR | stat.S_IXGRP | stat.S_IXOTH)

        self.targets = (f'{to_posix(self.certs)}/a/fullchain.cer:{to_posix(self.certs)}/a/privkey.key '
                        f'{to_posix(self.certs)}/b/fullchain.cer:{to_posix(self.certs)}/b/privkey.key')
        with open(os.path.join(self.state, 'cert.env'), 'w', encoding='utf-8', newline='\n') as f:
            f.write('CERT_PRIMARY=raxcl.cn\n'
                    "CERT_SANS='*.raxcl.cn'\n"
                    'TENCENT_SECRET_ID=\nTENCENT_SECRET_KEY=\n'
                    'CDN_DOMAINS=\n')          # 留空，避免测试去连腾讯云
        with open(os.path.join(self.state, 'resolved.env'), 'w', encoding='utf-8', newline='\n') as f:
            f.write(f'NGINX_CONTAINER=rblog-nginx\nCERT_TARGETS=\'{self.targets}\'\n')

        shutil.copy(os.path.join(HERE, 'push-to-cdn.py'), os.path.join(self.hook, 'push-to-cdn.py'))
        self.make_cert('DNS:raxcl.cn,DNS:*.raxcl.cn')

    def tearDown(self):
        shutil.rmtree(self.tmp, ignore_errors=True)

    def make_cert(self, san):
        """用 openssl 现签一张自签证书到 live/，SAN 由参数控制。"""
        c = os.path.join(self.live, 'fullchain.cer')
        k = os.path.join(self.live, 'privkey.key')
        env = dict(os.environ)
        if os.name == 'nt':
            env['MSYS_NO_PATHCONV'] = '1'      # git bash 会把 /CN=... 当路径转换
        subprocess.run(['openssl', 'req', '-x509', '-newkey', 'rsa:2048', '-nodes',
                        '-keyout', k, '-out', c, '-days', '90',
                        '-subj', '/CN=raxcl.cn', '-addext', f'subjectAltName={san}'],
                       check=True, capture_output=True, env=env, timeout=180)
        return c, k

    def run_hook(self, nginx_t_rc=0):
        script = (
            'set -uo pipefail\n'
            f'export PATH="{to_posix(self.fakebin)}:$PATH"\n'
            f'export RBLOG_CERT_STATE="{to_posix(self.state)}"\n'
            f'export FAKE_DOCKER_LOG="{to_posix(self.docker_log)}"\n'
            f'export FAKE_NGINX_T_RC={nginx_t_rc}\n'
            f'bash "{to_posix(os.path.join(HERE, "renew-hook.sh"))}"\n'
        )
        return subprocess.run(['bash', '-c', script], capture_output=True,
                              text=True, encoding='utf-8', errors='replace', timeout=180)

    def docker_calls(self):
        if not os.path.exists(self.docker_log):
            return []
        with open(self.docker_log, encoding='utf-8') as f:
            return [ln.strip() for ln in f if ln.strip()]

    def read_target(self, name):
        p = os.path.join(self.certs, name)
        if not os.path.exists(p):
            return None
        with open(p, encoding='utf-8') as f:
            return f.read()

    def test_distributes_to_every_target_and_reloads(self):
        p = self.run_hook()
        self.assertEqual(p.returncode, 0, p.stdout + p.stderr)
        with open(os.path.join(self.live, 'fullchain.cer'), encoding='utf-8') as f:
            want = f.read()
        for sub in ('a', 'b'):
            self.assertEqual(self.read_target(f'{sub}/fullchain.cer'), want, f'{sub} 证书没分发到')
            self.assertIsNotNone(self.read_target(f'{sub}/privkey.key'), f'{sub} 私钥没分发到')
        self.assertTrue(any('nginx -t' in c for c in self.docker_calls()), '没做配置校验')
        self.assertTrue(any('nginx -s reload' in c for c in self.docker_calls()), '没 reload nginx')

    def test_refuses_cert_without_wildcard_san(self):
        """最关键的一条：SAN 缺通配符时必须拒绝覆写，否则站点会在续期那刻挂掉。"""
        sentinel_c, sentinel_k = 'OLD-GOOD-CERT\n', 'OLD-GOOD-KEY\n'
        for sub in ('a', 'b'):
            for name, content in (('fullchain.cer', sentinel_c), ('privkey.key', sentinel_k)):
                with open(os.path.join(self.certs, sub, name), 'w', encoding='utf-8') as f:
                    f.write(content)

        self.make_cert('DNS:raxcl.cn')          # 只有主域名，没有 *.raxcl.cn
        p = self.run_hook()

        self.assertNotEqual(p.returncode, 0, 'SAN 不全的证书必须被拒绝')
        self.assertIn('SAN', p.stdout + p.stderr)
        for sub in ('a', 'b'):
            self.assertEqual(self.read_target(f'{sub}/fullchain.cer'), sentinel_c,
                             f'{sub} 的好证书被坏证书覆盖了')
            self.assertEqual(self.read_target(f'{sub}/privkey.key'), sentinel_k)
        self.assertFalse(any('nginx -s reload' in c for c in self.docker_calls()),
                         '拒绝分发后不应该 reload nginx')

    def test_rollback_when_nginx_test_fails(self):
        first = self.run_hook()                 # 先成功跑一次，铺好基线证书
        self.assertEqual(first.returncode, 0, first.stdout + first.stderr)
        baseline = self.read_target('a/fullchain.cer')

        self.make_cert('DNS:raxcl.cn,DNS:*.raxcl.cn')   # 换一张合法但不同的新证书
        with open(self.docker_log, 'w', encoding='utf-8') as f:
            f.write('')
        p = self.run_hook(nginx_t_rc=1)         # nginx -t 失败

        self.assertNotEqual(p.returncode, 0)
        self.assertEqual(self.read_target('a/fullchain.cer'), baseline, 'nginx -t 失败后没回滚')
        self.assertIn('回滚', p.stdout + p.stderr)
        self.assertFalse(any('nginx -s reload' in c for c in self.docker_calls()),
                         'nginx -t 失败后不应 reload（此时内存里仍是旧证书，磁盘也已回滚）')

    def test_cdn_only_mode_leaves_origin_untouched(self):
        """线上默认形态 MANAGE_ORIGIN=0：源站已有一套 HTTP-01 自动续签在跑，
        钩子只推 CDN，绝不能去动源站的证书文件，也不该调 docker。"""
        with open(os.path.join(self.state, 'resolved.env'), 'w',
                  encoding='utf-8', newline='\n') as f:
            f.write('NGINX_CONTAINER=\nCERT_TARGETS=\n')
        with open(os.path.join(self.state, 'cert.env'), 'a',
                  encoding='utf-8', newline='\n') as f:
            f.write('CDN_DOMAINS=cdn.raxcl.cn\n')      # 后写的覆盖前面的空值

        p = self.run_hook()
        out = p.stdout + p.stderr
        self.assertEqual(p.returncode, 0, out)
        self.assertIn('不动源站证书', out)
        self.assertEqual(self.docker_calls(), [], '只推 CDN 时不该调用任何 docker 命令')
        for sub in ('a', 'b'):
            self.assertIsNone(self.read_target(f'{sub}/fullchain.cer'),
                              f'{sub} 的源站证书被改写了')


# 模拟 CentOS 7 自带的 OpenSSL 1.0.2k：x509 -ext 不存在，其余行为照常转交真 openssl
FAKE_OPENSSL_102 = '''#!/usr/bin/env bash
for a in "$@"; do
  if [ "$a" = "-ext" ]; then
    echo "unknown option -ext" >&2
    echo "usage: x509 args" >&2
    exit 1
  fi
done
exec "$REAL_OPENSSL" "$@"
'''


class TestCertSansOnOpenSSL102(unittest.TestCase):
    """线上是 CentOS 7 + OpenSSL 1.0.2k，没有 x509 -ext（1.1.1 才引入）。
    SAN 闸门是唯一能拦住"缺通配符的证书覆盖掉好证书"的检查，读不到 SAN 就等于没有闸门。
    """

    SAN = 'DNS:raxcl.cn,DNS:*.raxcl.cn'

    def setUp(self):
        self.tmp = tempfile.mkdtemp(prefix='rblog-ossl102-')
        self.fakebin = os.path.join(self.tmp, 'bin')
        os.makedirs(self.fakebin)

        real = shutil.which('openssl')
        if real is None:
            self.skipTest('本机没有 openssl')
        self.real_openssl = to_posix(real)

        wrapper = os.path.join(self.fakebin, 'openssl')
        with open(wrapper, 'w', encoding='utf-8', newline='\n') as f:
            f.write(FAKE_OPENSSL_102)
        os.chmod(wrapper, os.stat(wrapper).st_mode | stat.S_IXUSR | stat.S_IXGRP | stat.S_IXOTH)

        self.cert = os.path.join(self.tmp, 'fullchain.cer')
        env = dict(os.environ)
        if os.name == 'nt':
            env['MSYS_NO_PATHCONV'] = '1'
        subprocess.run(['openssl', 'req', '-x509', '-newkey', 'rsa:2048', '-nodes',
                        '-keyout', os.path.join(self.tmp, 'privkey.key'),
                        '-out', self.cert, '-days', '90',
                        '-subj', '/CN=raxcl.cn', '-addext', f'subjectAltName={self.SAN}'],
                       check=True, capture_output=True, env=env, timeout=180)

    def tearDown(self):
        shutil.rmtree(self.tmp, ignore_errors=True)

    def run_bash(self, snippet, simulate_102=True):
        path = f'export PATH="{to_posix(self.fakebin)}:$PATH"\n' if simulate_102 else ''
        script = (
            'set -uo pipefail\n'
            f'{path}'
            f'export REAL_OPENSSL="{self.real_openssl}"\n'
            f'export RBLOG_CERT_STATE="{to_posix(self.tmp)}"\n'
            f'source "{to_posix(SETUP_SH)}"\n'
            f'{snippet}\n'
        )
        return subprocess.run(['bash', '-c', script], capture_output=True,
                              text=True, encoding='utf-8', errors='replace', timeout=120)

    def test_old_ext_only_code_reads_nothing(self):
        """复现原 bug：只靠 -ext 的旧写法在 1.0.2k 上拿到空串，闸门会把好证书也判成不合格。"""
        p = self.run_bash(
            f'openssl x509 -in "{to_posix(self.cert)}" -noout -ext subjectAltName 2>/dev/null | tr -d " "')
        self.assertEqual(p.stdout.strip(), '', '预期 1.0.2k 上 -ext 读不到 SAN')

    def test_cert_sans_falls_back_to_text(self):
        p = self.run_bash(f'cert_sans "{to_posix(self.cert)}"')
        self.assertEqual(p.returncode, 0, p.stderr)
        self.assertEqual(p.stdout.strip(), self.SAN)

    def test_cert_sans_agrees_with_and_without_ext_support(self):
        """支持 -ext（OpenSSL 1.1.1+）和不支持（1.0.2k）两条路必须给出同样的结果。"""
        old = self.run_bash(f'cert_sans "{to_posix(self.cert)}"').stdout.strip()
        new = self.run_bash(f'cert_sans "{to_posix(self.cert)}"', simulate_102=False).stdout.strip()
        self.assertEqual(old, new)
        self.assertEqual(new, self.SAN)

    def test_wildcard_gate_passes_under_102(self):
        """闸门在 1.0.2k 上要能认出通配符，否则合法证书也会被拒。"""
        p = self.run_bash(
            f'S=$(cert_sans "{to_posix(self.cert)}")\n'
            'case "$S" in *DNS:\\*.raxcl.cn*) echo PASS ;; *) echo "FAIL:$S" ;; esac')
        self.assertEqual(p.stdout.strip(), 'PASS', p.stdout + p.stderr)

    def test_renew_hook_keeps_the_same_fallback(self):
        """renew-hook.sh 是 cron 里独立跑的，不能依赖 setup-cert.sh，回退必须自带一份。"""
        with open(os.path.join(HERE, 'renew-hook.sh'), encoding='utf-8') as f:
            src = f.read()
        self.assertIn('cert_sans', src)
        self.assertIn('-noout -text', src,
                      'renew-hook.sh 丢了 -text 回退，在 OpenSSL 1.0.2k 上 SAN 闸门会失效')

    def test_print_cert_sans_ends_with_exactly_one_newline(self):
        """报告里 SAN 行必须自己收尾，否则会和下一段的标题粘成一行。

        cert_sans 结尾用 tr -d 抹掉了换行，GNU sed 又不会给缺尾换行的最后一行补上，
        所以 print_cert_sans 必须自己补。线上 --check 里出现过
        "    DNS:cdn.raxcl.cn  同机上其它 acme.sh 证书（...）：" 这种粘行。
        """
        p = self.run_bash(f'print_cert_sans "{to_posix(self.cert)}"\necho "<<下一段标题>>"')
        self.assertEqual(p.returncode, 0, p.stdout + p.stderr)
        lines = p.stdout.split('\n')
        san_lines = [l for l in lines if 'DNS:' in l]
        self.assertEqual(len(san_lines), 2, '两个 SAN 应该分成两行')
        for l in san_lines:
            self.assertTrue(l.startswith('    DNS:'), f'SAN 行该缩进 4 格：{l!r}')
        self.assertNotIn('DNS:raxcl.cn  <<', p.stdout, 'SAN 行和下一段标题粘在一起了')
        self.assertIn('<<下一段标题>>', lines, '下一段应该另起一行')

    def test_old_pipeline_glued_lines_together(self):
        """复现原 bug：直接管道出去的旧写法输出不以换行结尾。

        注意是**零间隔**粘住 —— 线上看到的 "DNS:cdn.raxcl.cn  同机上其它..." 里那
        两个空格是下一段标题自己的缩进，不是分隔符。这里让下一行也带上真实报告
        那样的两格缩进，形状才和线上一致。

        留着这条是防止有人觉得 print_cert_sans 绕、又改回一行管道。
        """
        p = self.run_bash(
            f'cert_sans "{to_posix(self.cert)}" | tr "," "\\n" | sed "/^$/d; s/^/    /"\n'
            'echo "  <<下一段标题>>"')
        self.assertIn('DNS:*.raxcl.cn  <<下一段标题>>', p.stdout,
                      '旧写法应当复现粘行，否则这条测试的前提就不成立')
        self.assertNotIn('DNS:*.raxcl.cn\n', p.stdout, '粘住的前提就是上一行没有换行收尾')


class TestPy36Compat(unittest.TestCase):
    """服务器上是 CentOS 7 的 Python 3.6.8。sys.stdout.reconfigure 是 3.7 才有的，
    在模块级调用会让脚本一导入就崩，报错还完全不指向真实原因；而且模块级改写
    sys.stdout/stderr 会污染用 spec_from_file_location 导入它的测试运行器。"""

    def test_module_import_does_not_replace_sys_streams(self):
        spec = importlib.util.spec_from_file_location(
            'push_to_cdn_reimport', os.path.join(HERE, 'push-to-cdn.py'))
        mod = importlib.util.module_from_spec(spec)
        out, err = sys.stdout, sys.stderr
        spec.loader.exec_module(mod)
        self.assertIs(sys.stdout, out, '模块级改写了 sys.stdout，会污染导入方')
        self.assertIs(sys.stderr, err, '模块级改写了 sys.stderr，会污染导入方')

    def test_force_utf8_falls_back_when_no_reconfigure(self):
        """3.6 的 stdout 没有 reconfigure，必须退回包一层 TextIOWrapper；
        否则 C locale 下打印中文直接 UnicodeEncodeError，盖住真正的下发结果。"""
        class Legacy:
            def __init__(self, buf):
                self.buffer = buf

        buf = io.BytesIO()
        wrapped = ptc._force_utf8(Legacy(buf))
        self.assertIsInstance(wrapped, io.TextIOWrapper)
        wrapped.write('证书下发成功')
        wrapped.flush()
        self.assertEqual(buf.getvalue().decode('utf-8'), '证书下发成功')

    def test_force_utf8_prefers_reconfigure(self):
        calls = []

        class Modern:
            def reconfigure(self, **kw):
                calls.append(kw)

        stream = Modern()
        self.assertIs(ptc._force_utf8(stream), stream)
        self.assertEqual(calls, [{'encoding': 'utf-8', 'errors': 'replace'}])


class TestHijackGuard(unittest.TestCase):
    """服务器上 raxcl.cn 和 admin.raxcl.cn 已经各有一套 HTTP-01 + webroot 的自动续签在跑。
    acme.sh 按主域名定位证书目录，撞名的话 --issue 会把 Le_Webroot 从路径就地改写成
    dns_tencent，顶掉那套原本不依赖任何云凭据的续签 —— 而且现场看不出是谁干的。
    """

    def setUp(self):
        self.tmp = tempfile.mkdtemp(prefix='rblog-hijack-')
        self.acme = os.path.join(self.tmp, 'acme')
        self.state = os.path.join(self.tmp, 'state')
        os.makedirs(self.acme)
        os.makedirs(self.state)

    def tearDown(self):
        shutil.rmtree(self.tmp, ignore_errors=True)

    def make_existing_cert(self, domain, webroot):
        d = os.path.join(self.acme, f'{domain}_ecc')
        os.makedirs(d, exist_ok=True)
        with open(os.path.join(d, f'{domain}.conf'), 'w', encoding='utf-8', newline='\n') as f:
            f.write(f"Le_Domain='{domain}'\nLe_Webroot='{webroot}'\n")

    def run_guard(self, primary):
        script = (
            'set -uo pipefail\n'
            f'export RBLOG_CERT_STATE="{to_posix(self.state)}"\n'
            f'source "{to_posix(SETUP_SH)}"\n'
            f'ACME_DIR="{to_posix(self.acme)}"\n'
            f'CERT_PRIMARY="{primary}"\n'
            'guard_no_hijack\n'
        )
        return subprocess.run(['bash', '-c', script], capture_output=True,
                              text=True, encoding='utf-8', errors='replace', timeout=60)

    def test_refuses_to_take_over_http01_cert(self):
        self.make_existing_cert('raxcl.cn', '/mnt/raxcl/blog_view')
        p = self.run_guard('raxcl.cn')
        self.assertNotEqual(p.returncode, 0, '撞上 HTTP-01 证书必须拒绝，否则会把人家顶掉')
        self.assertIn('/mnt/raxcl/blog_view', p.stdout + p.stderr,
                      '报错里要点名现有的验证方式，否则看不出为什么被拒')

    def test_allows_fresh_primary_domain(self):
        """换成 cdn.raxcl.cn 就不撞车 —— 这正是线上要用的配置。"""
        self.make_existing_cert('raxcl.cn', '/mnt/raxcl/blog_view')
        self.make_existing_cert('admin.raxcl.cn', '/mnt/raxcl/blog_admin')
        p = self.run_guard('cdn.raxcl.cn')
        self.assertEqual(p.returncode, 0, p.stdout + p.stderr)

    def test_allows_renewing_own_dns01_cert(self):
        self.make_existing_cert('cdn.raxcl.cn', 'dns_tencent')
        p = self.run_guard('cdn.raxcl.cn')
        self.assertEqual(p.returncode, 0, p.stdout + p.stderr)


class TestDnsPlugin(unittest.TestCase):
    """线上那份 acme.sh 是"只拷一个文件"式的残缺安装，整个 dnsapi/ 目录都不存在。
    HTTP-01 webroot 用不到插件，所以之前一直没暴露。缺插件时 acme.sh 不会明确报错，
    只打印 "Cannot find DNS API hook for: dns_tencent" 然后以退出码 3 失败。
    """

    GOOD_PLUGIN = 'dns_tencent_info="Tencent.com"\n\ndns_tencent_add() {\n  :\n}\n'

    def setUp(self):
        self.tmp = tempfile.mkdtemp(prefix='rblog-dnsplug-')
        self.acme = os.path.join(self.tmp, 'acme')
        self.state = os.path.join(self.tmp, 'state')
        self.fakebin = os.path.join(self.tmp, 'bin')
        for d in (self.acme, self.state, self.fakebin):
            os.makedirs(d)

    def tearDown(self):
        shutil.rmtree(self.tmp, ignore_errors=True)

    def stub_curl(self, body='', rc=0):
        """假 curl：认 -o <目标路径>，把 body 写过去，然后以 rc 退出。"""
        payload = os.path.join(self.tmp, 'payload')
        with open(payload, 'w', encoding='utf-8', newline='\n') as f:
            f.write(body)
        curl = os.path.join(self.fakebin, 'curl')
        with open(curl, 'w', encoding='utf-8', newline='\n') as f:
            f.write('#!/usr/bin/env bash\n'
                    'out=""; prev=""\n'
                    'for a in "$@"; do [ "$prev" = "-o" ] && out="$a"; prev="$a"; done\n'
                    f'[ -n "$out" ] && cp "{to_posix(payload)}" "$out"\n'
                    f'exit {rc}\n')
        os.chmod(curl, 0o755)

    def run_ensure(self):
        script = (
            'set -uo pipefail\n'
            f'export PATH="{to_posix(self.fakebin)}:$PATH"\n'
            f'export RBLOG_CERT_STATE="{to_posix(self.state)}"\n'
            f'source "{to_posix(SETUP_SH)}"\n'
            f'ACME_DIR="{to_posix(self.acme)}"\n'
            'ensure_dns_plugin\n'
        )
        return subprocess.run(['bash', '-c', script], capture_output=True, text=True,
                              encoding='utf-8', errors='replace', timeout=60)

    def plugin_path(self):
        return os.path.join(self.acme, 'dnsapi', 'dns_tencent.sh')

    def test_installs_when_missing(self):
        self.stub_curl(self.GOOD_PLUGIN)
        p = self.run_ensure()
        self.assertEqual(p.returncode, 0, p.stdout + p.stderr)
        self.assertTrue(os.path.exists(self.plugin_path()), '插件没落盘')
        with open(self.plugin_path(), encoding='utf-8') as f:
            self.assertIn('dns_tencent_add() {', f.read())

    def test_refuses_to_install_non_plugin_payload(self):
        """镜像抽风返回 HTML 404 页时，绝不能把垃圾当插件落盘 ——
        那样 acme.sh 会 source 一个语法错误的文件，报错更难懂。"""
        self.stub_curl('<html><body>404: Not Found</body></html>')
        p = self.run_ensure()
        self.assertNotEqual(p.returncode, 0, '下载内容不是插件时必须失败')
        self.assertFalse(os.path.exists(self.plugin_path()), '垃圾内容被落盘了')

    def test_dies_when_download_fails(self):
        self.stub_curl(rc=6)
        p = self.run_ensure()
        self.assertNotEqual(p.returncode, 0, '两个源都拉不到时必须失败')
        self.assertIn('dns_tencent', p.stdout + p.stderr)

    def test_skips_download_when_already_present(self):
        os.makedirs(os.path.dirname(self.plugin_path()))
        with open(self.plugin_path(), 'w', encoding='utf-8', newline='\n') as f:
            f.write(self.GOOD_PLUGIN)
        self.stub_curl(rc=7)
        p = self.run_ensure()
        self.assertEqual(p.returncode, 0, p.stdout + p.stderr)
        with open(self.plugin_path(), encoding='utf-8') as f:
            self.assertEqual(f.read(), self.GOOD_PLUGIN, '已就位还去覆盖，白下载一次')

    def test_main_installs_plugin_after_acme(self):
        with open(SETUP_SH, encoding='utf-8') as f:
            src = f.read()
        body = src[src.index('\nmain() {'):]
        self.assertIn('install_acme', body)
        self.assertLess(body.index('install_acme'), body.index('ensure_dns_plugin'),
                        'ensure_dns_plugin 必须在 install_acme 之后、issue_cert 之前')
        self.assertLess(body.index('ensure_dns_plugin'), body.index('issue_cert'))


class TestHardenAcmeSecrets(unittest.TestCase):
    """acme.sh 把 Tencent_SecretId/SecretKey 写进 ~/.acme.sh/account.conf，默认 644
    全局可读（线上实测就是 644）。cron 续签全靠这份文件，所以每次重装 acme.sh 都会
    复现，得由脚本统一收紧，而不是手工修一次。

    Windows 的 git bash 上 chmod 不会改变 stat 报出的模式，所以用假命令测逻辑。"""

    def setUp(self):
        self.tmp = tempfile.mkdtemp(prefix='rblog-harden-')
        self.acme = os.path.join(self.tmp, 'acme')
        self.state = os.path.join(self.tmp, 'state')
        self.fakebin = os.path.join(self.tmp, 'bin')
        for d in (self.acme, self.state, self.fakebin):
            os.makedirs(d)
        with open(os.path.join(self.acme, 'account.conf'), 'w', encoding='utf-8') as f:
            f.write("SAVED_Tencent_SecretId='x'\n")

    def tearDown(self):
        shutil.rmtree(self.tmp, ignore_errors=True)

    def stub_mode(self, mode):
        """假 stat 永远报 mode；假 chmod 把调用记进 calls.log。返回 calls.log 路径。"""
        calls = os.path.join(self.tmp, 'calls.log')
        with open(os.path.join(self.fakebin, 'stat'), 'w', encoding='utf-8', newline='\n') as f:
            f.write(f'#!/usr/bin/env bash\necho "{mode}"\n')
        with open(os.path.join(self.fakebin, 'chmod'), 'w', encoding='utf-8', newline='\n') as f:
            f.write('#!/usr/bin/env bash\n'
                    f'echo "chmod $*" >> "{to_posix(calls)}"\n')
        for n in ('stat', 'chmod'):
            os.chmod(os.path.join(self.fakebin, n), 0o755)
        return calls

    def run_fn(self):
        script = (
            'set -uo pipefail\n'
            f'export PATH="{to_posix(self.fakebin)}:$PATH"\n'
            f'export RBLOG_CERT_STATE="{to_posix(self.state)}"\n'
            f'source "{to_posix(SETUP_SH)}"\n'
            f'ACME_DIR="{to_posix(self.acme)}"\n'
            'harden_acme_secrets\n'
        )
        return subprocess.run(['bash', '-c', script], capture_output=True, text=True,
                              encoding='utf-8', errors='replace', timeout=60)

    def test_tightens_world_readable_conf(self):
        calls = self.stub_mode('644')
        p = self.run_fn()
        self.assertEqual(p.returncode, 0, p.stdout + p.stderr)
        with open(calls, encoding='utf-8') as f:
            self.assertIn('chmod 600', f.read(), '全局可读的凭据文件必须收紧')

    def test_quiet_when_already_600(self):
        """幂等重跑没变化就该安静，别每次都打一行"已收紧"。"""
        calls = self.stub_mode('600')
        p = self.run_fn()
        self.assertEqual(p.returncode, 0, p.stdout + p.stderr)
        self.assertFalse(os.path.exists(calls), '已经是 600 还去 chmod')
        self.assertEqual((p.stdout + p.stderr).strip(), '', '没变化不该出声')

    def test_no_crash_without_account_conf(self):
        os.remove(os.path.join(self.acme, 'account.conf'))
        p = self.run_fn()
        self.assertEqual(p.returncode, 0, p.stdout + p.stderr)

    def test_main_hardens_after_issue(self):
        with open(SETUP_SH, encoding='utf-8') as f:
            body = f.read()
        body = body[body.index('\nmain() {'):]
        self.assertLess(body.index('issue_cert'), body.index('harden_acme_secrets'),
                        '首次签发时 acme.sh 才写凭据进 account.conf，收紧必须排在签发之后')


class TestCredentialReport(unittest.TestCase):
    """线上实测到的泄露：--check 报告把 SecretId 和 SecretKey 全量打进了 stdout。

    起因是 ${VAR:+已设置}${VAR:-[!!] 未设置} 这个写法 —— ":-" 只在变量为空/未设置时
    才替换成后备值，变量已设置时它展开的就是变量本身。于是"已设置"后面紧跟着完整的
    SecretKey。--check 是排错时最常跑的命令，输出还常被重定向进日志，等于把云凭据
    持久化到一个没人当秘密看的地方。
    """

    FAKE_ID = 'AKIDxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx'   # 36 字符，和真实 SecretId 同长
    FAKE_KEY = 'SUPERSECRETKEYVALUE1234567890abcdefghij'

    def run_report(self, secret_id=None, secret_key=None, email=None):
        env = []
        if secret_id is not None:
            env.append(f'export TENCENT_SECRET_ID={secret_id!r}')
        if secret_key is not None:
            env.append(f'export TENCENT_SECRET_KEY={secret_key!r}')
        if email is not None:
            env.append(f'CERT_EMAIL={email!r}; export CERT_EMAIL')
        script = (
            'set -uo pipefail\n'
            + '\n'.join(env) + '\n'
            'export RBLOG_CERT_STATE=/nonexistent-state-dir\n'
            f'source "{to_posix(SETUP_SH)}"\n'
            'report_credentials\n'
        )
        return subprocess.run(['bash', '-c', script], capture_output=True, text=True,
                              encoding='utf-8', errors='replace', timeout=60)

    def test_secret_key_never_appears(self):
        p = self.run_report(self.FAKE_ID, self.FAKE_KEY)
        self.assertEqual(p.returncode, 0, p.stdout + p.stderr)
        self.assertNotIn(self.FAKE_KEY, p.stdout + p.stderr,
                         'SecretKey 一个字符都不能出现在报告里')
        self.assertIn('TENCENT_SECRET_KEY', p.stdout, '但要说明它已配置')
        self.assertIn(str(len(self.FAKE_KEY)), p.stdout, '报长度便于核对配没配错')

    def test_secret_id_only_prefix_shown(self):
        """SecretId 不是秘密（控制台本来就全量可见），留前缀是为了核对是哪把 key。"""
        p = self.run_report(self.FAKE_ID, self.FAKE_KEY)
        self.assertNotIn(self.FAKE_ID, p.stdout + p.stderr, '不该全量回显')
        self.assertIn(self.FAKE_ID[:8], p.stdout, '前缀要留着，否则没法核对配的是哪把 key')

    def test_unset_credentials_reported_clearly(self):
        p = self.run_report()
        self.assertEqual(p.returncode, 0, p.stdout + p.stderr)
        self.assertIn('TENCENT_SECRET_ID:  [!!] 未设置', p.stdout)
        self.assertIn('TENCENT_SECRET_KEY: [!!] 未设置', p.stdout)

    def test_missing_email_is_not_an_error(self):
        """ACME 的 contact 是可选的，实测不填也能签发成功，所以不该报成 [!!]。"""
        p = self.run_report(self.FAKE_ID, self.FAKE_KEY)
        line = [l for l in p.stdout.splitlines() if 'CERT_EMAIL' in l]
        self.assertEqual(len(line), 1)
        self.assertIn('[--]', line[0], '缺 email 不是错误，用 [--] 而不是 [!!]')
        self.assertNotIn('[!!]', line[0])
        self.assertIn('不影响签发', line[0], '要说清真实后果，别让人以为坏了')

    def test_email_shown_when_set(self):
        p = self.run_report(self.FAKE_ID, self.FAKE_KEY, 'me@example.com')
        self.assertIn('me@example.com', p.stdout, 'email 不是秘密，设置时要显示出来供核对')

    def test_old_expansion_actually_leaked(self):
        """钉死这个 bug 是真的：旧写法在同一把假 key 上会把两个值都全量打出来。

        留着这条是为了防止有人觉得新写法啰嗦、又改回 ${VAR:+..}${VAR:-..} 的紧凑形式。
        """
        script = (
            f'export TENCENT_SECRET_ID={self.FAKE_ID!r}\n'
            f'export TENCENT_SECRET_KEY={self.FAKE_KEY!r}\n'
            'echo "  TENCENT_SECRET_ID:  ${TENCENT_SECRET_ID:+已设置}'
            '${TENCENT_SECRET_ID:-[!!] 未设置}"\n'
            'echo "  TENCENT_SECRET_KEY: ${TENCENT_SECRET_KEY:+已设置}'
            '${TENCENT_SECRET_KEY:-[!!] 未设置}"\n'
        )
        p = subprocess.run(['bash', '-c', script], capture_output=True, text=True,
                           encoding='utf-8', errors='replace', timeout=60)
        self.assertIn(self.FAKE_KEY, p.stdout, '旧写法应当复现泄露，否则这条测试的前提就不成立')
        self.assertIn(self.FAKE_ID, p.stdout)

    def test_source_has_no_xtrace(self):
        """set -x 会把展开后的凭据打进 stderr，等于另一条泄露路径。

        逐行查而不是用 ^ 锚点的正则 —— re.search 默认没有 MULTILINE，^ 只匹配整个
        字符串开头，而脚本第一行是 shebang，那样这条断言永远通过、等于没测。
        """
        with open(SETUP_SH, encoding='utf-8') as f:
            lines = f.read().splitlines()
        offenders = [(i + 1, l.strip()) for i, l in enumerate(lines)
                     if not l.lstrip().startswith('#')
                     and re.match(r'\s*set\s+(-\S*x\b|-o\s+xtrace\b)', l)]
        self.assertEqual(offenders, [], '脚本里不能有 set -x / set -o xtrace')


class TestAcmeCertListing(unittest.TestCase):
    """~/.acme.sh/ 下不只有证书目录，还有 dnsapi / ca / deploy 这些程序目录。

    线上实测清单里混进了一行 "dnsapi 验证方式=?"，纯噪音；更要紧的是这份清单是用来
    看有没有撞名的，噪音多了真问题反而容易被划过去。判据改成"目录里有没有同名的
    <主域名>.conf"，这样以后 acme.sh 再加什么程序目录都不会被误列。
    """

    def setUp(self):
        self.tmp = tempfile.mkdtemp(prefix='rblog-list-')
        self.acme = os.path.join(self.tmp, 'acme')
        os.makedirs(self.acme)

    def tearDown(self):
        shutil.rmtree(self.tmp, ignore_errors=True)

    def make_cert_dir(self, domain, webroot):
        d = os.path.join(self.acme, f'{domain}_ecc')
        os.makedirs(d, exist_ok=True)
        with open(os.path.join(d, f'{domain}.conf'), 'w', encoding='utf-8', newline='\n') as f:
            f.write(f"Le_Domain='{domain}'\nLe_Webroot='{webroot}'\n")

    def make_program_dir(self, name):
        d = os.path.join(self.acme, name)
        os.makedirs(d, exist_ok=True)
        with open(os.path.join(d, 'something.sh'), 'w', encoding='utf-8') as f:
            f.write('#!/bin/sh\n')

    def run_listing(self, primary):
        script = (
            'set -uo pipefail\n'
            'export RBLOG_CERT_STATE=/nonexistent-state-dir\n'
            f'source "{to_posix(SETUP_SH)}"\n'
            f'ACME_DIR="{to_posix(self.acme)}"\n'
            f'CERT_PRIMARY="{primary}"\n'
            'list_acme_certs\n'
        )
        return subprocess.run(['bash', '-c', script], capture_output=True, text=True,
                              encoding='utf-8', errors='replace', timeout=60)

    def test_skips_program_directories(self):
        self.make_cert_dir('raxcl.cn', '/mnt/raxcl/blog_view')
        for name in ('dnsapi', 'ca', 'deploy'):
            self.make_program_dir(name)
        p = self.run_listing('cdn.raxcl.cn')
        self.assertEqual(p.returncode, 0, p.stdout + p.stderr)
        for name in ('dnsapi', 'ca', 'deploy'):
            self.assertNotRegex(p.stdout, rf'^\s+{name}\s', f'{name} 不是证书，不该被列出来')
        self.assertIn('raxcl.cn', p.stdout, '真证书要列出来')

    def test_shows_validation_method(self):
        """验证方式是判断"这张会不会被我顶掉"的关键信息，必须显示。"""
        self.make_cert_dir('admin.raxcl.cn', '/mnt/raxcl/blog_admin')
        p = self.run_listing('cdn.raxcl.cn')
        self.assertIn('/mnt/raxcl/blog_admin', p.stdout)

    def test_flags_colliding_primary(self):
        self.make_cert_dir('cdn.raxcl.cn', 'dns_tencent')
        self.make_cert_dir('raxcl.cn', '/mnt/raxcl/blog_view')
        p = self.run_listing('cdn.raxcl.cn')
        self.assertIn('和 CERT_PRIMARY 撞名', p.stdout)
        # 标题行本身就含"撞名"二字，得按标注用的 '<<<' 过滤才是真的证书行
        collide = [l for l in p.stdout.splitlines() if '<<<' in l]
        self.assertEqual(len(collide), 1, '只该标注撞名的那一个')
        self.assertIn('cdn.raxcl.cn', collide[0])

    def test_no_ecc_suffix_dir_also_listed(self):
        """RSA 证书目录不带 _ecc 后缀，同样要能识别。"""
        d = os.path.join(self.acme, 'rsa.example.com')
        os.makedirs(d)
        with open(os.path.join(d, 'rsa.example.com.conf'), 'w', encoding='utf-8', newline='\n') as f:
            f.write("Le_Domain='rsa.example.com'\nLe_Webroot='/var/www/html'\n")
        p = self.run_listing('cdn.raxcl.cn')
        self.assertIn('rsa.example.com', p.stdout)
        self.assertIn('/var/www/html', p.stdout)


class TestSanListGuard(unittest.TestCase):
    """线上真实失败过：CERT_PRIMARY=cdn.raxcl.cn + CERT_SANS='raxcl.cn *.raxcl.cn'，
    Let's Encrypt 直接 400 "Domain name cdn.raxcl.cn is redundant with a wildcard
    domain in the same request"。这个错误要等 DNS-01 跑完一轮才返回，光看日志很难
    想到是配置自相矛盾，所以要在本地拦下来。
    """

    def setUp(self):
        self.tmp = tempfile.mkdtemp(prefix='rblog-sanlist-')

    def tearDown(self):
        shutil.rmtree(self.tmp, ignore_errors=True)

    def run_guard(self, primary, sans):
        script = (
            'set -uo pipefail\n'
            f'export RBLOG_CERT_STATE="{to_posix(self.tmp)}"\n'
            f'source "{to_posix(SETUP_SH)}"\n'
            f'CERT_PRIMARY="{primary}"\n'
            f'CERT_SANS="{sans}"\n'
            'check_san_list && echo CLEAN || echo "BLOCKED:$SAN_PROBLEM"\n'
        )
        p = subprocess.run(['bash', '-c', script], capture_output=True, text=True,
                           encoding='utf-8', errors='replace', timeout=60)
        self.assertEqual(p.returncode, 0, p.stderr)
        return p.stdout.strip()

    def test_blocks_production_config_that_ca_rejected(self):
        out = self.run_guard('cdn.raxcl.cn', 'raxcl.cn *.raxcl.cn')
        self.assertTrue(out.startswith('BLOCKED:'), f'这个配置 CA 会拒单，必须本地拦住: {out}')
        self.assertIn('*.raxcl.cn', out, '报错要点名是哪个通配符造成的冗余')

    def test_allows_apex_plus_wildcard(self):
        """raxcl.cn + *.raxcl.cn 是完全合法的（通配符不含 apex 自身）。
        "*.raxcl.cn" 这个字符串自己也以 ".raxcl.cn" 结尾，朴素的子串匹配会把
        通配符判成"被自己覆盖"，从而误拦这套配置。"""
        self.assertEqual(self.run_guard('raxcl.cn', '*.raxcl.cn'), 'CLEAN')

    def test_allows_cdn_only_default(self):
        """现在线上用的就是单域名，CERT_SANS 留空。"""
        self.assertEqual(self.run_guard('cdn.raxcl.cn', ''), 'CLEAN')

    def test_wildcard_covers_only_one_label(self):
        self.assertEqual(self.run_guard('cdn.raxcl.cn', 'a.b.raxcl.cn'), 'CLEAN')
        self.assertTrue(self.run_guard('cdn.raxcl.cn', 'a.raxcl.cn *.raxcl.cn')
                        .startswith('BLOCKED:'))

    def test_blocks_duplicate_domain(self):
        out = self.run_guard('cdn.raxcl.cn', 'cdn.raxcl.cn')
        self.assertTrue(out.startswith('BLOCKED:'), out)
        self.assertIn('出现了多次', out)

    def test_shipped_example_config_passes_guard(self):
        """cert.env.example 的出厂默认值本身必须能签出来，否则照抄文档就翻车。"""
        with open(os.path.join(HERE, 'cert.env.example'), encoding='utf-8') as f:
            text = f.read()
        primary = re.search(r'(?m)^CERT_PRIMARY=(.*)$', text).group(1).strip().strip("'\"")
        sans = re.search(r'(?m)^CERT_SANS=(.*)$', text).group(1).strip().strip("'\"")
        self.assertEqual(primary, 'cdn.raxcl.cn')
        self.assertEqual(self.run_guard(primary, sans), 'CLEAN',
                         f'出厂配置 CERT_PRIMARY={primary} CERT_SANS={sans!r} 过不了闸门')

    def test_main_dies_before_docker_probe(self):
        """域名列表有问题要在 docker 探测之前就失败，别先跑一堆无关的探测。"""
        env_file = os.path.join(self.tmp, 'cert.env')
        with open(env_file, 'w', encoding='utf-8', newline='\n') as f:
            f.write("CERT_PRIMARY=cdn.raxcl.cn\nCERT_SANS='*.raxcl.cn'\nMANAGE_ORIGIN=0\n")
        script = (
            'set -uo pipefail\n'
            f'export RBLOG_CERT_STATE="{to_posix(self.tmp)}"\n'
            f'export RBLOG_CERT_ENV="{to_posix(env_file)}"\n'
            f'source "{to_posix(SETUP_SH)}"\n'
            'CHECK_ONLY=0\n'
            'main\n'
        )
        p = subprocess.run(['bash', '-c', script], capture_output=True, text=True,
                           encoding='utf-8', errors='replace', timeout=120)
        self.assertNotEqual(p.returncode, 0, '域名列表自相矛盾时必须失败')
        combined = p.stdout + p.stderr
        self.assertIn('域名列表有问题', combined)
        self.assertNotIn('nginx 容器', combined, '应该在 docker 探测之前就退出')


class TestLineEndings(unittest.TestCase):
    """这些 .sh 要在 Linux 服务器上直接执行，带 CRLF 会报
    "$'\\r': command not found" 或 bad interpreter，而报错完全不指向真实原因。
    本仓库 core.autocrlf=true，Windows 上编辑很容易把文件改写成 CRLF，所以钉死。
    """

    def test_all_committed_files_are_lf(self):
        offenders = []
        for name in sorted(os.listdir(HERE)):
            if name.startswith('.') or name == '__pycache__':
                continue
            path = os.path.join(HERE, name)
            if not os.path.isfile(path):
                continue
            with open(path, 'rb') as f:
                if b'\r\n' in f.read():
                    offenders.append(name)
        self.assertEqual(offenders, [], f'这些文件是 CRLF，服务器上会跑不起来: {offenders}')


if __name__ == '__main__':
    unittest.main(verbosity=2)
