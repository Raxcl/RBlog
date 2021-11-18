package cn.raxcl.service;

import cn.raxcl.entity.ScheduleJob;
import cn.raxcl.entity.ScheduleJobLog;

import java.util.List;

public interface ScheduleJobService {
	List<ScheduleJob> getJobList();

	void saveJob(ScheduleJob scheduleJob);

	void updateJob(ScheduleJob scheduleJob);

	void deleteJobById(Long jobId);

	void runJobById(Long jobId);

	void updateJobStatusById(Long jobId, Boolean status);

	List<ScheduleJobLog> getJobLogListByDate(String startDate, String endDate);

	void saveJobLog(ScheduleJobLog log);

	void deleteJobLogByLogId(Long logId);
}
