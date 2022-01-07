package cn.raxcl.mapper;

import cn.raxcl.model.dto.CommentDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import cn.raxcl.entity.Comment;
import cn.raxcl.model.vo.PageCommentVO;

import java.util.List;

/**
 * @Description: 博客评论持久层接口
 * @author Raxcl
 * @date 2022-01-06 10:12:40
 */
@Mapper
@Repository
public interface CommentMapper {
	/**
	 * 后台管理页面查询评论list
	 * @param page 页面分类（0普通文章，1关于我，2友链）
	 * @param blogId 如果是博客文章页面 需要提供博客id
	 * @param parentCommentId 父评论id
	 * @return List<Comment>
	 */
	List<Comment> getListByPageAndParentCommentId(Integer page, Long blogId, Long parentCommentId);

	/**
	 * 按id查询子评论
	 * @param parentCommentId 父评论id
	 * @return List<Comment>
	 */
	List<Comment> getListByParentCommentId(Long parentCommentId);

	/**
	 * 查询页面展示的评论List
	 * @param page 页面分类（0普通文章，1关于我，2友链）
	 * @param blogId 如果是博客文章页面 需要提供博客id
	 * @param parentCommentId parentCommentId
	 * @return List<PageComment>
	 */
	List<PageCommentVO> getPageCommentListByPageAndParentCommentId(Integer page, Long blogId, Long parentCommentId);

	/**
	 * 按id查询评论
	 * @param id id
	 * @return Comment
	 */
	Comment getCommentById(Long id);

	/**
	 * 更新评论公开状态
	 * @param commentId 评论id
	 * @param published 是否公开
	 * @return int
	 */
	int updateCommentPublishedById(Long commentId, Boolean published);

	/**
	 * 更新评论接收邮件提醒状态
	 * @param commentId 评论id
	 * @param notice 是否通知
	 * @return int
	 */
	int updateCommentNoticeById(Long commentId, Boolean notice);

	/**
	 * 按id删除评论
	 * @param commentId 评论id
	 * @return int
	 */
	int deleteCommentById(Long commentId);

	/**
	 * 按博客id删除博客下所有评论
	 * @param blogId 博客id
	 * @return int
	 */
	int deleteCommentsByBlogId(Long blogId);

	/**
	 * 更新评论
	 * @param comment 评论dto
	 * @return int
	 */
	int updateComment(Comment comment);

	/**
	 * 按页面查询评论数量
	 * @param page 页面分类（0普通文章，1关于我，2友链）
	 * @param blogId 博客id
	 * @param isPublished 是否发版
	 * @return int
	 */
	int countByPageAndIsPublished(Integer page, Long blogId, Boolean isPublished);

	/**
	 * 查询所有评论数量
	 * @return int
	 */
	int countComment();

	/**
	 * 保存评论
	 * @param commentDTO commentDTO
	 * @return int
	 */
	int saveComment(CommentDTO commentDTO);
}
