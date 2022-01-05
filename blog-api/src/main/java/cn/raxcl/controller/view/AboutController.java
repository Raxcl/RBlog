package cn.raxcl.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.raxcl.annotation.VisitLogger;
import cn.raxcl.model.vo.Result;
import cn.raxcl.service.AboutService;

/**
 * @Description: 关于我页面
 * @author Raxcl
 * @date 2020-08-31
 */
@RestController
public class AboutController {
	@Autowired
	AboutService aboutService;

	/**
	 * 获取关于我页面信息
	 *
	 * @return
	 */
	@VisitLogger(behavior = "访问页面", content = "关于我")
	@GetMapping("/about")
	public Result about() {
		return Result.success("获取成功", aboutService.getAboutInfo());
	}
}
