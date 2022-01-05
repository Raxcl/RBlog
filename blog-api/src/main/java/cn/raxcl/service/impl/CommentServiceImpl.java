package cn.raxcl.service.impl;

import cn.raxcl.constant.CodeConstant;
import cn.raxcl.constant.CommonConstant;
import cn.raxcl.entity.User;
import cn.raxcl.model.dto.CommentDTO;
import cn.raxcl.model.vo.FriendInfo;
import cn.raxcl.model.vo.PageResult;
import cn.raxcl.model.vo.Result;
import cn.raxcl.service.AboutService;
import cn.raxcl.service.BlogService;
import cn.raxcl.service.FriendService;
import cn.raxcl.util.JwtUtils;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.raxcl.entity.Comment;
import cn.raxcl.exception.PersistenceException;
import cn.raxcl.mapper.CommentMapper;
import cn.raxcl.model.vo.PageComment;
import cn.raxcl.service.CommentService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 博客评论业务层实现
 * @author Raxcl
 * @date 2020-08-03
 */
@Service
public class CommentServiceImpl implements CommentService {
	@Value("${token.secretKey}")
	private String secretKey;

	private final CommentMapper commentMapper;
	private final BlogService blogService;
	private final AboutService aboutService;
	private final FriendService friendService;
	private final UserServiceImpl userService;

	public CommentServiceImpl(CommentMapper commentMapper, BlogService blogService, AboutService aboutService, FriendService friendService, UserServiceImpl userService) {
		this.commentMapper = commentMapper;
		this.blogService = blogService;
		this.aboutService = aboutService;
		this.friendService = friendService;
		this.userService = userService;
	}

	@Override
	public List<Comment> getListByPageAndParentCommentId(Integer page, Long blogId, Long parentCommentId) {
		List<Comment> comments = commentMapper.getListByPageAndParentCommentId(page, blogId, parentCommentId);
		for (Comment c : comments) {
			//递归查询子评论及其子评论
			List<Comment> replyComments = getListByPageAndParentCommentId(page, blogId, c.getId());
			c.setReplyComments(replyComments);
		}
		return comments;
	}

	@Override
	public List<PageComment> getPageCommentList(Integer page, Long blogId, Long parentCommentId) {
		List<PageComment> comments = getPageCommentListByPageAndParentCommentId(page, blogId, parentCommentId);
		for (PageComment c : comments) {
			List<PageComment> tmpComments = new ArrayList<>();
			getReplyComments(tmpComments, c.getReplyComments());
			c.setReplyComments(tmpComments);
		}
		return comments;
	}

	@Override
	public Comment getCommentById(Long id) {
		Comment comment = commentMapper.getCommentById(id);
		if (comment == null) {
			throw new PersistenceException("评论不存在");
		}
		return comment;
	}

	/**
	 * 将所有子评论递归取出到一个List中
	 *
	 * @param comments
	 * @return
	 */
	private void getReplyComments(List<PageComment> tmpComments, List<PageComment> comments) {
		for (PageComment c : comments) {
			tmpComments.add(c);
			getReplyComments(tmpComments, c.getReplyComments());
		}
	}

	private List<PageComment> getPageCommentListByPageAndParentCommentId(Integer page, Long blogId, Long parentCommentId) {
		List<PageComment> comments = commentMapper.getPageCommentListByPageAndParentCommentId(page, blogId, parentCommentId);
		for (PageComment c : comments) {
			List<PageComment> replyComments = getPageCommentListByPageAndParentCommentId(page, blogId, c.getId());
			c.setReplyComments(replyComments);
		}
		return comments;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updateCommentPublishedById(Long commentId, Boolean published) {
		if (commentMapper.updateCommentPublishedById(commentId, published) != 1) {
			throw new PersistenceException("操作失败");
		}
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updateCommentNoticeById(Long commentId, Boolean notice) {
		if (commentMapper.updateCommentNoticeById(commentId, notice) != 1) {
			throw new PersistenceException("操作失败");
		}
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void deleteCommentById(Long commentId) {
		List<Comment> comments = getAllReplyComments(commentId);
		for (Comment c : comments) {
			delete(c);
		}
		if (commentMapper.deleteCommentById(commentId) != 1) {
			throw new PersistenceException("评论删除失败");
		}
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void deleteCommentsByBlogId(Long blogId) {
		commentMapper.deleteCommentsByBlogId(blogId);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updateComment(Comment comment) {
		if (commentMapper.updateComment(comment) != 1) {
			throw new PersistenceException("评论修改失败");
		}
	}

	@Override
	public int countByPageAndIsPublished(Integer page, Long blogId, Boolean isPublished) {
		return commentMapper.countByPageAndIsPublished(page, blogId, isPublished);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void saveComment(CommentDTO commentDTO) {
		if (commentMapper.saveComment(commentDTO) != 1) {
			throw new PersistenceException("评论失败");
		}
	}

	@Override
	public Result comments(Integer page, Long blogId, Integer pageNum, Integer pageSize, String jwt) {
		//查询对应页面评论是否开启
		int judgeResult = judgeCommentEnabled(page, blogId);
		if (judgeResult == 2) {
			return Result.exception(404, "该博客不存在");
		} else if (judgeResult == 1) {
			return Result.exception(403, "评论已关闭");
		} else if (judgeResult == 3) {
			Result codeResult = checkToken(jwt, blogId);
			if (!CodeConstant.SUCCESS.equals(codeResult.getCode())){
				return codeResult;
			}
		}
		//查询该页面所有评论的数量
		Integer allComment = countByPageAndIsPublished(page, blogId, null);
		//查询该页面公开评论的数量
		Integer openComment = countByPageAndIsPublished(page, blogId, true);
		PageMethod.startPage(pageNum, pageSize);
		PageInfo<PageComment> pageInfo = new PageInfo<>(getPageCommentList(page, blogId, (long) -1));
		PageResult<PageComment> pageResult = new PageResult<>(pageInfo.getPages(), pageInfo.getList());
		Map<String, Object> map = new HashMap<>();
		map.put("allComment", allComment);
		map.put("closeComment", allComment - openComment);
		map.put("comments", pageResult);
		return Result.success("获取成功", map);
	}

	private Result checkToken(String jwt, Long blogId){
		//文章受密码保护，需要验证Token
		if (!JwtUtils.judgeTokenIsExist(jwt)) {
			return Result.exception(403, "此文章受密码保护，请验证密码！");
		}
		try {
			String subject = JwtUtils.getTokenBody(jwt, secretKey).getSubject();
			//博主身份Token
			if (subject.startsWith(CommonConstant.ADMIN)) {
				String username = subject.replace(CommonConstant.ADMIN, "");
				User admin = (User) userService.loadUserByUsername(username);
				if (admin == null) {
					return Result.exception(403, CommonConstant.SUBJECT_MSG);
				}
			//经密码验证后的Token
			} else {
				Long tokenBlogId = Long.parseLong(subject);
				//博客id不匹配，验证不通过，可能博客id改变或客户端传递了其它密码保护文章的Token
				if (!tokenBlogId.equals(blogId)) {
					return Result.exception(403, "Token不匹配，请重新验证密码！");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Result.exception(403, "Token已失效，请重新验证密码！");
		}
		return Result.success("验证通过");

	}

	/**
	 * 查询对应页面评论是否开启
	 *
	 * @param page   页面分类（0普通文章，1关于我，2友链）
	 * @param blogId 如果page==0，需要博客id参数，校验文章是否公开状态
	 * @return 0:公开可查询状态 1:评论关闭 2:该博客不存在 3:文章受密码保护
	 */
	@Override
	public int judgeCommentEnabled(Integer page, Long blogId) {
		//普通博客
		if (page == 0) {
			Boolean commentEnabled = blogService.getCommentEnabledByBlogId(blogId);
			Boolean published = blogService.getPublishedByBlogId(blogId);
			//未查询到此博客
			if (commentEnabled == null || published == null) {
				return 2;
				//博客未公开
			} else if (!published) {
				return 2;
				//博客评论已关闭
			} else if (!commentEnabled) {
				return 1;
			}
			//判断文章是否存在密码
			String password = blogService.getBlogPassword(blogId);
			if (!"".equals(password)) {
				return 3;
			}
			//关于我页面
		} else if (page == 1) {
			//页面评论已关闭
			if (!aboutService.getAboutCommentEnabled()) {
				return 1;
			}
			//友链页面
		} else if (page == 2) {
			FriendInfo friendInfo = friendService.getFriendInfo(true, false);
			if (Boolean.FALSE.equals(friendInfo.getCommentEnabled())) {
				return 1;
			}
		}
		return 0;
	}

	/**
	 * 递归删除子评论
	 *
	 * @param comment 需要删除子评论的父评论
	 * @return
	 */
	private void delete(Comment comment) {
		for (Comment c : comment.getReplyComments()) {
			delete(c);
		}
		if (commentMapper.deleteCommentById(comment.getId()) != 1) {
			throw new PersistenceException("评论删除失败");
		}
	}

	/**
	 * 按id递归查询子评论
	 *
	 * @param parentCommentId 需要查询子评论的父评论id
	 * @return
	 */
	private List<Comment> getAllReplyComments(Long parentCommentId) {
		List<Comment> comments = commentMapper.getListByParentCommentId(parentCommentId);
		for (Comment c : comments) {
			List<Comment> replyComments = getAllReplyComments(c.getId());
			c.setReplyComments(replyComments);
		}
		return comments;
	}
}
