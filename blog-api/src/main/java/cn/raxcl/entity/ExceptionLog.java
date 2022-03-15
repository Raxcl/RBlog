package cn.raxcl.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * 异常日志
 * @author Raxcl
 * @date 2022-01-07 09:55:41
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ExceptionLog {
	private Long id;
	/**
	 * 请求接口
	 */
	private String uri;
	/**
	 * 请求方式
	 */
	private String method;
	/**
	 * 请求参数
	 */
	private String param;
	/**
	 * 操作描述
	 */
	private String description;
	/**
	 * 异常信息
	 */
	private String error;
	/**
	 * ip
	 */
	private String ip;
	/**
	 * ip来源
	 */
	private String ipSource;
	/**
	 * 操作系统
	 */
	private String os;
	/**
	 * 浏览器
	 */
	private String browser;
	/**
	 * 操作时间
	 */
	private Date createTime;

	private String userAgent;

	public ExceptionLog(String uri, String method, String description, String error, String ip, String userAgent) {
		this.uri = uri;
		this.method = method;
		this.description = description;
		this.error = error;
		this.ip = ip;
		this.createTime = new Date();
		this.userAgent = userAgent;
	}
}
