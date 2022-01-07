package cn.raxcl.model.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: 评论管理页面按博客title查询评论
 * @author Raxcl
 * @date 2022-01-07 09:33:19
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BlogIdAndTitleVO {
	private Long id;
	private String title;
}
