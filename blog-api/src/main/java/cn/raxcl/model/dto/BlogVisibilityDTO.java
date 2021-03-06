package cn.raxcl.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 博客可见性DTO
 * @author Raxcl
 * @date 2022-01-07 09:15:58
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BlogVisibilityDTO {
	/**
	 * 赞赏开关
	 */
	private Boolean appreciation;

	/**
	 * 推荐开关
	 */
	private Boolean recommend;

	/**
	 * 评论开关
	 */
	private Boolean commentEnabled;

	/**
	 * 是否置顶
	 */
	private Boolean top;

	/**
	 * 公开或私密
	 */
	private Boolean published;

	/**
	 * 密码保护
	 */
	private String password;
}
