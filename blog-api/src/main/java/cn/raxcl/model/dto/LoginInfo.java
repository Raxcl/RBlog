package cn.raxcl.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: 登录账号密码
 * @Author: Raxcl
 * @Date: 2020-09-02
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LoginInfo {
	private String username;
	private String password;
}
