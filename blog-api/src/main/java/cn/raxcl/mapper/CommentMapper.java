package cn.raxcl.mapper;

import cn.raxcl.model.dto.CommentDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import cn.raxcl.entity.Comment;
import cn.raxcl.model.vo.PageComment;

import java.util.List;

/**
 * @Description: 博客评论持久层接口
 * @author Raxcl
 * @date 2022-01-06 10:12:40
 */
@Mapper
@Repository
public interface CommentMapper {
	List<Comment> getListByPageAndParentCommentId(Integer page, Long blogId, Long parentCommentId);

	List<Comment> getListByParentCommentId(Long parentCommentId);

	List<PageComment> getPageCommentListByPageAndParentCommentId(Integer page, Long blogId, Long parentCommentId);

	Comment getCommentById(Long id);

	int updateCommentPublishedById(Long commentId, Boolean published);

	int updateCommentNoticeById(Long commentId, Boolean notice);

	int deleteCommentById(Long commentId);

	int deleteCommentsByBlogId(Long blogId);

	int updateComment(Comment comment);

	int countByPageAndIsPublished(Integer page, Long blogId, Boolean isPublished);

	int countComment();

	/**
	 * 保存评论
	 * @param commentDTO commentDTO
	 * @return int
	 */
	int saveComment(CommentDTO commentDTO);
}
