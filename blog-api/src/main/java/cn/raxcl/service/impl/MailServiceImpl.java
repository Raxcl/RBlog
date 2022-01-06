package cn.raxcl.service.impl;

import cn.raxcl.constant.CommonConstant;
import cn.raxcl.model.dto.CommentDTO;
import cn.raxcl.service.BlogService;
import cn.raxcl.service.MailService;
import cn.raxcl.util.MailUtils;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${custom.blog.name}")
    public String blogName;
    @Value("${custom.url.cms}")
    public String cmsUrl;
    @Value("${custom.url.website}")
    public String websiteUrl;

    private final BlogService blogService;
    private final MailProperties mailProperties;
    private final MailUtils mailUtils;

    public MailServiceImpl(BlogService blogService, MailProperties mailProperties, MailUtils mailUtils) {
        this.blogService = blogService;
        this.mailProperties = mailProperties;
        this.mailUtils = mailUtils;
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
        if (commentDTO.getPage().equals(CommonConstant.ZERO)) {
            //普通博客
            title = blogService.getTitleByBlogId(commentDTO.getBlogId());
            path = CommonConstant.BLOG + commentDTO.getBlogId();
        } else if (commentDTO.getPage().equals(CommonConstant.ONE)) {
            //关于我页面
            title = "关于我";
            path = CommonConstant.ABOUT;
        } else if (commentDTO.getPage().equals(CommonConstant.TWO)) {
            //友链页面
            title = "友人帐";
            path = CommonConstant.FRIENDS;
        }
        Map<String, Object> map = new HashMap<>(8);
        map.put("title", title);
        map.put("time", commentDTO.getCreateTime());
        map.put("nickname", commentDTO.getNickname());
        map.put("content", commentDTO.getContent());
        map.put("ip", commentDTO.getIp());
        map.put("email", commentDTO.getEmail());
        map.put("status", Boolean.TRUE.equals(commentDTO.getPublished()) ? "公开" : "待审核");
        map.put("url", websiteUrl + path);
        map.put("manageUrl", cmsUrl + CommonConstant.COMMENTS);
        String toAccount = mailProperties.getUsername();
        String subject = blogName + " 收到新评论";
        mailUtils.sendHtmlTemplateMail(map, toAccount, subject, "owner.html");
    }

    @Override
    public void sendHtmlTemplateMail(Map<String, Object> map, String toAccount, String subject, String template) {
        mailUtils.sendHtmlTemplateMail(map, toAccount, subject, "guest.html");
    }
}
