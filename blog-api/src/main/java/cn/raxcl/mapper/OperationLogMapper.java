package cn.raxcl.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import cn.raxcl.entity.OperationLog;

import java.util.List;

/**
 * @Description: 操作日志持久层接口
 * @Author: Raxcl
 * @Date: 2020-11-30
 */
@Mapper
@Repository
public interface OperationLogMapper {
	List<OperationLog> getOperationLogListByDate(String startDate, String endDate);

	int saveOperationLog(OperationLog log);

	int deleteOperationLogById(Long id);
}
