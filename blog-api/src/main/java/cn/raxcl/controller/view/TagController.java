package cn.raxcl.controller.view;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cn.raxcl.annotation.VisitLogger;
import cn.raxcl.model.vo.BlogInfoVO;
import cn.raxcl.model.vo.PageResultVO;
import cn.raxcl.util.common.Result;
import cn.raxcl.service.BlogService;

/**
 * @Description: 标签
 * @author Raxcl
 * @date 2022-01-07 15:43:11
 */
@RestController
public class TagController {
	private final BlogService blogService;

	public TagController(BlogService blogService) {
		this.blogService = blogService;
	}

	/**
	 * 根据标签name分页查询公开博客列表
	 *
	 * @param tagName 标签name
	 * @param pageNum 页码
	 * @return Result
	 */
	@VisitLogger(behavior = "查看标签")
	@GetMapping("/tag")
	public Result tag(@RequestParam String tagName,
	                  @RequestParam(defaultValue = "1") Integer pageNum) {
		PageResultVO<BlogInfoVO> pageResultVO = blogService.getBlogInfoListByTagNameAndIsPublished(tagName, pageNum);
		return Result.success("请求成功", pageResultVO);
	}
}
