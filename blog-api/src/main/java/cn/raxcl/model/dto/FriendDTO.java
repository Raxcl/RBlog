package cn.raxcl.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: 友链DTO
 * @author Raxcl
 * @date 2022-01-07 09:17:48
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class FriendDTO {
	private Long id;
	/**
	 * 昵称
	 */
	private String nickname;
	/**
	 * 描述
	 */
	private String description;
	/**
	 * 站点
	 */
	private String website;
	/**
	 * 头像
	 */
	private String avatar;
	/**
	 * 公开或隐藏
	 */
	private Boolean published;
}
