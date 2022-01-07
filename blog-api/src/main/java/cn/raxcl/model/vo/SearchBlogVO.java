package cn.raxcl.model.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: 关键字搜索博客
 * @author Raxcl
 * @date 2022-01-07 09:33:52
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SearchBlogVO {
	private Long id;
	private String title;
	private String content;
}
