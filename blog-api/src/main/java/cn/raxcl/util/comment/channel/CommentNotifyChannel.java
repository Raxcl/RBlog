package cn.raxcl.util.comment.channel;

import cn.raxcl.model.dto.CommentDTO;

/**
 * 评论提醒方式
 *
 * @author Raxcl
 * @date 2022-03-15 23:47:06
 */
public interface CommentNotifyChannel {
	/**
	 * 通过指定方式通知自己
	 *
	 * @param commentDTO 当前收到的评论
	 */
	void notifyMyself(CommentDTO commentDTO);
}
