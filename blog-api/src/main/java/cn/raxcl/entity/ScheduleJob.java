package cn.raxcl.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * 定时任务
 * @author Raxcl
 * @date 2022-01-07 10:00:10
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ScheduleJob {
	/**
	 * 任务调度参数key
	 */
	public static final String JOB_PARAM_KEY = "JOB_PARAM_KEY";
	/**
	 * 任务id
	 */
	private Long jobId;
	/**
	 * spring bean名称
	 */
	private String beanName;
	/**
	 * 方法名
	 */
	private String methodName;
	/**
	 * 参数
	 */
	private String params;
	/**
	 * cron表达式
	 */
	private String cron;
	/**
	 * 任务状态
	 */
	private Boolean status;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 创建时间
	 */
	private Date createTime;
}
