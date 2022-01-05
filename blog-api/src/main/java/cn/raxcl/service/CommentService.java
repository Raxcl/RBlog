package cn.raxcl.service;

import cn.raxcl.entity.Comment;
import cn.raxcl.model.dto.CommentDTO;
import cn.raxcl.model.vo.PageComment;
import cn.raxcl.model.vo.Result;

import java.util.List;

public interface CommentService {
	List<Comment> getListByPageAndParentCommentId(Integer page, Long blogId, Long parentCommentId);

	List<PageComment> getPageCommentList(Integer page, Long blogId, Long parentCommentId);

	Comment getCommentById(Long id);

	void updateCommentPublishedById(Long commentId, Boolean published);

	void updateCommentNoticeById(Long commentId, Boolean notice);

	void deleteCommentById(Long commentId);

	void deleteCommentsByBlogId(Long blogId);

	void updateComment(Comment comment);

	int countByPageAndIsPublished(Integer page, Long blogId, Boolean isPublished);

	void saveComment(CommentDTO commentDTO);

	/**
	 * 根据页面分页查询评论列表
	 * @param page 当前页
	 * @param blogId 博客id
	 * @param pageNum
	 * @param pageSize
	 * @param jwt
	 * @return Result
	 */
	Result comments(Integer page, Long blogId, Integer pageNum, Integer pageSize, String jwt);

	/**
	 * 查询对应页面评论是否开启
	 * @param page
	 * @param blogId
	 * @return
	 */
	int judgeCommentEnabled(Integer page, Long blogId);
}
