package cn.raxcl.model.bean;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: GitHub徽标
 * @author Raxcl
 * @date 2022-01-07 18:43:43
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Badge {
	private String title;
	private String url;
	private String subject;
	private String value;
	private String color;
}
