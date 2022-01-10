package cn.raxcl.controller.view;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cn.raxcl.annotation.VisitLogger;
import cn.raxcl.model.vo.FriendVO;
import cn.raxcl.model.vo.FriendInfoVO;
import cn.raxcl.util.common.Result;
import cn.raxcl.service.FriendService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 友链
 * @author Raxcl
 * @date 2022-01-07 10:17:43
 */
@RestController
public class FriendController {
	private final FriendService friendService;

	public FriendController(FriendService friendService) {
		this.friendService = friendService;
	}

	/**
	 * 获取友链页面
	 * @return Result
	 */
	@VisitLogger(behavior = "访问页面", content = "友链")
	@GetMapping("/friends")
	public Result friends() {
		List<FriendVO> friendVOList = friendService.getFriendVOList();
		FriendInfoVO friendInfoVO = friendService.getFriendInfo(true, true);
		Map<String, Object> map = new HashMap<>(16);
		map.put("friendList", friendVOList);
		map.put("friendInfo", friendInfoVO);
		return Result.success("获取成功", map);
	}

	/**
	 * 按昵称增加友链浏览次数
	 *
	 * @param nickname 友链昵称
	 * @return Result
	 */
	@VisitLogger(behavior = "点击友链")
	@PostMapping("/friend")
	public Result addViews(@RequestParam String nickname) {
		friendService.updateViewsByNickname(nickname);
		return Result.success("请求成功");
	}
}
