package cn.raxcl.service;

import cn.raxcl.entity.ScheduleJob;
import cn.raxcl.entity.ScheduleJobLog;

import java.util.List;

/**
 * @author c-long.chan
 * @date 2022-01-07 16:02:43
 */
public interface ScheduleJobService {
	/**
	 * 查询定时任务列表
	 * @return List<ScheduleJob>
	 */
	List<ScheduleJob> getJobList();

	/**
	 * 新建定时任务
	 * @param scheduleJob scheduleJob
	 */
	void saveJob(ScheduleJob scheduleJob);

	/**
	 * 修改定时任务
	 * @param scheduleJob scheduleJob
	 */
	void updateJob(ScheduleJob scheduleJob);

	/**
	 * 删除定时任务
	 * @param jobId 任务id
	 */
	void deleteJobById(Long jobId);

	/**
	 * 立即执行任务
	 * @param jobId 任务id
	 */
	void runJobById(Long jobId);

	/**
	 * 更新任务状态：暂停或恢复
	 * @param jobId 任务id
	 * @param status 状态
	 */
	void updateJobStatusById(Long jobId, Boolean status);

	/**
	 * 分页查询定时任务日志列表
	 * @param startDate startDate
	 * @param endDate endDate
	 * @return List<ScheduleJobLog>
	 */
	List<ScheduleJobLog> getJobLogListByDate(String startDate, String endDate);

	/**
	 * 保存任务日志
	 * @param log log
	 */
	void saveJobLog(ScheduleJobLog log);

	/**
	 * 按id删除任务日志
	 * @param logId logId
	 */
	void deleteJobLogByLogId(Long logId);
}
