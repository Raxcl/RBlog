package cn.raxcl.service.impl;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.raxcl.entity.LoginLog;
import cn.raxcl.exception.PersistenceException;
import cn.raxcl.mapper.LoginLogMapper;
import cn.raxcl.service.LoginLogService;
import cn.raxcl.util.IpAddressUtils;
import cn.raxcl.util.UserAgentUtils;

import java.util.List;
import java.util.Map;

/**
 * @Description: 登录日志业务层实现
 * @author Raxcl
 * @date 2022-01-07 12:33:13
 */
@Service
public class LoginLogServiceImpl implements LoginLogService {
	private final LoginLogMapper loginLogMapper;
	private final UserAgentUtils userAgentUtils;

	public LoginLogServiceImpl(LoginLogMapper loginLogMapper, UserAgentUtils userAgentUtils) {
		this.loginLogMapper = loginLogMapper;
		this.userAgentUtils = userAgentUtils;
	}

	@Override
	public List<LoginLog> getLoginLogListByDate(String startDate, String endDate) {
		return loginLogMapper.getLoginLogListByDate(startDate, endDate);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	@Async
	public void saveLoginLog(LoginLog log) {
		//TODO 重复代码 尝试提取失败
		String ipSource = IpAddressUtils.getCityInfo(log.getIp());
		Map<String, String> userAgentMap = userAgentUtils.parseOsAndBrowser(log.getUserAgent());
		String os = userAgentMap.get("os");
		String browser = userAgentMap.get("browser");
		log.setIpSource(ipSource);
		log.setOs(os);
		log.setBrowser(browser);
		if (loginLogMapper.saveLoginLog(log) != 1) {
			throw new PersistenceException("日志添加失败");
		}
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void deleteLoginLogById(Long id) {
		if (loginLogMapper.deleteLoginLogById(id) != 1) {
			throw new PersistenceException("删除日志失败");
		}
	}
}
