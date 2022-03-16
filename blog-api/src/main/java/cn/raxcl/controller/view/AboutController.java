package cn.raxcl.controller.view;

import cn.raxcl.enums.VisitBehavior;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.raxcl.annotation.VisitLogger;
import cn.raxcl.util.common.Result;
import cn.raxcl.service.AboutService;

/**
 * 关于我页面
 * @author Raxcl
 * @date 2022-01-07 10:15:25
 */
@RestController
public class AboutController {
	private final AboutService aboutService;

	public AboutController(AboutService aboutService) {
		this.aboutService = aboutService;
	}

	/**
	 * 获取关于我页面信息
	 *
	 * @return Result
	 */
	@VisitLogger(VisitBehavior.ABOUT)
	@GetMapping("/about")
	public Result about() {
		return Result.success("获取成功", aboutService.getAboutInfo());
	}
}
