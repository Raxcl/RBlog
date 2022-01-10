package cn.raxcl.service.impl;

import cn.raxcl.common.LogService;
import cn.raxcl.model.temp.LogDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.raxcl.entity.OperationLog;
import cn.raxcl.exception.PersistenceException;
import cn.raxcl.mapper.OperationLogMapper;
import cn.raxcl.service.OperationLogService;

import java.util.List;

/**
 * @Description: 操作日志业务层实现
 * @author Raxcl
 * @date 2022-01-07 15:02:36
 */
@Service
public class OperationLogServiceImpl implements OperationLogService {
	private final OperationLogMapper operationLogMapper;
	private final LogService logService;

	public OperationLogServiceImpl(OperationLogMapper operationLogMapper, LogService logService) {
		this.operationLogMapper = operationLogMapper;
		this.logService = logService;
	}

	@Override
	public List<OperationLog> getOperationLogListByDate(String startDate, String endDate) {
		return operationLogMapper.getOperationLogListByDate(startDate, endDate);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	@Async
	public void saveOperationLog(OperationLog log) {
		LogDTO logDTO = logService.saveLog(log.getIp(), log.getUserAgent());
		BeanUtils.copyProperties(logDTO, log);
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
