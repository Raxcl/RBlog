package cn.raxcl.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: 受保护文章密码DTO
 * @author Raxcl
 * @date 2022-01-07 09:12:49
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BlogPasswordDTO {
	private Long blogId;
	private String password;
}
