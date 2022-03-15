package cn.raxcl.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * 定时任务日志
 * @author Raxcl
 * @date 2022-01-07 10:02:34
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ScheduleJobLog {
	/**
	 * 日志id
	 */
	private Long logId;
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
	 * 任务执行结果
	 */
	private Boolean status;
	/**
	 * 异常信息
	 */
	private String error;
	/**
	 * 耗时(单位：毫秒)
	 */
	private Integer times;
	/**
	 * 创建时间
	 */
	private Date createTime;
}
