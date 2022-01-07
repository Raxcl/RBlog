package cn.raxcl.service.impl;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.raxcl.entity.ExceptionLog;
import cn.raxcl.exception.PersistenceException;
import cn.raxcl.mapper.ExceptionLogMapper;
import cn.raxcl.service.ExceptionLogService;
import cn.raxcl.util.IpAddressUtils;
import cn.raxcl.util.UserAgentUtils;

import java.util.List;
import java.util.Map;

/**
 * @Description: 异常日志业务层实现
 * @author Raxcl
 * @date 2022-01-07 15:58:16
 */
@Service
public class ExceptionLogServiceImpl implements ExceptionLogService {
	private final ExceptionLogMapper exceptionLogMapper;
	private final UserAgentUtils userAgentUtils;

	public ExceptionLogServiceImpl(ExceptionLogMapper exceptionLogMapper, UserAgentUtils userAgentUtils) {
		this.exceptionLogMapper = exceptionLogMapper;
		this.userAgentUtils = userAgentUtils;
	}

	@Override
	public List<ExceptionLog> getExceptionLogListByDate(String startDate, String endDate) {
		return exceptionLogMapper.getExceptionLogListByDate(startDate, endDate);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	@Async
	public void saveExceptionLog(ExceptionLog log) {
		//TODO
		String ipSource = IpAddressUtils.getCityInfo(log.getIp());
		Map<String, String> userAgentMap = userAgentUtils.parseOsAndBrowser(log.getUserAgent());
		String os = userAgentMap.get("os");
		String browser = userAgentMap.get("browser");
		log.setIpSource(ipSource);
		log.setOs(os);
		log.setBrowser(browser);
		if (exceptionLogMapper.saveExceptionLog(log) != 1) {
			throw new PersistenceException("日志添加失败");
		}
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void deleteExceptionLogById(Long id) {
		if (exceptionLogMapper.deleteExceptionLogById(id) != 1) {
			throw new PersistenceException("删除日志失败");
		}
	}
}
