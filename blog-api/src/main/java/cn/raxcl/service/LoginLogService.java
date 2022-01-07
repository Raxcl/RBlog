package cn.raxcl.service;

import cn.raxcl.entity.LoginLog;

import java.util.List;

/**
 * @author c-long.chan
 * @date 2022-01-07 12:32:46
 */
public interface LoginLogService {
	/**
	 * 查询登录日志列表
	 * @param startDate 开始时间
	 * @param endDate 结束时间
	 * @return List<LoginLog>
	 */
	List<LoginLog> getLoginLogListByDate(String startDate, String endDate);

	/**
	 * 保存登录日志
	 * @param log log
	 */
	void saveLoginLog(LoginLog log);

	/**
	 * 根据id删除登录日志
	 * @param id id
	 */
	void deleteLoginLogById(Long id);

}
