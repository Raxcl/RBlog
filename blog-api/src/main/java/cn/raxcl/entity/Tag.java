package cn.raxcl.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * 博客标签
 * @author Raxcl
 * @date 2022-01-06 20:17:48
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Tag {
	private Long id;
	/**
	 * 标签名称
	 */
	private String name;
	/**
	 * 标签颜色(与Semantic UI提供的颜色对应，可选)
	 */
	private String color;
	/**
	 * 该标签下的博客文章
	 */
	private List<Blog> blogs = new ArrayList<>();
}
