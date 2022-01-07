package cn.raxcl.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import cn.raxcl.entity.ScheduleJobLog;

import java.util.List;

/**
 * @Description: 定时任务日志持久层接口
 * @author Raxcl
 * @date 2022-01-07 15:11:09
 */
@Mapper
@Repository
public interface ScheduleJobLogMapper {
	/**
	 * 分页查询定时任务日志列表
	 * @param startDate startDate
	 * @param endDate endDate
	 * @return List<ScheduleJobLog>
	 */
	List<ScheduleJobLog> getJobLogListByDate(String startDate, String endDate);

	/**
	 * 保存任务日志
	 * @param jobLog jobLog
	 * @return int
	 */
	int saveJobLog(ScheduleJobLog jobLog);

	/**
	 * 按id删除任务日志
	 * @param logId logId
	 * @return int
	 */
	int deleteJobLogByLogId(Long logId);
}
