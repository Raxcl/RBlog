package cn.raxcl.controller.view;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.raxcl.annotation.VisitLogger;
import cn.raxcl.util.common.Result;
import cn.raxcl.service.BlogService;

import java.util.Map;

/**
 * 归档页面
 * @author Raxcl
 * @date 2022-01-07 10:15:29
 */
@RestController
public class ArchiveController {
	private final BlogService blogService;

	public ArchiveController(BlogService blogService) {
		this.blogService = blogService;
	}

	/**
	 * 按年月分组归档公开博客 统计公开博客总数
	 *
	 * @return Result
	 */
	@VisitLogger(behavior = "访问页面", content = "文章归档")
	@GetMapping("/archives")
	public Result archives() {
		Map<String, Object> archiveBlogMap = blogService.getArchiveBlogAndCountByIsPublished();
		return Result.success("请求成功", archiveBlogMap);
	}
}
