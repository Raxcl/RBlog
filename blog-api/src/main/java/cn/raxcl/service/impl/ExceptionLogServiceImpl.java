package cn.raxcl.service.impl;

import cn.raxcl.common.CommonService;
import cn.raxcl.model.temp.LogDTO;
import org.springframework.beans.BeanUtils;
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
	private final CommonService commonService;

	public ExceptionLogServiceImpl(ExceptionLogMapper exceptionLogMapper, UserAgentUtils userAgentUtils, CommonService commonService) {
		this.exceptionLogMapper = exceptionLogMapper;
		this.userAgentUtils = userAgentUtils;
		this.commonService = commonService;
	}

	@Override
	public List<ExceptionLog> getExceptionLogListByDate(String startDate, String endDate) {
		return exceptionLogMapper.getExceptionLogListByDate(startDate, endDate);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	@Async
	public void saveExceptionLog(ExceptionLog log) {
		LogDTO logDTO = commonService.saveLog(log.getIp(), log.getUserAgent());
		BeanUtils.copyProperties(logDTO, log);
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
