package cn.raxcl.service.impl;

import cn.raxcl.common.CommonService;
import cn.raxcl.model.temp.LogDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.raxcl.entity.VisitLog;
import cn.raxcl.exception.PersistenceException;
import cn.raxcl.mapper.VisitLogMapper;
import cn.raxcl.model.dto.VisitLogUuidTimeDTO;
import cn.raxcl.service.VisitLogService;

import java.util.List;

/**
 * 访问日志业务层实现
 * @author Raxcl
 * @date 2022-01-07 12:38:25
 */
@Service
public class VisitLogServiceImpl implements VisitLogService {
	private final VisitLogMapper visitLogMapper;
	private final CommonService commonService;

	public VisitLogServiceImpl(VisitLogMapper visitLogMapper, CommonService commonService) {
		this.visitLogMapper = visitLogMapper;
		this.commonService = commonService;
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
		LogDTO logDTO = commonService.saveLog(log.getIp(), log.getUserAgent());
		BeanUtils.copyProperties(logDTO, log);
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
