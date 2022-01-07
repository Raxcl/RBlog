package cn.raxcl.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: 登录账号密码
 * @author Raxcl
 * @date 2022-01-07 09:26:14
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LoginInfoDTO {
	private String username;
	private String password;
}
