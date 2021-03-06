package cn.raxcl.service.impl;

import cn.raxcl.config.properties.BlogProperties;
import cn.raxcl.constant.CommonConstants;
import cn.raxcl.model.dto.CommentDTO;
import cn.raxcl.service.BlogService;
import cn.raxcl.service.MailService;
import cn.raxcl.util.MailUtils;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author c-long.chan
 * 2022-01-06 13:14:42
 */
@Service
public class MailServiceImpl implements MailService {

    private final BlogService blogService;
    private final MailProperties mailProperties;
    private final MailUtils mailUtils;
    private final BlogProperties blogProperties;

    public MailServiceImpl(BlogService blogService, MailProperties mailProperties, MailUtils mailUtils, BlogProperties blogProperties) {
        this.blogService = blogService;
        this.mailProperties = mailProperties;
        this.mailUtils = mailUtils;
        this.blogProperties = blogProperties;
    }

    /**
     * 发送邮件提醒我自己
     *
     * @param commentDTO 当前评论
     */
    @Override
    public void sendMailToMe(CommentDTO commentDTO) {

        String path = "";
        String title = "";
        if (commentDTO.getPage().equals(CommonConstants.ZERO)) {
            //普通博客
            title = blogService.getTitleByBlogId(commentDTO.getBlogId());
            path = CommonConstants.BLOG + commentDTO.getBlogId();
        } else if (commentDTO.getPage().equals(CommonConstants.ONE)) {
            //关于我页面
            title = "关于我";
            path = CommonConstants.ABOUT;
        } else if (commentDTO.getPage().equals(CommonConstants.TWO)) {
            //友链页面
            title = "友人帐";
            path = CommonConstants.FRIENDS;
        }
        Map<String, Object> map = new HashMap<>(16);
        map.put("title", title);
        map.put("time", commentDTO.getCreateTime());
        map.put("nickname", commentDTO.getNickname());
        map.put("content", commentDTO.getContent());
        map.put("ip", commentDTO.getIp());
        map.put("email", commentDTO.getEmail());
        map.put("status", Boolean.TRUE.equals(commentDTO.getPublished()) ? "公开" : "待审核");
        map.put("url", blogProperties.getView() + path);
        map.put("manageUrl", blogProperties.getCms() + CommonConstants.COMMENTS);
        String toAccount = mailProperties.getUsername();
        String subject = blogProperties.getName() + " 收到新评论";
        mailUtils.sendHtmlTemplateMail(map, toAccount, subject, "owner.html");
    }

    @Override
    public void sendHtmlTemplateMail(Map<String, Object> map, String toAccount, String subject, String template) {
        mailUtils.sendHtmlTemplateMail(map, toAccount, subject, "guest.html");
    }
}
