package cn.raxcl.controller.admin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.raxcl.entity.User;
import cn.raxcl.model.dto.LoginInfo;
import cn.raxcl.model.vo.Result;
import cn.raxcl.service.UserService;
import cn.raxcl.util.JwtUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 前台登录
 * @author Raxcl
 * @date 2022-01-04 15:03:38
 */
@RestController
@RequestMapping("/admin")
public class LoginAdminController {
	@Value("${token.secretKey}")
	private String secretKey;
	@Value("${token.expireTime}")
	private Long expireTime;

	private final UserService userService;

	public LoginAdminController(UserService userService) {
		this.userService = userService;
	}

	/**
	 * 登录成功后，签发博主身份Token
	 *
	 * @param loginInfo
	 * @return
	 */
	@PostMapping("/login")
	public Result login(@RequestBody LoginInfo loginInfo) {
		User user = userService.findUserByUsernameAndPassword(loginInfo.getUsername(), loginInfo.getPassword());
		if (!"ROLE_admin".equals(user.getRole())) {
			return Result.create(403, "无权限");
		}
		user.setPassword(null);
		String jwt = JwtUtils.generateToken("admin:" + user.getUsername(),expireTime, secretKey);
		Map<String, Object> map = new HashMap<>();
		map.put("user", user);
		map.put("token", jwt);
		return Result.ok("登录成功", map);
	}
}
