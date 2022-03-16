package cn.raxcl.util.comment.channel;

import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import cn.raxcl.config.properties.BlogProperties;
import cn.raxcl.enums.CommentPageEnum;
import cn.raxcl.model.dto.CommentDTO;
import cn.raxcl.util.MailUtils;
import cn.raxcl.util.comment.CommentUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 邮件提醒方式
 *
 * @author Raxcl
 * @date 2022-03-15 23:47:06
 */
@Lazy
@Component
public class MailChannel implements CommentNotifyChannel {
	private final BlogProperties blogProperties;
	private final MailProperties mailProperties;
	private final MailUtils mailUtils;

	public MailChannel(BlogProperties blogProperties, MailProperties mailProperties, MailUtils mailUtils) {
		this.blogProperties = blogProperties;
		this.mailProperties = mailProperties;
		this.mailUtils = mailUtils;
	}

	/**
	 * 发送邮件提醒我自己
	 *
	 * @param commentDTO 当前收到的评论
	 */
	@Override
	public void notifyMyself(CommentDTO commentDTO) {
		CommentPageEnum commentPageEnum = CommentUtils.getCommentPageEnum(commentDTO);
		Map<String, Object> map = new HashMap<>(9);
		map.put("title", commentPageEnum.getTitle());
		map.put("time", commentDTO.getCreateTime());
		map.put("nickname", commentDTO.getNickname());
		map.put("content", commentDTO.getContent());
		map.put("ip", commentDTO.getIp());
		map.put("email", commentDTO.getEmail());
		map.put("status", Boolean.TRUE.equals(commentDTO.getPublished()) ? "公开" : "待审核");
		map.put("url", blogProperties.getView() + commentPageEnum.getPath());
		map.put("manageUrl", blogProperties.getCms() + "/comments");
		String toAccount = mailProperties.getUsername();
		String subject = blogProperties.getName() + " 收到新评论";
		mailUtils.sendHtmlTemplateMail(map, toAccount, subject, "owner.html");
	}
}
