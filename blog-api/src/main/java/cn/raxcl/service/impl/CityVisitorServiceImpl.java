package cn.raxcl.service.impl;

import org.springframework.stereotype.Service;
import cn.raxcl.entity.CityVisitor;
import cn.raxcl.mapper.CityVisitorMapper;
import cn.raxcl.service.CityVisitorService;

/**
 * @Description: 城市访客数量统计业务层实现
 * @author Raxcl
 * @date 2022-01-07 15:54:12
 */
@Service
public class CityVisitorServiceImpl implements CityVisitorService {
	private final CityVisitorMapper cityVisitorMapper;

	public CityVisitorServiceImpl(CityVisitorMapper cityVisitorMapper) {
		this.cityVisitorMapper = cityVisitorMapper;
	}

	@Override
	public void saveCityVisitor(CityVisitor cityVisitor) {
		cityVisitorMapper.saveCityVisitor(cityVisitor);
	}
}
