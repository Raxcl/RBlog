package cn.raxcl.model.bean;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 自定义爱好
 * @author Raxcl
 * @date 2022-01-07 18:43:54
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Favorite {
	private String title;
	private String content;
}
