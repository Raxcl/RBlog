package cn.raxcl.model.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import cn.raxcl.model.bean.Favorite;

import java.util.List;

/**
 * @Description: 侧边栏资料卡
 * @author Raxcl
 * @date 2022-01-07 09:33:38
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class IntroductionVO {
	private String avatar;
	private String name;
	private String github;
	private String qq;
	private String bilibili;
	private String netease;
	private String email;

	private List<String> rollText;
	private List<Favorite> favorites;

}
