package cn.raxcl.controller.view;

import cn.raxcl.constant.CodeConstants;
import cn.raxcl.constant.JwtConstants;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cn.raxcl.annotation.AccessLimit;
import cn.raxcl.annotation.VisitLogger;
import cn.raxcl.entity.Moment;
import cn.raxcl.entity.User;
import cn.raxcl.model.vo.PageResultVO;
import cn.raxcl.util.common.Result;
import cn.raxcl.service.MomentService;
import cn.raxcl.service.impl.UserServiceImpl;
import cn.raxcl.util.JwtUtils;

/**
 * 动态
 * @author Raxcl
 * @date 2022-01-07 15:43:03
 */
@RestController
public class MomentController {

	private final MomentService momentService;
	private final UserServiceImpl userService;

	public MomentController(MomentService momentService, UserServiceImpl userService) {
		this.momentService = momentService;
		this.userService = userService;
	}

	/**
	 * 分页查询动态List
	 *
	 * @param pageNum 页码
	 * @param jwt     博主访问Token
	 * @return Result
	 */
	@VisitLogger(behavior = "访问页面", content = "动态")
	@GetMapping("/moments")
	public Result moments(@RequestParam(defaultValue = "1") Integer pageNum,
	                      @RequestHeader(value = "Authorization", defaultValue = "") String jwt) {
		boolean adminIdentity = false;
		if (JwtUtils.judgeTokenIsExist(jwt)) {
			try {
				String subject = JwtUtils.getTokenBody(jwt, CodeConstants.SECRET_KEY).getSubject();
				//博主身份Token
				if (subject.startsWith(JwtConstants.ADMIN_PREFIX)) {
					String username = subject.replace("admin:", "");
					User admin = (User) userService.loadUserByUsername(username);
					if (admin != null) {
						adminIdentity = true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		PageInfo<Moment> pageInfo = new PageInfo<>(momentService.getMomentVOList(pageNum, adminIdentity));
		PageResultVO<Moment> pageResultVO = new PageResultVO<>(pageInfo.getPages(), pageInfo.getList());
		return Result.success("获取成功", pageResultVO);
	}

	/**
	 * 给动态点赞
	 * 简单限制一下点赞
	 *
	 * @param id 动态id
	 * @return Result
	 */
	@AccessLimit(seconds = 86400, maxCount = 1, msg = "不可以重复点赞哦")
	@VisitLogger(behavior = "点赞动态")
	@PostMapping("/moment/like/{id}")
	public Result like(@PathVariable Long id) {
		momentService.addLikeByMomentId(id);
		return Result.success("点赞成功");
	}
}
