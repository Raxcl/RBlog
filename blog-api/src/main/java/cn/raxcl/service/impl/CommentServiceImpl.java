package cn.raxcl.service.impl;

import cn.raxcl.constant.CodeConstants;
import cn.raxcl.constant.CommonConstants;
import cn.raxcl.constant.JwtConstants;
import cn.raxcl.entity.User;
import cn.raxcl.enums.CommentOpenStateEnum;
import cn.raxcl.exception.NotFoundException;
import cn.raxcl.model.dto.CommentDTO;
import cn.raxcl.model.temp.PostCommentDTO;
import cn.raxcl.model.vo.PageResultVO;
import cn.raxcl.service.*;
import cn.raxcl.util.*;
import cn.raxcl.util.comment.CommentUtils;
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
    //待修改
    @Value("${blog.name}")
    public String blogName;
    @Value("${blog.cms}")
    public String cmsUrl;
    @Value("${blog.view}")
    public String websiteUrl;

    private final CommentMapper commentMapper;
    private final UserServiceImpl userService;
    private final PostCommentDTO postCommentDTO;
    private final MailService mailService;
    private final CommentUtils commentUtils;

    public CommentServiceImpl(CommentMapper commentMapper,  UserServiceImpl userService, PostCommentDTO postCommentDTO,
                              MailService mailService, CommentUtils commentUtils) {
        this.commentMapper = commentMapper;
        this.userService = userService;
        this.postCommentDTO = postCommentDTO;
        this.mailService = mailService;
        this.commentUtils = commentUtils;
    }

    @Override
    public Map<String, Object> comments(Integer page, Long blogId, Integer pageNum, Integer pageSize, String jwt) {
        //评论功能校验
        checkQueryComment(jwt, blogId, page);
        //查询该页面所有评论的数量
        Integer allComment = countByPageAndIsPublished(page, blogId, null);
        //查询该页面公开评论的数量
        Integer openComment = countByPageAndIsPublished(page, blogId, true);
        PageMethod.startPage(pageNum, pageSize);
        PageInfo<PageCommentVO> pageInfo = new PageInfo<>(getPageCommentList(page, blogId, (long) -1));
        PageResultVO<PageCommentVO> pageResultVO = new PageResultVO<>(pageInfo.getPages(), pageInfo.getList());
        Map<String, Object> map = new HashMap<>(16);
        map.put("allComment", allComment);
        map.put("closeComment", allComment - openComment);
        map.put("comments", pageResultVO);
        return map;
    }

    /**
     * 查询评论功能的校验
     * @param jwt jwt
     * @param blogId blogId
     * @param page page
     */
    private void checkQueryComment(String jwt, Long blogId, Integer page) {
        //查询对应页面评论是否开启
        CommentOpenStateEnum openState = commentUtils.judgeCommentState(page, blogId);
        switch (openState){
            case NOT_FOUND:
                throw new NotFoundException("该博客不存在");
            case CLOSE:
                throw new NotFoundException("评论已关闭");
            case PASSWORD:
                //文章受密码保护，需要验证Token
                if (!JwtUtils.judgeTokenIsExist(jwt)) {
                    throw new NotFoundException("此文章受密码保护，请验证密码！(jwt为空)");
                }
                String subject;
                try {
                    subject = JwtUtils.getTokenBody(jwt, CodeConstants.SECRET_KEY).getSubject();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new NotFoundException("Token已失效，请重新验证密码！");
                }
                //博主身份Token
                if (subject.startsWith(JwtConstants.ADMIN_PREFIX)) {
                    String username = subject.replace(JwtConstants.ADMIN_PREFIX, "");
                    User admin = (User) userService.loadUserByUsername(username);
                    if (admin == null) {
                        throw new NotFoundException("博主身份Token已失效，请重新登录！");
                    }
                //普通访客经文章密码验证后携带Token
                }else {
                    Long tokenBlogId = Long.parseLong(subject);
                    //博客id不匹配，验证不通过，可能博客id改变或客户端传递了其它密码保护文章的Token
                    if (!tokenBlogId.equals(blogId)) {
                        throw new NotFoundException("Token不匹配，请重新验证密码！");
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 评论功能校验
     *
     * @param jwt jwt
     * @param commentDTO commentDTO
     */
    private void checkComment(CommentDTO commentDTO, HttpServletRequest request, String jwt) {
        //查询对应页面评论是否开启
        CommentOpenStateEnum openState = commentUtils.judgeCommentState(commentDTO.getPage(), commentDTO.getBlogId());
        switch (openState){
            case NOT_FOUND:
                throw new NotFoundException("该博客不存在");
            case CLOSE:
                throw new NotFoundException("评论已关闭");
            case PASSWORD:
                //文章受密码保护,验证Token合法性
                checkToken(jwt, commentDTO, request);
                break;
            case OPEN:
                //评论正常开放
                //有Token则为博主评论，或文章原先为密码保护，后取消保护，但客户端仍存在Token
                    if (JwtUtils.judgeTokenIsExist(jwt)) {
                        String subject;
                        try {
                            subject = JwtUtils.getTokenBody(jwt, CodeConstants.SECRET_KEY).getSubject();
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new NotFoundException("Token已失效，请重新验证密码！");
                        }
                        //博主评论，根据博主信息设置评论属性
                        if (subject.startsWith(JwtConstants.ADMIN_PREFIX)) {
                            //Token验证通过，获取Token中用户名
                            String username = subject.replace(JwtConstants.ADMIN_PREFIX, "");
                            User admin = (User) userService.loadUserByUsername(username);
                            if (admin == null) {
                                throw new NotFoundException("博主身份Token已失效，请重新登录！");
                            }
                            //博主身份赋值
                            commentUtils.setAdminComment(commentDTO, request, admin);
                            postCommentDTO.setIsVisitorComment(false);
                        //文章原先为密码保护，后取消保护，但客户端仍存在Token，则忽略Token
                        }else {
                            //访客评论
                            //对访客的评论昵称、邮箱合法性校验
                            if (StringUtils.isEmpty(commentDTO.getNickname(), commentDTO.getEmail()) || commentDTO.getNickname().length() > 15) {
                                throw new NotFoundException("参数有误");
                            }
                            commentUtils.setVisitorComment(commentDTO, request);
                            postCommentDTO.setIsVisitorComment(true);
                        }
                    } else {
                        //访客评论
                        //对访客的评论昵称、邮箱合法性校验
                        if (StringUtils.isEmpty(commentDTO.getNickname(), commentDTO.getEmail()) || commentDTO.getNickname().length() > 15) {
                            throw new NotFoundException("参数有误");
                        }
                        commentUtils.setVisitorComment(commentDTO, request);
                        postCommentDTO.setIsVisitorComment(true);
                    }
                    break;
            default:
                break;
        }
    }

    /**
     * 文章受密码保护，需要验证Token
     * @param jwt jwt
     * @param commentDTO commentDTO
     * @param request request
     */
    private void checkToken(String jwt, CommentDTO commentDTO, HttpServletRequest request) {
        if (!JwtUtils.judgeTokenIsExist(jwt)) {
            throw new NotFoundException("此文章受密码保护，请验证密码！(jwt为空)");
        }
        String subject;
        try {
            subject = JwtUtils.getTokenBody(jwt, CodeConstants.SECRET_KEY).getSubject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotFoundException("Token已失效，请重新验证密码！");
        }
        //校验是否为博主身份
        //博主评论，不受密码保护限制，根据博主信息设置评论属性
        if (subject.startsWith(JwtConstants.ADMIN_PREFIX)) {
            //Token验证通过，获取Token中用户名
            String username = subject.replace(JwtConstants.ADMIN_PREFIX, "");
            User admin = (User) userService.loadUserByUsername(username);
            if (admin == null) {
                throw new NotFoundException("博主身份Token已失效，请重新登录！");
            }
            //博主身份赋值
            commentUtils.setAdminComment(commentDTO, request, admin);
            postCommentDTO.setIsVisitorComment(false);
        //普通访客经文章密码验证后携带Token
        //对访客的评论昵称、邮箱合法性校验
        }else {
            if (StringUtils.isEmpty(commentDTO.getNickname(), commentDTO.getEmail()) || commentDTO.getNickname().length() > 15) {
                throw new NotFoundException("昵称、邮箱等参数有误");
            }
            //对于受密码保护的文章，则Token是必须的
            Long tokenBlogId = Long.parseLong(subject);
            //博客id不匹配，验证不通过，可能博客id改变或客户端传递了其它密码保护文章的Token
            if (!tokenBlogId.equals(commentDTO.getBlogId())) {
                throw new NotFoundException("Token不匹配，请重新验证密码！");
            }
            commentUtils.setVisitorComment(commentDTO, request);
            postCommentDTO.setIsVisitorComment(true);
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
        checkComment(commentDTO, request, jwt);

        self().saveComment(commentDTO);
        judgeSendMail(commentDTO, postCommentDTO.getIsVisitorComment(), parentComment);
    }

    //todo 提取方法逻辑不合理，待整改
    private void checkCommentToken(String jwt) {
        String subject;
        try {
            subject = JwtUtils.getTokenBody(jwt, CodeConstants.SECRET_KEY).getSubject();
            postCommentDTO.setSubject(subject);
        } catch (Exception e) {
            throw new NotFoundException("Token已失效，请重新验证密码！",e);
        }
        if (subject.startsWith(JwtConstants.ADMIN_PREFIX)) {
            String username = subject.replace(JwtConstants.ADMIN_PREFIX, "");
            User admin = (User) userService.loadUserByUsername(username);
            if (admin == null) {
                throw new NotFoundException(CommonConstants.SUBJECT_MSG);
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveComment(CommentDTO commentDTO) {
        if (commentMapper.saveComment(commentDTO) != 1) {
            throw new PersistenceException("评论失败");
        }
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
            //对于两列评论来说，按时间顺序排列应该比树形更合理些
            //排序一下
            Comparator<PageCommentVO> comparator = Comparator.comparing(PageCommentVO::getCreateTime);
            tmpComments.sort(comparator);
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
            path = CommonConstants.BLOG + commentDTO.getBlogId();
        } else if (commentDTO.getPage() == 1) {
            //关于我页面
            title = "关于我";
            path = CommonConstants.ABOUT;
        } else if (commentDTO.getPage() == 2) {
            //友链页面
            title = "友人帐";
            path = CommonConstants.FRIENDS;
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



}