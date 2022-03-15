package cn.raxcl.entity;

import lombok.*;

import java.util.Date;

/**
 * 访问日志
 * @author Raxcl
 * @date 2022-01-07 10:04:44
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class VisitLog {
	private Long id;
	/**
	 * 访客标识码
	 */
	private String uuid;
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
	 * 访问行为
	 */
	private String behavior;
	/**
	 * 访问内容
	 */
	private String content;
	/**
	 * 备注
	 */
	private String remark;
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
	 * 请求耗时（毫秒）
	 */
	private Integer times;
	/**
	 * 访问时间
	 */
	private Date createTime;
	private String userAgent;

}
