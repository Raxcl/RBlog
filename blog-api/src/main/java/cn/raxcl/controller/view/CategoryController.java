package cn.raxcl.controller.view;

import cn.raxcl.enums.VisitBehavior;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cn.raxcl.annotation.VisitLogger;
import cn.raxcl.model.vo.BlogInfoVO;
import cn.raxcl.model.vo.PageResultVO;
import cn.raxcl.util.common.Result;
import cn.raxcl.service.BlogService;

/**
 * 分类
 * @author Raxcl
 * @date 2022-01-07 09:11:40
 */
@RestController
public class CategoryController {
	private final BlogService blogService;

	public CategoryController(BlogService blogService) {
		this.blogService = blogService;
	}

	/**
	 * 根据分类name分页查询公开博客列表
	 *
	 * @param categoryName 分类name
	 * @param pageNum      页码
	 * @return Result
	 */
	@VisitLogger(VisitBehavior.CATEGORY)
	@GetMapping("/category")
	public Result category(@RequestParam String categoryName,
	                       @RequestParam(defaultValue = "1") Integer pageNum) {
		PageResultVO<BlogInfoVO> pageResultVO = blogService.getBlogInfoListByCategoryNameAndIsPublished(categoryName, pageNum);
		return Result.success("请求成功", pageResultVO);
	}
}
