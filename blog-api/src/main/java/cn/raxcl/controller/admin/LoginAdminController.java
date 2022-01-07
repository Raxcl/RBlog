package cn.raxcl.controller.admin;

import cn.raxcl.constant.CommonConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.raxcl.entity.User;
import cn.raxcl.model.dto.LoginInfoDTO;
import cn.raxcl.util.common.Result;
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
	 * @param loginInfoDTO
	 * @return
	 */
	@PostMapping("/login")
	public Result login(@RequestBody LoginInfoDTO loginInfoDTO) {
		User user = userService.findUserByUsernameAndPassword(loginInfoDTO.getUsername(), loginInfoDTO.getPassword());
		if (!CommonConstant.ROLE_ADMIN.equals(user.getRole())) {
			return Result.exception(403, "无权限");
		}
		user.setPassword(null);
		String jwt = JwtUtils.generateToken("admin:" + user.getUsername(),expireTime, secretKey);
		Map<String, Object> map = new HashMap<>(8);
		map.put("user", user);
		map.put("token", jwt);
		return Result.success("登录成功", map);
	}
}
