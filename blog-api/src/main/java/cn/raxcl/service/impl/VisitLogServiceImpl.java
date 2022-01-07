package cn.raxcl.service.impl;

import cn.raxcl.service.LoginLogService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.raxcl.entity.VisitLog;
import cn.raxcl.exception.PersistenceException;
import cn.raxcl.mapper.VisitLogMapper;
import cn.raxcl.model.dto.VisitLogUuidTimeDTO;
import cn.raxcl.service.VisitLogService;
import cn.raxcl.util.IpAddressUtils;
import cn.raxcl.util.UserAgentUtils;

import java.util.List;
import java.util.Map;

/**
 * @Description: 访问日志业务层实现
 * @author Raxcl
 * @date 2022-01-07 12:38:25
 */
@Service
public class VisitLogServiceImpl implements VisitLogService {
	private final VisitLogMapper visitLogMapper;
	private final UserAgentUtils userAgentUtils;
	private final LoginLogService loginLogService;

	public VisitLogServiceImpl(VisitLogMapper visitLogMapper, UserAgentUtils userAgentUtils, LoginLogService loginLogService) {
		this.visitLogMapper = visitLogMapper;
		this.userAgentUtils = userAgentUtils;
		this.loginLogService = loginLogService;
	}

	@Override
	public List<VisitLog> getVisitLogListByUuidAndDate(String uuid, String startDate, String endDate) {
		return visitLogMapper.getVisitLogListByUuidAndDate(uuid, startDate, endDate);
	}

	@Override
	public List<VisitLogUuidTimeDTO> getUuidAndCreateTimeByYesterday() {
		return visitLogMapper.getUuidAndCreateTimeByYesterday();
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	@Async
	public void saveVisitLog(VisitLog log) {
		//TODO 重复代码 尝试提取失败
		String ipSource = IpAddressUtils.getCityInfo(log.getIp());
		Map<String, String> userAgentMap = userAgentUtils.parseOsAndBrowser(log.getUserAgent());
		String os = userAgentMap.get("os");
		String browser = userAgentMap.get("browser");
		log.setIpSource(ipSource);
		log.setOs(os);
		log.setBrowser(browser);
		if (visitLogMapper.saveVisitLog(log) != 1) {
			throw new PersistenceException("日志添加失败");
		}
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void deleteVisitLogById(Long id) {
		if (visitLogMapper.deleteVisitLogById(id) != 1) {
			throw new PersistenceException("删除日志失败");
		}
	}
}
