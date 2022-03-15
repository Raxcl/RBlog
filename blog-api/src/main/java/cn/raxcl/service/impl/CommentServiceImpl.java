package cn.raxcl.service.impl;

import cn.raxcl.constant.CodeConstant;
import cn.raxcl.constant.CommonConstant;
import cn.raxcl.entity.User;
import cn.raxcl.exception.NotFoundException;
import cn.raxcl.model.dto.CommentDTO;
import cn.raxcl.model.temp.PostCommentDTO;
import cn.raxcl.model.vo.FriendInfoVO;
import cn.raxcl.model.vo.PageResultVO;
import cn.raxcl.service.*;
import cn.raxcl.util.*;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import cn.raxcl.aspect.AopProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.raxcl.entity.Comment;
import cn.raxcl.exception.PersistenceException;
import cn.raxcl.mapper.CommentMapper;
import cn.raxcl.model.vo.PageCommentVO;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author Raxcl
 * 博客评论业务层实现
 * @date 2022-01-07 19:58:05
 */
@Service
public class CommentServiceImpl implements CommentService, AopProxy<CommentServiceImpl> {
    /**
     * GitHub token
     */
    @Value("${upload.github.token}")
    private String githubToken;
    /**
     * GitHub用户名
     */
    @Value("${upload.github.username}")
    private String githubUsername;
    /**
     * GitHub仓库名
     */
    @Value("${upload.github.repos}")
    private String githubRepos;
    /**
     * GitHub仓库路径
     */
    @Value("${upload.github.repos-path}")
    private String githubReposPath;
    @Value("${custom.blog.name}")
    public String blogName;
    @Value("${custom.url.cms}")
    public String cmsUrl;
    @Value("${custom.url.website}")
    public String websiteUrl;

    private final CommentMapper commentMapper;
    private final BlogService blogService;
    private final AboutService aboutService;
    private final FriendService friendService;
    private final UserServiceImpl userService;
    private final PostCommentDTO postCommentDTO;
    private final MailService mailService;

    public CommentServiceImpl(CommentMapper commentMapper, BlogService blogService, AboutService aboutService,
                              FriendService friendService, UserServiceImpl userService, PostCommentDTO postCommentDTO,
                              MailService mailService) {
        this.commentMapper = commentMapper;
        this.blogService = blogService;
        this.aboutService = aboutService;
        this.friendService = friendService;
        this.userService = userService;
        this.postCommentDTO = postCommentDTO;
        this.mailService = mailService;
    }

    @Override
    public Map<String, Object> comments(Integer page, Long blogId, Integer pageNum, Integer pageSize, String jwt) {

        //评论功能校验
        checkComment(jwt, blogId, page);
        //查询该页面所有评论的数量
        Integer allComment = countByPageAndIsPublished(page, blogId, null);
        //查询该页面公开评论的数量
        Integer openComment = countByPageAndIsPublished(page, blogId, true);
        PageMethod.startPage(pageNum, pageSize);
        Long parentCommentId = -1L;
        PageInfo<PageCommentVO> pageInfo = new PageInfo<>(getPageCommentList(page, blogId, parentCommentId));
        PageResultVO<PageCommentVO> pageResultVO = new PageResultVO<>(pageInfo.getPages(), pageInfo.getList());
        Map<String, Object> map = new HashMap<>(16);
        map.put("allComment", allComment);
        map.put("closeComment", allComment - openComment);
        map.put("comments", pageResultVO);
        return map;
    }

    /**
     * 评论功能校验
     *
     * @param jwt jwt
     * @param blogId blogId
     */
    private void checkComment(String jwt, Long blogId, Integer page) {
        //查询对应页面评论是否开启
        int judgeResult = judgeCommentEnabled(page, blogId);
        postCommentDTO.setJudgeResult(judgeResult);
        if (judgeResult == 1) {
            throw new NotFoundException("评论已关闭");
        } else if (judgeResult == CommonConstant.TWO) {
            throw new NotFoundException("该博客不存在");
        } else if (judgeResult == CommonConstant.THREE) {
            //文章受密码保护,验证Token合法性
            checkToken(jwt, blogId);
        }
    }

    /**
     * 查询对应页面评论是否开启
     *
     * @param page   页面分类（0普通文章，1关于我，2友链）
     * @param blogId 如果page==0，需要博客id参数，校验文章是否公开状态
     * @return 0:公开可查询状态 1:评论关闭 2:该博客不存在 3:文章受密码保护
     */
    private int judgeCommentEnabled(Integer page, Long blogId) {
        //普通博客
        if (page == 0) {
            Boolean commentEnabled = blogService.getCommentEnabledByBlogId(blogId);
            Boolean published = blogService.getPublishedByBlogId(blogId);
            //未查询到此博客 or 博客未公开
            if (commentEnabled == null || published == null || Boolean.FALSE.equals(published)) {
                return 2;
            } else if (!commentEnabled) {
                //博客评论已关闭
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
            FriendInfoVO friendInfoVO = friendService.getFriendInfo(true, false);
            if (Boolean.FALSE.equals(friendInfoVO.getCommentEnabled())) {
                return 1;
            }
        }
        return 0;
    }

    /**
     * 文章受密码保护，需要验证Token
     *
     * @param jwt jwt
     * @param blogId blogId
     */
    private void checkToken(String jwt, Long blogId) {
        if (!JwtUtils.judgeTokenIsExist(jwt)) {
            throw new NotFoundException("此文章受密码保护，请验证密码！(jwt为空)");
        }
        //校验是否为博主身份
        checkCommentToken(jwt);
        //博主身份Token
        if (!postCommentDTO.getSubject().startsWith(CommonConstant.ADMIN)) {
            //经密码验证后的Token
            Long tokenBlogId = Long.parseLong(postCommentDTO.getSubject());
            //博客id不匹配，验证不通过，可能博客id改变或客户端传递了其它密码保护文章的Token
            if (!tokenBlogId.equals(blogId)) {
                throw new NotFoundException("Token不匹配，请重新验证密码！");
            }
        }
    }

    /**
     * 访客评论 博主评论
     * 父评论 子评论
     * 加密文章 普通文章
     * 评论内容校验  评论留下账号校验
     */
    @Override
    public void postComment(CommentDTO commentDTO, HttpServletRequest request, String jwt) {
        //评论内容合法性校验
        if (StringUtils.isEmpty(commentDTO.getContent()) || commentDTO.getContent().length() > 250 ||
                commentDTO.getPage() == null || commentDTO.getParentCommentId() == null) {
            throw new NotFoundException("评论内容不合法");
        }
        //是否访客的评论 访客，管理员  默认为管理员
        postCommentDTO.setIsVisitorComment(false);
        //判断是否为父评论
        Comment parentComment = parentCommentCheck(commentDTO);
        //评论功能校验 页面及密码保护校验
        checkComment(jwt, commentDTO.getBlogId(), commentDTO.getPage());
        //博主身份赋值
        if(postCommentDTO.getAdmin() != null){
            setAdminComment(commentDTO, request, postCommentDTO.getAdmin());
        }else {
            //对访客的评论昵称、邮箱合法性校验
            if (StringUtils.isEmpty(commentDTO.getNickname(), commentDTO.getEmail()) || commentDTO.getNickname().length() > 15) {
                throw new NotFoundException("昵称、邮箱等参数有误");
            }
            setVisitorComment(commentDTO, request);
            postCommentDTO.setIsVisitorComment(true);
        }
        //普通文章
        if (postCommentDTO.getJudgeResult() == 0) {
            //有Token则为博主评论，或文章原先为密码保护，后取消保护，但客户端仍存在Token
            if (JwtUtils.judgeTokenIsExist(jwt)) {
                //校验是否为博主身份
                checkCommentToken(jwt);
                //博主评论，根据博主信息设置评论属性
                if (postCommentDTO.getAdmin() != null){
                    setAdminComment(commentDTO, request, postCommentDTO.getAdmin());
                    postCommentDTO.setIsVisitorComment(false);
                }else {
                    //文章原先为密码保护，后取消保护，但客户端仍存在Token，则忽略Token
                    //对访客的评论昵称、邮箱合法性校验
                    commonParamCheck(commentDTO,request);
                }
            } else {
                //访客评论
                //对访客的评论昵称、邮箱合法性校验
                commonParamCheck(commentDTO,request);
            }
        }
        self().saveComment(commentDTO);
        judgeSendMail(commentDTO, postCommentDTO.getIsVisitorComment(), parentComment);
    }

    private void checkCommentToken(String jwt) {
        String subject;
        try {
            subject = JwtUtils.getTokenBody(jwt, CodeConstant.SECRET_KEY).getSubject();
            postCommentDTO.setSubject(subject);
        } catch (Exception e) {
            throw new NotFoundException("Token已失效，请重新验证密码！",e);
        }
        if (subject.startsWith(CommonConstant.ADMIN)) {
            String username = subject.replace(CommonConstant.ADMIN, "");
            User admin = (User) userService.loadUserByUsername(username);
            if (admin == null) {
                throw new NotFoundException(CommonConstant.SUBJECT_MSG);
            }
            postCommentDTO.setAdmin(admin);
        }
    }


    private Comment parentCommentCheck(CommentDTO commentDTO) {
        Comment parentComment = null;
        //对于有指定父评论的评论，应该以父评论为准，只判断页面可能会被绕过“评论开启状态检测”
        if (commentDTO.getParentCommentId() != -1) {
            //当前评论为子评论
            parentComment = getCommentById(commentDTO.getParentCommentId());
            Integer page = parentComment.getPage();
            Long blogId = page == 0 ? parentComment.getBlog().getId() : null;
            commentDTO.setPage(page);
            commentDTO.setBlogId(blogId);
        } else if (commentDTO.getPage() != 0){
            //当前评论为顶级评论(父评论)
            commentDTO.setBlogId(null);
        }
        return parentComment;
    }

    /**
     * 设置博主评论属性
     *
     * @param commentDTO 评论DTO
     * @param request    获取ip
     * @param admin      博主信息
     */
    private void setAdminComment(CommentDTO commentDTO, HttpServletRequest request, User admin) {
        commentDTO.setAdminComment(true);
        commentDTO.setCreateTime(new Date());
        commentDTO.setPublished(true);
        commentDTO.setAvatar(admin.getAvatar());
        commentDTO.setWebsite("/");
        commentDTO.setNickname(admin.getNickname());
        commentDTO.setEmail(admin.getEmail());
        commentDTO.setIp(IpAddressUtils.getIpAddress(request));
        commentDTO.setNotice(false);
    }

    /**
     * 设置访客评论属性
     *
     * @param commentDTO 评论DTO
     * @param request    用于获取ip
     */
    private void setVisitorComment(CommentDTO commentDTO, HttpServletRequest request) {
        String commentNickname = commentDTO.getNickname();
        try {
            if (QqInfoUtils.isQqNumber(commentNickname)) {
                commentDTO.setQq(commentNickname);
                commentDTO.setNickname(QqInfoUtils.getQqNickname(commentNickname));
                commentDTO.setAvatar(QqInfoUtils.getQqAvatarUrlByGithubUpload(commentNickname, githubToken, githubUsername, githubRepos, githubReposPath));
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
        //默认不需要审核
        commentDTO.setPublished(true);
        commentDTO.setWebsite(website);
        commentDTO.setEmail(commentDTO.getEmail().trim());
        commentDTO.setIp(IpAddressUtils.getIpAddress(request));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveComment(CommentDTO commentDTO) {
        if (commentMapper.saveComment(commentDTO) != 1) {
            throw new PersistenceException("评论失败");
        }
    }

    private void commonParamCheck(CommentDTO commentDTO, HttpServletRequest request) {
        if (StringUtils.isEmpty(commentDTO.getNickname(), commentDTO.getEmail()) || commentDTO.getNickname().length() > 15) {
            throw new NotFoundException("参数有误");
        }
        setVisitorComment(commentDTO, request);
        postCommentDTO.setIsVisitorComment(true);
    }

    /**
     * 判断是否发送邮件
     * 6种情况：
     * 1.我以父评论提交：不用邮件提醒
     * 2.我回复我自己：不用邮件提醒
     * 3.我回复访客的评论：只提醒该访客
     * 4.访客以父评论提交：只提醒我自己
     * 5.访客回复我的评论：只提醒我自己
     * 6.访客回复访客的评论(即使是他自己先前的评论)：提醒我自己和他回复的评论
     *
     * @param commentDTO          当前评论
     * @param isVisitorComment 是否访客评论
     * @param parentComment    父评论
     */
    private void judgeSendMail(CommentDTO commentDTO, boolean isVisitorComment, cn.raxcl.entity.Comment parentComment) {
        if (parentComment != null && !parentComment.getAdminComment() && Boolean.TRUE.equals(parentComment.getNotice())) {
            //我回复访客的评论，且对方接收提醒，邮件提醒对方(3)
            //访客回复访客的评论(即使是他自己先前的评论)，且对方接收提醒，邮件提醒对方(6)
            sendMailToParentComment(parentComment, commentDTO);
        }
        if (isVisitorComment) {
            //访客以父评论提交，只邮件提醒我自己(4)
            //访客回复我的评论，邮件提醒我自己(5)
            //访客回复访客的评论，不管对方是否接收提醒，都要提醒我有新评论(6)
            mailService.sendMailToMe(commentDTO);
        }
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

    private List<PageCommentVO> getPageCommentList(Integer page, Long blogId, Long parentCommentId) {
        List<PageCommentVO> comments = getPageCommentListByPageAndParentCommentId(page, blogId, parentCommentId);
        for (PageCommentVO c : comments) {
            List<PageCommentVO> tmpComments = new ArrayList<>();
            getReplyComments(tmpComments, c.getReplyComments());
            c.setReplyComments(tmpComments);
        }
        return comments;
    }

    private Comment getCommentById(Long id) {
        Comment comment = commentMapper.getCommentById(id);
        if (comment == null) {
            throw new PersistenceException("评论不存在");
        }
        return comment;
    }

    /**
     * 将所有子评论递归取出到一个List中
     *
     * @param comments comments
     */
    private void getReplyComments(List<PageCommentVO> tmpComments, List<PageCommentVO> comments) {
        for (PageCommentVO c : comments) {
            tmpComments.add(c);
            getReplyComments(tmpComments, c.getReplyComments());
        }
    }

    private List<PageCommentVO> getPageCommentListByPageAndParentCommentId(Integer page, Long blogId, Long parentCommentId) {
        List<PageCommentVO> comments = commentMapper.getPageCommentListByPageAndParentCommentId(page, blogId, parentCommentId);
        for (PageCommentVO c : comments) {
            List<PageCommentVO> replyComments = getPageCommentListByPageAndParentCommentId(page, blogId, c.getId());
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

    private int countByPageAndIsPublished(Integer page, Long blogId, Boolean isPublished) {
        return commentMapper.countByPageAndIsPublished(page, blogId, isPublished);
    }

    /**
     * 发送邮件提醒回复对象
     *
     * @param parentComment 父评论
     * @param commentDTO       当前评论
     */
    private void sendMailToParentComment(Comment parentComment, CommentDTO commentDTO) {
        String path = "";
        String title = "";
        if (commentDTO.getPage() == 0) {
            //普通博客
            title = parentComment.getBlog().getTitle();
            path = CommonConstant.BLOG + commentDTO.getBlogId();
        } else if (commentDTO.getPage() == 1) {
            //关于我页面
            title = "关于我";
            path = CommonConstant.ABOUT;
        } else if (commentDTO.getPage() == 2) {
            //友链页面
            title = "友人帐";
            path = CommonConstant.FRIENDS;
        }
        Map<String, Object> map = new HashMap<>(16);
        map.put("parentNickname", parentComment.getNickname());
        map.put("nickname", commentDTO.getNickname());
        map.put("title", title);
        map.put("time", commentDTO.getCreateTime());
        map.put("parentContent", parentComment.getContent());
        map.put("content", commentDTO.getContent());
        map.put("url", websiteUrl + path);
        String toAccount = parentComment.getEmail();
        String subject = "您在 " + blogName + " 的评论有了新回复";
        mailService.sendHtmlTemplateMail(map, toAccount, subject, "guest.html");
    }

    /**
     * 递归删除子评论
     *
     * @param comment 需要删除子评论的父评论
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
     * @return List<Comment>
     */
    private List<Comment> getAllReplyComments(Long parentCommentId) {
        List<Comment> comments = commentMapper.getListByParentCommentId(parentCommentId);
        for (Comment c : comments) {
            List<Comment> replyComments = getAllReplyComments(c.getId());
            c.setReplyComments(replyComments);
        }
        return comments;
    }

    /**
     * 对于昵称不是QQ号的评论，根据昵称Hash设置头像
     *
     * @param commentDTO 评论DTO
     */
    private void setCommentRandomAvatar(CommentDTO commentDTO) {
        //设置随机头像
        long nicknameHash = HashUtils.getMurmurHash32(commentDTO.getNickname());//根据评论昵称取Hash，保证每一个昵称对应一个头像
        long num = nicknameHash % 6 + 1;//计算对应的头像
        String avatar = "/img/comment-avatar/" + num + ".jpg";
        commentDTO.setAvatar(avatar);
    }

}