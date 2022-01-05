package cn.raxcl.controller.view;

import cn.raxcl.annotation.AccessLimit;
import cn.raxcl.entity.Comment;
import cn.raxcl.entity.User;
import cn.raxcl.model.dto.CommentDTO;
import cn.raxcl.model.vo.Result;
import cn.raxcl.service.BlogService;
import cn.raxcl.service.CommentService;
import cn.raxcl.service.impl.UserServiceImpl;
import cn.raxcl.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 评论
 * @author  Raxcl
 * @date 2021-12-31 16:02:54
 */
@RestController
public class CommentController {
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
	@Value("${token.secretKey}")
	private String secretKey;

	private final CommentService commentService;
	private final BlogService blogService;
	private final UserServiceImpl userService;
	private final MailProperties mailProperties;
	private final MailUtils mailUtils;

	public CommentController(CommentService commentService, BlogService blogService,UserServiceImpl userService,
							 MailProperties mailProperties,
							 MailUtils mailUtils) {
		this.commentService = commentService;
		this.blogService = blogService;
		this.userService = userService;
		this.mailProperties = mailProperties;
		this.mailUtils = mailUtils;
	}

	/**
	 * 根据页面分页查询评论列表
	 *
	 * @param page     页面分类（0普通文章，1关于我...）
	 * @param blogId   如果page==0，需要博客id参数
	 * @param pageNum  页码
	 * @param pageSize 每页个数
	 * @param jwt      若文章受密码保护，需要获取访问Token
	 * @return
	 */
	@GetMapping("/comments")
	public Result comments(@RequestParam Integer page,
	                       @RequestParam(defaultValue = "") Long blogId,
	                       @RequestParam(defaultValue = "1") Integer pageNum,
	                       @RequestParam(defaultValue = "10") Integer pageSize,
	                       @RequestHeader(value = "Authorization", defaultValue = "") String jwt) {
		return commentService.comments(page, blogId, pageNum, pageSize, jwt);
	}

	/**
	 * 提交评论 又长又臭 能用就不改了:) https://cdn.jsdelivr.net/gh/Raxcl/blog-resource/img/1stLaw4Coding.jpg
	 * 单个ip，30秒内允许提交1次评论
	 *
	 * @param commentDTO 评论DTO
	 * @param request 获取ip
	 * @param jwt     博主身份Token
	 * @return
	 */
	@AccessLimit(seconds = 30, maxCount = 1, msg = "30秒内只能提交一次评论")
	@PostMapping("/comment")
	public Result postComment(@RequestBody CommentDTO commentDTO,
	                          HttpServletRequest request,
	                          @RequestHeader(value = "Authorization", defaultValue = "") String jwt) {
		//评论内容合法性校验
		if (StringUtils.isEmpty(commentDTO.getContent()) || commentDTO.getContent().length() > 250 ||
				commentDTO.getPage() == null || commentDTO.getParentCommentId() == null) {
			return Result.error("参数有误");
		}
		//是否访客的评论
		boolean isVisitorComment = false;
		//父评论
		cn.raxcl.entity.Comment parentComment = null;
		//对于有指定父评论的评论，应该以父评论为准，只判断页面可能会被绕过“评论开启状态检测”
		if (commentDTO.getParentCommentId() != -1) {
			//当前评论为子评论
			parentComment = commentService.getCommentById(commentDTO.getParentCommentId());
			Integer page = parentComment.getPage();
			Long blogId = page == 0 ? parentComment.getBlog().getId() : null;
			commentDTO.setPage(page);
			commentDTO.setBlogId(blogId);
		} else {
			//当前评论为顶级评论
			if (commentDTO.getPage() != 0) {
				commentDTO.setBlogId(null);
			}
		}
		//判断是否可评论
		int judgeResult = commentService.judgeCommentEnabled(commentDTO.getPage(), commentDTO.getBlogId());
		if (judgeResult == 2) {
			return Result.exception(404, "该博客不存在");
		} else if (judgeResult == 1) {
			return Result.exception(403, "评论已关闭");
		} else if (judgeResult == 3) {//文章受密码保护
			//验证Token合法性
			if (JwtUtils.judgeTokenIsExist(jwt)) {
				String subject;
				try {
					subject = JwtUtils.getTokenBody(jwt, secretKey).getSubject();
				} catch (Exception e) {
					e.printStackTrace();
					return Result.exception(403, "Token已失效，请重新验证密码！");
				}
				//博主评论，不受密码保护限制，根据博主信息设置评论属性
				if (subject.startsWith("admin:")) {
					//Token验证通过，获取Token中用户名
					String username = subject.replace("admin:", "");
					User admin = (User) userService.loadUserByUsername(username);
					if (admin == null) {
						return Result.exception(403, "博主身份Token已失效，请重新登录！");
					}
					setAdminComment(commentDTO, request, admin);
					isVisitorComment = false;
				} else {//普通访客经文章密码验证后携带Token
					//对访客的评论昵称、邮箱合法性校验
					if (StringUtils.isEmpty(commentDTO.getNickname(), commentDTO.getEmail()) || commentDTO.getNickname().length() > 15) {
						return Result.error("参数有误");
					}
					//对于受密码保护的文章，则Token是必须的
					Long tokenBlogId = Long.parseLong(subject);
					//博客id不匹配，验证不通过，可能博客id改变或客户端传递了其它密码保护文章的Token
					if (!tokenBlogId.equals(commentDTO.getBlogId())) {
						return Result.exception(403, "Token不匹配，请重新验证密码！");
					}
					setVisitorComment(commentDTO, request);
					isVisitorComment = true;
				}
			} else {//不存在Token则无评论权限
				return Result.exception(403, "此文章受密码保护，请验证密码！");
			}
		} else if (judgeResult == 0) {//普通文章
			//有Token则为博主评论，或文章原先为密码保护，后取消保护，但客户端仍存在Token
			if (JwtUtils.judgeTokenIsExist(jwt)) {
				String subject;
				try {
					subject = JwtUtils.getTokenBody(jwt, secretKey).getSubject();
				} catch (Exception e) {
					e.printStackTrace();
					return Result.exception(403, "Token已失效，请重新验证密码");
				}
				//博主评论，根据博主信息设置评论属性
				if (subject.startsWith("admin:")) {
					//Token验证通过，获取Token中用户名
					String username = subject.replace("admin:", "");
					User admin = (User) userService.loadUserByUsername(username);
					if (admin == null) {
						return Result.exception(403, "博主身份Token已失效，请重新登录！");
					}
					setAdminComment(commentDTO, request, admin);
					isVisitorComment = false;
				} else {//文章原先为密码保护，后取消保护，但客户端仍存在Token，则忽略Token
					//对访客的评论昵称、邮箱合法性校验
					if (StringUtils.isEmpty(commentDTO.getNickname(), commentDTO.getEmail()) || commentDTO.getNickname().length() > 15) {
						return Result.error("参数有误");
					}
					setVisitorComment(commentDTO, request);
					isVisitorComment = true;
				}
			} else {//访客评论
				//对访客的评论昵称、邮箱合法性校验
				if (StringUtils.isEmpty(commentDTO.getNickname(), commentDTO.getEmail()) || commentDTO.getNickname().length() > 15) {
					return Result.error("参数有误");
				}
				setVisitorComment(commentDTO, request);
				isVisitorComment = true;
			}
		}
		commentService.saveComment(commentDTO);
		judgeSendMail(commentDTO, isVisitorComment, parentComment);
		return Result.success("评论成功");
	}

	/**
	 * 设置博主评论属性
	 *
	 * @param commentDTO 评论DTO
	 * @param request 获取ip
	 * @param admin   博主信息
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
	 * @param request 用于获取ip
	 */
	private void setVisitorComment(CommentDTO commentDTO, HttpServletRequest request) {
		String commentNickname = commentDTO.getNickname();
		try {
			if (QqInfoUtils.isQqNumber(commentNickname)) {
				commentDTO.setQq(commentNickname);
				commentDTO.setNickname(QqInfoUtils.getQqNickname(commentNickname));
				commentDTO.setAvatar(QqInfoUtils.getQqAvatarUrlByGithubUpload(commentNickname,githubToken, githubUsername, githubRepos, githubReposPath));
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
		commentDTO.setPublished(true);//默认不需要审核
		commentDTO.setWebsite(website);
		commentDTO.setEmail(commentDTO.getEmail().trim());
		commentDTO.setIp(IpAddressUtils.getIpAddress(request));
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
		if (parentComment != null && !parentComment.getAdminComment() && parentComment.getNotice()) {
			//我回复访客的评论，且对方接收提醒，邮件提醒对方(3)
			//访客回复访客的评论(即使是他自己先前的评论)，且对方接收提醒，邮件提醒对方(6)
			sendMailToParentComment(parentComment, commentDTO);
		}
		if (isVisitorComment) {
			//访客以父评论提交，只邮件提醒我自己(4)
			//访客回复我的评论，邮件提醒我自己(5)
			//访客回复访客的评论，不管对方是否接收提醒，都要提醒我有新评论(6)
			sendMailToMe(commentDTO);
		}
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
			path = "/blog/" + commentDTO.getBlogId();
		} else if (commentDTO.getPage() == 1) {
			//关于我页面
			title = "关于我";
			path = "/about";
		} else if (commentDTO.getPage() == 2) {
			//友链页面
			title = "友人帐";
			path = "/friends";
		}
		Map<String, Object> map = new HashMap<>();
		map.put("parentNickname", parentComment.getNickname());
		map.put("nickname", commentDTO.getNickname());
		map.put("title", title);
		map.put("time", commentDTO.getCreateTime());
		map.put("parentContent", parentComment.getContent());
		map.put("content", commentDTO.getContent());
		map.put("url", websiteUrl + path);
		String toAccount = parentComment.getEmail();
		String subject = "您在 " + blogName + " 的评论有了新回复";
		mailUtils.sendHtmlTemplateMail(map, toAccount, subject, "guest.html");
	}

	/**
	 * 发送邮件提醒我自己
	 *
	 * @param commentDTO 当前评论
	 */
	private void sendMailToMe(CommentDTO commentDTO) {
		String path = "";
		String title = "";
		if (commentDTO.getPage() == 0) {
			//普通博客
			title = blogService.getTitleByBlogId(commentDTO.getBlogId());
			path = "/blog/" + commentDTO.getBlogId();
		} else if (commentDTO.getPage() == 1) {
			//关于我页面
			title = "关于我";
			path = "/about";
		} else if (commentDTO.getPage() == 2) {
			//友链页面
			title = "友人帐";
			path = "/friends";
		}
		Map<String, Object> map = new HashMap<>();
		map.put("title", title);
		map.put("time", commentDTO.getCreateTime());
		map.put("nickname", commentDTO.getNickname());
		map.put("content", commentDTO.getContent());
		map.put("ip", commentDTO.getIp());
		map.put("email", commentDTO.getEmail());
		map.put("status", commentDTO.getPublished() ? "公开" : "待审核");
		map.put("url", websiteUrl + path);
		map.put("manageUrl", cmsUrl + "/comments");
		String toAccount = mailProperties.getUsername();
		String subject = blogName + " 收到新评论";
		mailUtils.sendHtmlTemplateMail(map, toAccount, subject, "owner.html");
	}
}