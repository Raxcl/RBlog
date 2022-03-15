package cn.raxcl.service.impl;

import cn.raxcl.common.CommonService;
import cn.raxcl.model.temp.LogDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.raxcl.constant.RedisKeyConstants;
import cn.raxcl.entity.Visitor;
import cn.raxcl.exception.PersistenceException;
import cn.raxcl.mapper.VisitorMapper;
import cn.raxcl.model.dto.VisitLogUuidTimeDTO;
import cn.raxcl.service.RedisService;
import cn.raxcl.service.VisitorService;

import java.util.List;

/**
 * 访客统计业务层实现
 * @author Raxcl
 * @date 2022-01-07 13:01:42
 */
@Service
public class VisitorServiceImpl implements VisitorService {
	private final VisitorMapper visitorMapper;
	private final RedisService redisService;
	private final CommonService commonService;

	public VisitorServiceImpl(VisitorMapper visitorMapper, RedisService redisService, CommonService commonService) {
		this.visitorMapper = visitorMapper;
		this.redisService = redisService;
		this.commonService = commonService;
	}

	@Override
	public List<Visitor> getVisitorListByDate(String startDate, String endDate) {
		return visitorMapper.getVisitorListByDate(startDate, endDate);
	}

	@Override
	public List<String> getNewVisitorIpSourceByYesterday() {
		return visitorMapper.getNewVisitorIpSourceByYesterday();
	}

	@Override
	public boolean hasUuid(String uuid) {
		return visitorMapper.hasUuid(uuid) != 0;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	@Async
	public void saveVisitor(Visitor visitor) {
		LogDTO logDTO = commonService.saveLog(visitor.getIp(), visitor.getUserAgent());
		BeanUtils.copyProperties(logDTO, visitor);
		if (visitorMapper.saveVisitor(visitor) != 1) {
			throw new PersistenceException("访客添加失败");
		}
	}

	@Override
	public void updatePvAndLastTimeByUuid(VisitLogUuidTimeDTO dto) {
		visitorMapper.updatePvAndLastTimeByUuid(dto);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void deleteVisitor(Long id, String uuid) {
		//删除Redis中该访客的uuid
		redisService.deleteValueBySet(RedisKeyConstants.IDENTIFICATION_SET, uuid);
		if (visitorMapper.deleteVisitorById(id) != 1) {
			throw new PersistenceException("删除访客失败");
		}
	}
}
