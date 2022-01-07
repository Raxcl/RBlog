package cn.raxcl.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import cn.raxcl.entity.VisitRecord;

import java.util.List;

/**
 * @Description: 访问记录持久层接口
 * @author Raxcl
 * @date 2022-01-07 16:00:44
 */
@Mapper
@Repository
public interface VisitRecordMapper {
	/**
	 * 按天数查询访问记录
	 * @param limit limit
	 * @return List<VisitRecord>
	 */
	List<VisitRecord> getVisitRecordListByLimit(Integer limit);

	/**
	 * 添加访问记录
	 * @param visitRecord visitRecord
	 * @return int
	 */
	int saveVisitRecord(VisitRecord visitRecord);
}
