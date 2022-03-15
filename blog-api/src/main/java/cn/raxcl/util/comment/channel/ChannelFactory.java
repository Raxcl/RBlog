package cn.raxcl.util.comment.channel;

import cn.raxcl.constant.CommentConstants;
import cn.raxcl.util.common.SpringContextUtils;

/**
 * 评论提醒方式
 *
 * @author Raxcl
 * @date 2022-03-15 23:47:06
 */
public class ChannelFactory {
	/**
	 * 创建评论提醒方式
	 *
	 * @param channelName 方式名称
	 * @return
	 */
	public static CommentNotifyChannel getChannel(String channelName) {
		if (CommentConstants.TELEGRAM.equalsIgnoreCase(channelName)) {
			return SpringContextUtils.getBean("telegramChannel", CommentNotifyChannel.class);
		} else if (CommentConstants.MAIL.equalsIgnoreCase(channelName)) {
			return SpringContextUtils.getBean("mailChannel", CommentNotifyChannel.class);
		}
		throw new RuntimeException("Unsupported value in [application.properties]: [comment.notify.channel]");
	}
}
