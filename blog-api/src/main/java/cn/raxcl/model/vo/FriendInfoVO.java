package cn.raxcl.model.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: 友链页面信息
 * @author Raxcl
 * @date 2022-01-07 09:33:31
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class FriendInfoVO {
	private String content;
	private Boolean commentEnabled;
}
