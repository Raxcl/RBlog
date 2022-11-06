package cn.raxcl.util.comment.channel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import cn.raxcl.config.properties.BlogProperties;
import cn.raxcl.config.properties.TelegramProperties;
import cn.raxcl.enums.CommentPageEnum;
import cn.raxcl.model.dto.CommentDTO;
import cn.raxcl.util.StringUtils;
import cn.raxcl.util.comment.CommentUtils;
import cn.raxcl.util.telegram.TelegramUtils;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TimeZone;

/**
 * Telegram提醒方式
 *
 * @author Raxcl
 * @date 2022-03-15 23:47:06
 */
@Slf4j
@Lazy
@Component
public class TelegramChannel implements CommentNotifyChannel {
    private final TelegramUtils telegramUtils;
    private final BlogProperties blogProperties;
    private final TelegramProperties telegramProperties;
    private final SimpleDateFormat simpleDateFormat;
    private final CommentUtils commentUtils;

    public TelegramChannel(TelegramUtils telegramUtils, BlogProperties blogProperties, TelegramProperties telegramProperties, CommentUtils commentUtils) {
        this.telegramUtils = telegramUtils;
        this.blogProperties = blogProperties;
        this.telegramProperties = telegramProperties;
        this.commentUtils = commentUtils;

        this.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        this.simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

        log.info("TelegramChannel instantiating");
        telegramUtils.setWebhook();
    }

    /**
     * 发送Telegram消息提醒我自己
     *
     * @param commentDTO 当前收到的评论
     */
    @Override
    public void notifyMyself(CommentDTO commentDTO,CommentPageEnum commentPageEnum) {
        String url = telegramProperties.getApi() + telegramProperties.getToken() + TelegramUtils.SEND_MESSAGE;
        String content = getContent(commentDTO);
        Map<String, Object> messageBody = telegramUtils.getMessageBody(content);
        telegramUtils.sendByAutoCheckReverseProxy(url, messageBody);
    }

    private String getContent(CommentDTO commentDTO) {
        CommentPageEnum commentPageEnum = commentUtils.getCommentPageEnum(commentDTO);
        return String.format(
                "<b>您的文章<a href=\"%s\">《%s》</a>有了新的评论~</b>\n" +
                        "\n" +
                        "<b>%s</b> 给您的评论：\n" +
                        "\n" +
                        "<pre>%s</pre>\n" +
                        "\n" +
                        "<b>其他信息：</b>\n" +
                        "评论ID：<code>%d</code>\n" +
                        "IP：%s\n" +
                        "%s" +
                        "时间：<u>%s</u>\n" +
                        "邮箱：<code>%s</code>\n" +
                        "%s" +
                        "状态：%s [<a href=\"%s\">管理评论</a>]\n",
                blogProperties.getView() + commentPageEnum.getPath(),
                commentPageEnum.getTitle(),
                commentDTO.getNickname(),
                commentDTO.getContent(),
                commentDTO.getId(),
                commentDTO.getIp(),
                StringUtils.isEmpty(commentDTO.getQq()) ? "" : "QQ：" + commentDTO.getQq() + "\n",
                simpleDateFormat.format(commentDTO.getCreateTime()),
                commentDTO.getEmail(),
                StringUtils.isEmpty(commentDTO.getWebsite()) ? "" : "网站：" + commentDTO.getWebsite() + "\n",
                Boolean.TRUE.equals(commentDTO.getPublished()) ? "公开" : "待审核",
                blogProperties.getCms() + "/blog/comment/list"
        );
    }
}
