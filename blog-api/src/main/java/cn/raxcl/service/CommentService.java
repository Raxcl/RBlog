package cn.raxcl.service;

import cn.raxcl.entity.Comment;
import cn.raxcl.model.dto.CommentDTO;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author c-long.chan
 * @date 2022-01-05 20:06:41
 */
public interface CommentService {
	
	/**
	 * 根据页面分页查询评论列表
	 * @param page 页面分类（0普通文章，1关于我，2友链）
	 * @param blogId 如果page==0，需要博客id参数
	 * @param pageNum 页码
	 * @param pageSize 每页个数
	 * @param jwt 若文章受密码保护，需要获取访问Token
	 * @return Result
	 */
	Map<String, Object> comments(Integer page, Long blogId, Integer pageNum, Integer pageSize, String jwt);

	/**
	 * 提交评论
	 * @param commentDTO commentDTO
	 * @param request request
	 * @param jwt jwt
	 */
	void postComment(CommentDTO commentDTO, HttpServletRequest request, String jwt);

	/**
	 * 保存评论
	 * @param commentDTO commentDTO
	 */
	void saveComment(CommentDTO commentDTO);

	/**
	 * 后台管理页面查询评论list
	 * @param page 页面分类（0普通文章，1关于我，2友链）
	 * @param blogId 如果是博客文章页面 需要提供博客id
	 * @param parentCommentId 父评论id
	 * @return List<Comment>
	 */
	List<Comment> getListByPageAndParentCommentId(Integer page, Long blogId, Long parentCommentId);


	/**
	 * 更新评论公开状态
	 * @param commentId 评论id
	 * @param published 是否发版
	 */
	void updateCommentPublishedById(Long commentId, Boolean published);

	/**
	 * 更新评论接收邮件提醒状态
	 * @param commentId 评论id
	 * @param notice 是否通知
	 */
	void updateCommentNoticeById(Long commentId, Boolean notice);

	/**
	 * 按id删除该评论及其所有子评论
	 * @param commentId 评论id
	 */
	void deleteCommentById(Long commentId);

	/**
	 * 按博客id删除博客下所有评论
	 * @param blogId 博客id
	 */
	void deleteCommentsByBlogId(Long blogId);

	/**
	 * 修改评论
	 * @param comment 评论dto
	 */
	void updateComment(Comment comment);

}
