package cn.raxcl.model.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: 标签和博客数量
 * @author Raxcl
 * @date 2022-01-07 09:33:56
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TagBlogCountVO {
	private Long id;
	/**
	 * 标签名
	 */
	private String name;
	/**
	 * 标签下博客数量
	 */
	private Integer value;
}
