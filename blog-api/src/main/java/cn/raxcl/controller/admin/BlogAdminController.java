package cn.raxcl.controller.admin;

import cn.raxcl.annotation.OperationLogger;
import cn.raxcl.entity.Blog;
import cn.raxcl.entity.Category;
import cn.raxcl.entity.Tag;
import cn.raxcl.model.dto.BlogDTO;
import cn.raxcl.model.dto.BlogVisibilityDTO;
import cn.raxcl.util.common.Result;
import cn.raxcl.service.BlogService;
import cn.raxcl.service.CategoryService;
import cn.raxcl.service.CommentService;
import cn.raxcl.service.TagService;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 博客文章后台管理
 * @author Raxcl
 * @date 2022-01-06 16:23:04
 */
@RestController
@RequestMapping("/admin")
public class BlogAdminController {
	private final BlogService blogService;
	private final CategoryService categoryService;
	private final TagService tagService;
	private final CommentService commentService;

	public BlogAdminController(BlogService blogService, CategoryService categoryService, TagService tagService, CommentService commentService) {
		this.blogService = blogService;
		this.categoryService = categoryService;
		this.tagService = tagService;
		this.commentService = commentService;
	}

	/**
	 * 获取博客文章列表
	 *
	 * @param title      按标题模糊查询
	 * @param categoryId 按分类id查询
	 * @param pageNum    页码
	 * @param pageSize   每页个数
	 * @return Result
	 */
	@GetMapping("/blogs")
	public Result blogs(@RequestParam(defaultValue = "") String title,
	                    @RequestParam(defaultValue = "") Integer categoryId,
	                    @RequestParam(defaultValue = "1") Integer pageNum,
	                    @RequestParam(defaultValue = "10") Integer pageSize) {
		String orderBy = "create_time desc";
		PageMethod.startPage(pageNum, pageSize, orderBy);
		PageInfo<Blog> pageInfo = new PageInfo<>(blogService.getListByTitleAndCategoryId(title, categoryId));
		List<Category> categories = categoryService.getCategoryList();
		Map<String, Object> map = new HashMap<>(16);
		map.put("blogs", pageInfo);
		map.put("categories", categories);
		return Result.success("请求成功", map);
	}

	/**
	 * 删除博客文章、删除博客文章下的所有评论、同时维护 blog_tag 表
	 *
	 * @param id 文章id
	 * @return Result
	 */
	@OperationLogger("删除博客")
	@DeleteMapping("/blog")
	public Result delete(@RequestParam Long id) {
		blogService.deleteBlogTagByBlogId(id);
		blogService.deleteBlogById(id);
		commentService.deleteCommentsByBlogId(id);
		return Result.success("删除成功");
	}

	/**
	 * 获取分类列表和标签列表
	 *
	 * @return Result
	 */
	@GetMapping("/categoryAndTag")
	public Result categoryAndTag() {
		List<Category> categories = categoryService.getCategoryList();
		List<Tag> tags = tagService.getTagList();
		Map<String, Object> map = new HashMap<>(16);
		map.put("categories", categories);
		map.put("tags", tags);
		return Result.success("请求成功", map);
	}

	/**
	 * 更新博客置顶状态
	 *
	 * @param id  博客id
	 * @param top 是否置顶
	 * @return Result
	 */
	@OperationLogger("更新博客置顶状态")
	@PutMapping("/blog/top")
	public Result updateTop(@RequestParam Long id, @RequestParam Boolean top) {
		blogService.updateBlogTopById(id, top);
		return Result.success("操作成功");
	}

	/**
	 * 更新博客推荐状态
	 *
	 * @param id        博客id
	 * @param recommend 是否推荐
	 * @return Result
	 */
	@OperationLogger("更新博客推荐状态")
	@PutMapping("/blog/recommend")
	public Result updateRecommend(@RequestParam Long id, @RequestParam Boolean recommend) {
		blogService.updateBlogRecommendById(id, recommend);
		return Result.success("操作成功");
	}

	/**
	 * 更新博客可见性状态
	 *
	 * @param id             博客id
	 * @param blogVisibilityDTO 博客可见性DTO
	 * @return Result
	 */
	@OperationLogger("更新博客可见性状态")
	@PutMapping("blog/{id}/visibility")
	public Result updateVisibility(@PathVariable Long id, @RequestBody BlogVisibilityDTO blogVisibilityDTO) {
		blogService.updateBlogVisibilityById(id, blogVisibilityDTO);
		return Result.success("操作成功");
	}

	/**
	 * 按id获取博客详情
	 *
	 * @param id 博客id
	 * @return Result
	 */
	@GetMapping("/blog")
	public Result getBlog(@RequestParam Long id) {
		Blog blog = blogService.getBlogById(id);
		return Result.success("获取成功", blog);
	}

	/**
	 * 保存草稿或发布新文章
	 *
	 * @param blogDTO 博客文章DTO
	 * @return Result
	 */
	@OperationLogger("发布博客")
	@PostMapping("/blog")
	public Result saveBlog(@RequestBody BlogDTO blogDTO) {
		return blogService.getResult(blogDTO, "save");
	}

	/**
	 * 更新博客
	 *
	 * @param blogDTO 博客文章DTO
	 * @return Result
	 */
	@OperationLogger("更新博客")
	@PutMapping("/blog")
	public Result updateBlog(@RequestBody BlogDTO blogDTO) {
		return blogService.getResult(blogDTO, "update");
	}

}
