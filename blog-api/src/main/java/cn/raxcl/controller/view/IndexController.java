package cn.raxcl.controller.view;

import cn.raxcl.model.vo.NewBlogVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.raxcl.entity.Category;
import cn.raxcl.entity.Tag;
import cn.raxcl.model.vo.RandomBlogVO;
import cn.raxcl.util.common.Result;
import cn.raxcl.service.BlogService;
import cn.raxcl.service.CategoryService;
import cn.raxcl.service.SiteSettingService;
import cn.raxcl.service.TagService;

import java.util.List;
import java.util.Map;

/**
 * @Description: 站点相关
 * @author Raxcl
 * @date 2022-01-07 15:42:18
 */

@RestController
public class IndexController {
	private final SiteSettingService siteSettingService;
	private final BlogService blogService;
	private final CategoryService categoryService;
	private final TagService tagService;

	public IndexController(SiteSettingService siteSettingService, BlogService blogService, CategoryService categoryService, TagService tagService) {
		this.siteSettingService = siteSettingService;
		this.blogService = blogService;
		this.categoryService = categoryService;
		this.tagService = tagService;
	}

	/**
	 * 获取站点配置信息、最新推荐博客、分类列表、标签云、随机博客
	 *
	 * @return Result
	 */
	@GetMapping("/site")
	public Result site() {
		Map<String, Object> map = siteSettingService.getSiteInfo();
		List<NewBlogVO> newBlogVOList = blogService.getNewBlogListByIsPublished();
		List<Category> categoryList = categoryService.getCategoryNameList();
		List<Tag> tagList = tagService.getTagListNotId();
		List<RandomBlogVO> randomBlogVOList = blogService.getRandomBlogListByLimitNumAndIsPublishedAndIsRecommend();
		map.put("newBlogList", newBlogVOList);
		map.put("categoryList", categoryList);
		map.put("tagList", tagList);
		map.put("randomBlogList", randomBlogVOList);
		return Result.success("请求成功", map);
	}
}
