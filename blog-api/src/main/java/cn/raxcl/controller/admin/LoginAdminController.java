package cn.raxcl.controller.admin;

import cn.raxcl.constant.CodeConstants;
import cn.raxcl.constant.CommonConstants;
import cn.raxcl.entity.User;
import cn.raxcl.model.dto.LoginInfoDTO;
import cn.raxcl.service.UserService;
import cn.raxcl.util.JwtUtils;
import cn.raxcl.util.common.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 前台登录
 * @author Raxcl
 * @date 2022-03-15 12:35:50
 */
@RestController
@RequestMapping("/admin")
public class LoginAdminController {

	private final UserService userService;

	public LoginAdminController(UserService userService) {
		this.userService = userService;
	}

	/**
	 * 登录成功后，签发博主身份Token
	 *
	 * @param loginInfoDTO loginInfoDTO
	 * @return Result
	 */
	@PostMapping("/login")
	public Result login(@RequestBody LoginInfoDTO loginInfoDTO) {
		User user = userService.findUserByUsernameAndPassword(loginInfoDTO.getUsername(), loginInfoDTO.getPassword());
		if (!CommonConstants.ROLE_ADMIN.equals(user.getRole())) {
			return Result.exception(403, "无权限");
		}
		user.setPassword(null);
		String jwt = JwtUtils.generateToken("admin:" + user.getUsername(), CodeConstants.EXPIRE_TIME, CodeConstants.SECRET_KEY);
		Map<String, Object> map = new HashMap<>(16);
		map.put("user", user);
		map.put("token", jwt);
		return Result.success("登录成功", map);
	}
}
