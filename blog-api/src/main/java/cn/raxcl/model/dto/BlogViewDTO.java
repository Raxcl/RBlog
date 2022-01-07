package cn.raxcl.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: 博客浏览量
 * @author Raxcl
 * @date 2022-01-07 09:15:28
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BlogViewDTO {
	private Long id;
	private Integer views;
}
