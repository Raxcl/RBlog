package cn.raxcl.service.impl;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.raxcl.constant.RedisKeyConstant;
import cn.raxcl.entity.Visitor;
import cn.raxcl.exception.PersistenceException;
import cn.raxcl.mapper.VisitorMapper;
import cn.raxcl.model.dto.VisitLogUuidTimeDTO;
import cn.raxcl.service.RedisService;
import cn.raxcl.service.VisitorService;
import cn.raxcl.util.IpAddressUtils;
import cn.raxcl.util.UserAgentUtils;

import java.util.List;
import java.util.Map;

/**
 * @Description: 访客统计业务层实现
 * @author Raxcl
 * @date 2022-01-07 13:01:42
 */
@Service
public class VisitorServiceImpl implements VisitorService {
	private final VisitorMapper visitorMapper;
	private final RedisService redisService;
	private final UserAgentUtils userAgentUtils;

	public VisitorServiceImpl(VisitorMapper visitorMapper, RedisService redisService, UserAgentUtils userAgentUtils) {
		this.visitorMapper = visitorMapper;
		this.redisService = redisService;
		this.userAgentUtils = userAgentUtils;
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
		//TODO 重复代码
		String ipSource = IpAddressUtils.getCityInfo(visitor.getIp());
		Map<String, String> userAgentMap = userAgentUtils.parseOsAndBrowser(visitor.getUserAgent());
		String os = userAgentMap.get("os");
		String browser = userAgentMap.get("browser");
		visitor.setIpSource(ipSource);
		visitor.setOs(os);
		visitor.setBrowser(browser);
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
		redisService.deleteValueBySet(RedisKeyConstant.IDENTIFICATION_SET, uuid);
		if (visitorMapper.deleteVisitorById(id) != 1) {
			throw new PersistenceException("删除访客失败");
		}
	}
}
