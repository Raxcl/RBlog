package cn.raxcl.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: 关于我
 * @author Raxcl
 * @date 2022-01-07 09:52:33
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class About {
	private Long id;
	private String nameEn;
	private String nameZh;
	private String value;
}
