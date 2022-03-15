package cn.raxcl.controller.view;

import cn.raxcl.annotation.AccessLimit;
import cn.raxcl.model.dto.CommentDTO;
import cn.raxcl.util.common.Result;
import cn.raxcl.service.CommentService;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 评论
 * @author  Raxcl
 * @date 2021-12-31 16:02:54
 */
@RestController
public class CommentController {
	private final CommentService commentService;

	public CommentController(CommentService commentService) {
		this.commentService = commentService;
	}

	/**
	 * 根据页面分页查询评论列表
	 * @param page     页面分类（0普通文章，1关于我，2友链）
	 * @param blogId   如果page==0，需要博客id参数
	 * @param pageNum  页码
	 * @param pageSize 每页个数
	 * @param jwt      若文章受密码保护，需要获取访问Token
	 * @return Result
	 */
	@GetMapping("/comments")
	public Result comments(@RequestParam Integer page,
	                       @RequestParam(defaultValue = "") Long blogId,
	                       @RequestParam(defaultValue = "1") Integer pageNum,
	                       @RequestParam(defaultValue = "10") Integer pageSize,
	                       @RequestHeader(value = "Authorization", defaultValue = "") String jwt) {
		Map<String, Object> map = commentService.comments(page, blogId, pageNum, pageSize, jwt);
		return Result.success("获取成功", map);
	}

	/**
	 * 提交评论
	 * 单个ip，10秒内允许提交1次评论
	 * @param commentDTO 评论DTO
	 * @param request 获取ip
	 * @param jwt     博主身份Token
	 * @return Result
	 */
	@AccessLimit(seconds = 10, maxCount = 1, msg = "10秒内只能提交一次评论")
	@PostMapping("/comment")
	public Result postComment(@RequestBody CommentDTO commentDTO,
	                          HttpServletRequest request,
	                          @RequestHeader(value = "Authorization", defaultValue = "") String jwt) {
		commentService.postComment(commentDTO, request, jwt);
		return Result.success("评论成功");

	}

}