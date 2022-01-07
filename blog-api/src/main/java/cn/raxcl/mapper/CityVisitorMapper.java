package cn.raxcl.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import cn.raxcl.entity.CityVisitor;

import java.util.List;

/**
 * @Description: 城市访客数量统计持久层接口
 * @author Raxcl
 * @date 2022-01-07 15:55:29
 */
@Mapper
@Repository
public interface CityVisitorMapper {
	/**
	 * 查询城市访客数
	 * @return List<CityVisitor>
	 */
	List<CityVisitor> getCityVisitorList();

	/**
	 * 添加访问记录
	 * @param cityVisitor cityVisitor
	 * @return int
	 */
	int saveCityVisitor(CityVisitor cityVisitor);
}
