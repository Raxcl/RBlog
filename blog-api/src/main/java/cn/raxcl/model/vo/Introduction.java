package cn.raxcl.model.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import cn.raxcl.model.bean.Favorite;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 侧边栏资料卡
 * @Author: Raxcl
 * @Date: 2020-08-09
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Introduction {
	private String avatar;
	private String name;
	private String github;
	private String qq;
	private String bilibili;
	private String netease;
	private String email;

	private List<String> rollText = new ArrayList<>();
	private List<Favorite> favorites = new ArrayList<>();

}
