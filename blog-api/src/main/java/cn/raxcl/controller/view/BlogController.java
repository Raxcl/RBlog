package cn.raxcl.controller.view;

import cn.raxcl.constant.CommonConstants;
import cn.raxcl.constant.JwtConstants;
import cn.raxcl.enums.VisitBehavior;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cn.raxcl.annotation.VisitLogger;
import cn.raxcl.model.dto.BlogPasswordDTO;
import cn.raxcl.model.vo.BlogDetailVO;
import cn.raxcl.model.vo.BlogInfoVO;
import cn.raxcl.model.vo.PageResultVO;
import cn.raxcl.util.common.Result;
import cn.raxcl.model.vo.SearchBlogVO;
import cn.raxcl.service.BlogService;
import cn.raxcl.util.JwtUtils;
import cn.raxcl.util.StringUtils;
import java.util.List;

/**
 * 博客相关
 * @author Raxcl
 * @date 2022-01-06 19:44:16
 */
@RestController
public class BlogController {

	private final BlogService blogService;

	public BlogController(BlogService blogService) {
		this.blogService = blogService;
	}

	/**
	 * 按置顶、创建时间排序 分页查询博客简要信息列表
	 *
	 * @param pageNum 页码
	 * @return Result
	 */
	@VisitLogger(VisitBehavior.INDEX)
	@GetMapping("/blogs")
	public Result blogs(@RequestParam(defaultValue = "1") Integer pageNum) {
		PageResultVO<BlogInfoVO> pageResultVO = blogService.getBlogInfoListByIsPublished(pageNum);
		return Result.success("请求成功", pageResultVO);
	}

	/**
	 * 按id获取公开博客详情
	 *
	 * @param id  博客id
	 * @param jwt 密码保护文章的访问Token
	 * @return Result
	 */
	@VisitLogger(VisitBehavior.BLOG)
	@GetMapping("/blog")
	public Result getBlog(@RequestParam Long id,
	                      @RequestHeader(value = "Authorization", defaultValue = "") String jwt) {
		BlogDetailVO blog = blogService.getBlog(id, jwt);
		return Result.success("获取成功", blog);
	}

	/**
	 * 校验受保护文章密码是否正确，正确则返回jwt
	 *
	 * @param blogPasswordDTO 博客id、密码
	 * @return Result
	 */
	@VisitLogger(VisitBehavior.CHECK_PASSWORD)
	@PostMapping("/checkBlogPassword")
	public Result checkBlogPassword(@RequestBody BlogPasswordDTO blogPasswordDTO) {
		String password = blogService.getBlogPassword(blogPasswordDTO.getBlogId());
		if (password.equals(blogPasswordDTO.getPassword())) {
			//生成有效时间一个月的Token
			String jwt = JwtUtils.generateToken(blogPasswordDTO.getBlogId().toString(), 1000 * 3600 * 24 * 30L, JwtConstants.SECRET_KEY);
			return Result.success("密码正确", jwt);
		} else {
			return Result.exception(403, "密码错误");
		}
	}

	/**
	 * 按关键字根据文章内容搜索公开且无密码保护的博客文章
	 *
	 * @param query 关键字字符串
	 * @return Result
	 */
	@VisitLogger(VisitBehavior.SEARCH)
	@GetMapping("/searchBlog")
	public Result searchBlog(@RequestParam String query) {
		//校验关键字字符串合法性
		if (StringUtils.isEmpty(query) || StringUtils.hasSpecialChar(query) || query.trim().length() > CommonConstants.TWENTY) {
			return Result.error("参数错误");
		}
		List<SearchBlogVO> searchBlogVOList = blogService.getSearchBlogListByQueryAndIsPublished(query.trim());
		return Result.success("获取成功", searchBlogVOList);
	}
}
