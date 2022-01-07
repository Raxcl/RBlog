package cn.raxcl.model.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: 归档页面博客简要信息
 * @author Raxcl
 * @date 2022-01-07 09:27:10
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ArchiveBlogVO {
	private Long id;
	private String title;
	private String day;
	private String password;
	private Boolean privacy;
}
