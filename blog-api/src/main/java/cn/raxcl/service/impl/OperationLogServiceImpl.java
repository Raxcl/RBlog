package cn.raxcl.service.impl;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.raxcl.entity.OperationLog;
import cn.raxcl.exception.PersistenceException;
import cn.raxcl.mapper.OperationLogMapper;
import cn.raxcl.service.OperationLogService;
import cn.raxcl.util.IpAddressUtils;
import cn.raxcl.util.UserAgentUtils;

import java.util.List;
import java.util.Map;

/**
 * @Description: 操作日志业务层实现
 * @author Raxcl
 * @date 2022-01-07 15:02:36
 */
@Service
public class OperationLogServiceImpl implements OperationLogService {
	private final OperationLogMapper operationLogMapper;
	private final UserAgentUtils userAgentUtils;

	public OperationLogServiceImpl(OperationLogMapper operationLogMapper, UserAgentUtils userAgentUtils) {
		this.operationLogMapper = operationLogMapper;
		this.userAgentUtils = userAgentUtils;
	}

	@Override
	public List<OperationLog> getOperationLogListByDate(String startDate, String endDate) {
		return operationLogMapper.getOperationLogListByDate(startDate, endDate);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	@Async
	public void saveOperationLog(OperationLog log) {
		//TODO 重复代码
		String ipSource = IpAddressUtils.getCityInfo(log.getIp());
		Map<String, String> userAgentMap = userAgentUtils.parseOsAndBrowser(log.getUserAgent());
		String os = userAgentMap.get("os");
		String browser = userAgentMap.get("browser");
		log.setIpSource(ipSource);
		log.setOs(os);
		log.setBrowser(browser);
		if (operationLogMapper.saveOperationLog(log) != 1) {
			throw new PersistenceException("日志添加失败");
		}
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void deleteOperationLogById(Long id) {
		if (operationLogMapper.deleteOperationLogById(id) != 1) {
			throw new PersistenceException("删除日志失败");
		}
	}
}
