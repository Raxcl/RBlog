package cn.raxcl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.raxcl.entity.Category;
import cn.raxcl.entity.Tag;
import cn.raxcl.model.vo.NewBlog;
import cn.raxcl.model.vo.RandomBlog;
import cn.raxcl.model.vo.Result;
import cn.raxcl.service.BlogService;
import cn.raxcl.service.CategoryService;
import cn.raxcl.service.SiteSettingService;
import cn.raxcl.service.TagService;

import java.util.List;
import java.util.Map;

/**
 * @Description: 站点相关
 * @Author: Raxcl
 * @Date: 2020-08-09
 */

@RestController
public class IndexController {
	@Autowired
	SiteSettingService siteSettingService;
	@Autowired
	BlogService blogService;
	@Autowired
	CategoryService categoryService;
	@Autowired
	TagService tagService;

	/**
	 * 获取站点配置信息、最新推荐博客、分类列表、标签云、随机博客
	 *
	 * @return
	 */
	@GetMapping("/site")
	public Result site() {
		Map<String, Object> map = siteSettingService.getSiteInfo();
		List<NewBlog> newBlogList = blogService.getNewBlogListByIsPublished();
		List<Category> categoryList = categoryService.getCategoryNameList();
		List<Tag> tagList = tagService.getTagListNotId();
		List<RandomBlog> randomBlogList = blogService.getRandomBlogListByLimitNumAndIsPublishedAndIsRecommend();
		map.put("newBlogList", newBlogList);
		map.put("categoryList", categoryList);
		map.put("tagList", tagList);
		map.put("randomBlogList", randomBlogList);
		return Result.ok("请求成功", map);
	}
}
