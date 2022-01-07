package cn.raxcl.service;

import cn.raxcl.entity.OperationLog;

import java.util.List;

/**
 * @author c-long.chan
 * @date 2022-01-07 15:02:16
 */
public interface OperationLogService {
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
	 */
	void saveOperationLog(OperationLog log);

	/**
	 * 删除日志
	 * @param id id
	 */
	void deleteOperationLogById(Long id);
}
