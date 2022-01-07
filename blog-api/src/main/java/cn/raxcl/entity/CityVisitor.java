package cn.raxcl.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: 城市访客数量
 * @author Raxcl
 * @date 2022-01-07 09:55:12
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CityVisitor {
	/**
	 * 城市名称
	 */
	private String city;
	/**
	 * 独立访客数量
	 */
	private Integer uv;
}
