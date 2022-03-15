package cn.raxcl.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * 访客记录
 * @author Raxcl
 * @date 2022-01-07 10:11:52
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Visitor {
	private Long id;
	/**
	 * 访客标识码
	 */
	private String uuid;
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
	 * 首次访问时间
	 */
	private Date createTime;
	/**
	 * 最后访问时间
	 */
	private Date lastTime;
	/**
	 * 访问页数统计
	 */
	private Integer pv;
	private String userAgent;

	public Visitor(String uuid, String ip, String userAgent) {
		this.uuid = uuid;
		this.ip = ip;
		Date date = new Date();
		this.createTime = date;
		this.lastTime = date;
		this.pv = 0;
		this.userAgent = userAgent;
	}
}
