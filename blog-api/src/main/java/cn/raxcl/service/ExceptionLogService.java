package cn.raxcl.service;

import cn.raxcl.entity.ExceptionLog;

import java.util.List;

/**
 * @author c-long.chan
 * @date 2022-01-07 15:58:00
 */
public interface ExceptionLogService {
	/**
	 * 查询日志
	 * @param startDate startDate
	 * @param endDate endDate
	 * @return List<ExceptionLog>
	 */
	List<ExceptionLog> getExceptionLogListByDate(String startDate, String endDate);


	/**
	 * 添加日志
	 * @param log log
	 */
	void saveExceptionLog(ExceptionLog log);

	/**
	 * 删除日志
	 * @param id id
	 */
	void deleteExceptionLogById(Long id);
}
