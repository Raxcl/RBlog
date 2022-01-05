package cn.raxcl.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
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
 * @date 2020-09-01
 */
@RestController
@RequestMapping("/admin")
public class AboutAdminController {
	@Autowired
	AboutService aboutService;

	/**
	 * 获取关于我页面配置
	 *
	 * @return
	 */
	@GetMapping("/about")
	public Result about() {
		return Result.success("请求成功", aboutService.getAboutSetting());
	}

	/**
	 * 修改关于我页面
	 *
	 * @param map
	 * @return
	 */
	@OperationLogger("修改关于我页面")
	@PutMapping("/about")
	public Result updateAbout(@RequestBody Map<String, String> map) {
		aboutService.updateAbout(map);
		return Result.success("修改成功");
	}
}
