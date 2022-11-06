package cn.raxcl.util.comment;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import cn.raxcl.config.properties.BlogProperties;
import cn.raxcl.constant.PageConstants;
import cn.raxcl.constant.RedisKeyConstants;
import cn.raxcl.entity.User;
import cn.raxcl.model.dto.CommentDTO;
import cn.raxcl.model.vo.FriendInfoVO;
import cn.raxcl.service.*;
import cn.raxcl.util.HashUtils;
import cn.raxcl.util.IpAddressUtils;
import cn.raxcl.util.MailUtils;
import cn.raxcl.util.QqInfoUtils;
import cn.raxcl.util.StringUtils;
import cn.raxcl.util.comment.channel.ChannelFactory;
import cn.raxcl.util.comment.channel.CommentNotifyChannel;
import cn.raxcl.enums.CommentOpenStateEnum;
import cn.raxcl.enums.CommentPageEnum;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 评论工具类
 *
 * @author Raxcl
 * @date 2022-03-15 23:47:06
 */
@Component
@DependsOn("springContextUtils")
public class CommentUtils {
    private final BlogProperties blogProperties;
    private final MailUtils mailUtils;
    private final AboutService aboutService;
    private final FriendService friendService;
    private final UserService userService;
    private final RedisService redisService;

    private final BlogService blogService;

    private final CommentNotifyChannel notifyChannel = ChannelFactory.getChannel("mail");

    public CommentUtils(BlogProperties blogProperties, MailUtils mailUtils, AboutService aboutService,
                        FriendService friendService, UserService userService, RedisService redisService, BlogService blogService) {
        this.blogProperties = blogProperties;
        this.mailUtils = mailUtils;
        this.aboutService = aboutService;
        this.friendService = friendService;
        this.userService = userService;
        this.redisService = redisService;
        this.blogService = blogService;
    }


    /**
     * 判断是否发送提醒
     * 6种情况：
     * 1.我以父评论提交：不用提醒
     * 2.我回复我自己：不用提醒
     * 3.我回复访客的评论：只提醒该访客
     * 4.访客以父评论提交：只提醒我自己
     * 5.访客回复我的评论：只提醒我自己
     * 6.访客回复访客的评论(即使是他自己先前的评论)：提醒我自己和他回复的评论
     *
     * @param commentDTO        当前收到的评论
     * @param isVisitorComment 是否访客评论
     * @param parentComment    父评论
     */
    public void judgeSendNotify(CommentDTO commentDTO, boolean isVisitorComment, cn.raxcl.entity.Comment parentComment) {
        if (parentComment != null && !parentComment.getAdminComment() && Boolean.TRUE.equals(parentComment.getNotice())) {
            //我回复访客的评论，且对方接收提醒，邮件提醒对方(3)
            //访客回复访客的评论(即使是他自己先前的评论)，且对方接收提醒，邮件提醒对方(6)
            sendMailToParentComment(parentComment, commentDTO);
        }
        if (isVisitorComment) {
            //访客以父评论提交，只提醒我自己(4)
            //访客回复我的评论，提醒我自己(5)
            //访客回复访客的评论，不管对方是否接收提醒，都要提醒我有新评论(6)
            notifyMyself(commentDTO);
        }
    }

    /**
     * 发送邮件提醒回复对象
     *
     * @param parentComment 父评论
     * @param commentDTO    当前收到的评论
     */
    private void sendMailToParentComment(cn.raxcl.entity.Comment parentComment, CommentDTO commentDTO) {
        CommentPageEnum commentPageEnum = getCommentPageEnum(commentDTO);
        Map<String, Object> map = new HashMap<>(7);
        map.put("parentNickname", parentComment.getNickname());
        map.put("nickname", commentDTO.getNickname());
        map.put("title", commentPageEnum.getTitle());
        map.put("time", commentDTO.getCreateTime());
        map.put("parentContent", parentComment.getContent());
        map.put("content", commentDTO.getContent());
        map.put("url", blogProperties.getView() + commentPageEnum.getPath());
        String toAccount = parentComment.getEmail();
        String subject = "您在 " + blogProperties.getName() + " 的评论有了新回复";
        mailUtils.sendHtmlTemplateMail(map, toAccount, subject, "guest.html");
    }

    /**
     * 通过指定方式通知自己
     *
     * @param commentDTO 当前收到的评论
     */
    private void notifyMyself(CommentDTO commentDTO) {
        notifyChannel.notifyMyself(commentDTO, getCommentPageEnum(commentDTO));
    }

    /**
     * 获取评论对应的页面
     *
     * @param commentDTO 当前收到的评论
     * @return CommentPageEnum
     */
    public CommentPageEnum getCommentPageEnum(CommentDTO commentDTO) {
        CommentPageEnum commentPageEnum = CommentPageEnum.UNKNOWN;
        switch (commentDTO.getPage()) {
            case 0:
                //普通博客
                commentPageEnum = CommentPageEnum.BLOG;
                commentPageEnum.setTitle(blogService.getTitleByBlogId(commentDTO.getBlogId()));
                commentPageEnum.setPath("/blog/" + commentDTO.getBlogId());
                break;
            case 1:
                //关于我页面
                commentPageEnum = CommentPageEnum.ABOUT;
                break;
            case 2:
                //友链页面
                commentPageEnum = CommentPageEnum.FRIEND;
                break;
            default:
                break;
        }
        return commentPageEnum;
    }

    /**
     * 查询对应页面评论是否开启
     *
     * @param page   页面分类（0普通文章，1关于我，2友链）
     * @param blogId 如果page==0，需要博客id参数，校验文章是否公开状态
     * @return CommentOpenStateEnum
     */
    public CommentOpenStateEnum judgeCommentState(Integer page, Long blogId) {
        switch (page) {
            case PageConstants.BLOG:
                //普通博客
                Boolean commentEnabled = blogService.getCommentEnabledByBlogId(blogId);
                Boolean published = blogService.getPublishedByBlogId(blogId);
                if (commentEnabled == null || published == null) {
                    //未查询到此博客
                    return CommentOpenStateEnum.NOT_FOUND;
                } else if (!published) {
                    //博客未公开
                    return CommentOpenStateEnum.NOT_FOUND;
                } else if (!commentEnabled) {
                    //博客评论已关闭
                    return CommentOpenStateEnum.CLOSE;
                }
                //判断文章是否存在密码
                String password = blogService.getBlogPassword(blogId);
                if (!StringUtils.isEmpty(password)) {
                    return CommentOpenStateEnum.PASSWORD;
                }
                break;
            case PageConstants.ABOUT:
                //关于我页面
                if (!aboutService.getAboutCommentEnabled()) {
                    //页面评论已关闭
                    return CommentOpenStateEnum.CLOSE;
                }
                break;
            case PageConstants.FRIEND:
                //友链页面
                FriendInfoVO friendInfoVO = friendService.getFriendInfo(true, false);
                if (Boolean.FALSE.equals(friendInfoVO.getCommentEnabled())) {
                    //页面评论已关闭
                    return CommentOpenStateEnum.CLOSE;
                }
                break;
            default:
                break;
        }
        return CommentOpenStateEnum.OPEN;
    }

    /**
     * 对于昵称不是QQ号的评论，根据昵称Hash设置头像
     *
     * @param commentDTO 当前收到的评论
     */
    private void setCommentRandomAvatar(CommentDTO commentDTO) {
        //设置随机头像
        //根据评论昵称取Hash，保证每一个昵称对应一个头像
        long nicknameHash = HashUtils.getMurmurHash32(commentDTO.getNickname());
        //计算对应的头像
        long num = nicknameHash % 6 + 1;
        String avatar = "/img/comment-avatar/" + num + ".jpg";
        commentDTO.setAvatar(avatar);
    }

    /**
     * 通用博主评论属性
     *
     * @param commentDTO 评论DTO
     * @param admin   博主信息
     */
    private void setGeneralAdminComment(CommentDTO commentDTO, User admin) {
        commentDTO.setAdminComment(true);
        commentDTO.setCreateTime(new Date());
        commentDTO.setPublished(true);
        commentDTO.setAvatar(admin.getAvatar());
        commentDTO.setWebsite("/");
        commentDTO.setNickname(admin.getNickname());
        commentDTO.setEmail(admin.getEmail());
        commentDTO.setNotice(false);
    }

    /**
     * 为[Telegram快捷回复]方式设置评论属性
     *
     * @param commentDTO 评论DTO
     */
    public void setAdminCommentByTelegramAction(CommentDTO commentDTO) {
        //查出博主信息，默认id为1的记录就是博主
        User admin = userService.findUserById(1L);

        setGeneralAdminComment(commentDTO, admin);
        commentDTO.setIp("via Telegram");
    }

    /**
     * 设置博主评论属性
     *
     * @param commentDTO 当前收到的评论
     * @param request 用于获取ip
     * @param admin   博主信息
     */
    public void setAdminComment(CommentDTO commentDTO, HttpServletRequest request, User admin) {
        setGeneralAdminComment(commentDTO, admin);
        commentDTO.setIp(IpAddressUtils.getIpAddress(request));
    }

    /**
     * 设置访客评论属性
     *
     * @param commentDTO 当前收到的评论
     * @param request 用于获取ip
     */
    public void setVisitorComment(CommentDTO commentDTO, HttpServletRequest request) {
        String commentNickname = commentDTO.getNickname();
        try {
            if (QqInfoUtils.isQqNumber(commentNickname)) {
                commentDTO.setQq(commentNickname);
                commentDTO.setNickname(QqInfoUtils.getQqNickname(commentNickname));
                String githubAvatarUrl = (String) redisService.getValueByHashKey(RedisKeyConstants.QQ_AVATAR_GITHUB_URL, commentNickname);
                if (StringUtils.isEmpty(githubAvatarUrl)){
                    String qqAvatarUrl = QqInfoUtils.getQqAvatarUrl(commentNickname);
                    commentDTO.setAvatar(qqAvatarUrl);
                    redisService.saveKvToHash(RedisKeyConstants.QQ_AVATAR_GITHUB_URL, commentNickname,qqAvatarUrl);
                }else {
                    commentDTO.setAvatar(githubAvatarUrl);
                }

            } else {
                commentDTO.setNickname(commentDTO.getNickname().trim());
                setCommentRandomAvatar(commentDTO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            commentDTO.setNickname(commentDTO.getNickname().trim());
            setCommentRandomAvatar(commentDTO);
        }

        //set website
        String website = commentDTO.getWebsite().trim();
        if (!"".equals(website) && !website.startsWith("http://") && !website.startsWith("https://")) {
            website = "http://" + website;
        }
        commentDTO.setAdminComment(false);
        commentDTO.setCreateTime(new Date());
        //新评论是否默认公开
        Boolean commentDefaultOpen = true;
        commentDTO.setPublished(commentDefaultOpen);
        commentDTO.setWebsite(website);
        commentDTO.setEmail(commentDTO.getEmail().trim());
        commentDTO.setIp(IpAddressUtils.getIpAddress(request));
    }
}
