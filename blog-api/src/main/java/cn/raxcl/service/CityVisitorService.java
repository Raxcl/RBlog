package cn.raxcl.service;

import cn.raxcl.entity.CityVisitor;

/**
 * @author c-long.chan
 * @date 2022-01-07 16:03:47
 */
public interface CityVisitorService {
	/**
	 * 添加访问记录
	 * @param cityVisitor cityVisitor
	 */
	void saveCityVisitor(CityVisitor cityVisitor);
}
