package cn.raxcl.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import cn.raxcl.entity.OperationLog;

import java.util.List;

/**
 * 操作日志持久层接口
 * @author Raxcl
 * @date 2022-01-07 14:54:41
 */
@Mapper
@Repository
public interface OperationLogMapper {
	/**
	 * 查询日志
	 * @param startDate startDate
	 * @param endDate endDate
	 * @return List<OperationLog>
	 */
	List<OperationLog> getOperationLogListByDate(String startDate, String endDate);

	/**
	 * 添加日志
	 * @param log log
	 * @return int
	 */
	int saveOperationLog(OperationLog log);

	/**
	 * 删除日志
	 * @param id id
	 * @return int
	 */
	int deleteOperationLogById(Long id);
}
