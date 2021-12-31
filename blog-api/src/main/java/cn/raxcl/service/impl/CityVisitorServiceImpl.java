package cn.raxcl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.raxcl.entity.CityVisitor;
import cn.raxcl.mapper.CityVisitorMapper;
import cn.raxcl.service.CityVisitorService;

/**
 * @Description: 城市访客数量统计业务层实现
 * @author Raxcl
 * @date 2021-02-26
 */
@Service
public class CityVisitorServiceImpl implements CityVisitorService {
	@Autowired
	CityVisitorMapper cityVisitorMapper;

	@Override
	public void saveCityVisitor(CityVisitor cityVisitor) {
		cityVisitorMapper.saveCityVisitor(cityVisitor);
	}
}
