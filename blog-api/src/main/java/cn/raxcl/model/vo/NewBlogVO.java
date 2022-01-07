package cn.raxcl.model.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: 最新推荐博客
 * @author Raxcl
 * @date 2022-01-07 09:33:41
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class NewBlogVO {
	private Long id;
	private String title;
	private String password;
	private Boolean privacy;
}
