package cn.raxcl.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: 访问记录
 * @author Raxcl
 * @date 2022-01-07 10:13:21
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class VisitRecord {
	private Long id;
	/**
	 * 访问量
	 */
	private Integer pv;
	/**
	 * 独立用户
	 */
	private Integer uv;
	/**
	 * 日期"02-23"
	 */
	private String date;

	public VisitRecord(Integer pv, Integer uv, String date) {
		this.pv = pv;
		this.uv = uv;
		this.date = date;
	}
}
