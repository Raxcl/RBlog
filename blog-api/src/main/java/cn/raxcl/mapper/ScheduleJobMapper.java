package cn.raxcl.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import cn.raxcl.entity.ScheduleJob;

import java.util.List;

/**
 * @Description: 定时任务持久层接口
 * @author Raxcl
 * @date 2020-11-01
 */
@Mapper
@Repository
public interface ScheduleJobMapper {
	List<ScheduleJob> getJobList();

	ScheduleJob getJobById(Long jobId);

	int saveJob(ScheduleJob scheduleJob);

	int updateJob(ScheduleJob scheduleJob);

	int deleteJobById(Long jobId);

	int updateJobStatusById(Long jobId, Boolean status);
}
