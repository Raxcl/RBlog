package cn.raxcl.controller.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.raxcl.annotation.OperationLogger;
import cn.raxcl.model.vo.Result;
import cn.raxcl.service.AboutService;

import java.util.Map;

/**
 * @Description: 关于我页面后台管理
 * @author Raxcl
 * @date 2022-01-07 08:51:09
 */
@RestController
@RequestMapping("/admin")
public class AboutAdminController {
	private final AboutService aboutService;

	public AboutAdminController(AboutService aboutService) {
		this.aboutService = aboutService;
	}

	/**
	 * 获取关于我页面配置
	 *
	 * @return Result
	 */
	@GetMapping("/about")
	public Result about() {
		return Result.success("请求成功", aboutService.getAboutSetting());
	}

	/**
	 * 修改关于我页面
	 *
	 * @param map map
	 * @return Result
	 */
	@OperationLogger("修改关于我页面")
	@PutMapping("/about")
	public Result updateAbout(@RequestBody Map<String, String> map) {
		aboutService.updateAbout(map);
		return Result.success("修改成功");
	}
}
