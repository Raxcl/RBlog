package cn.raxcl.controller.admin;

import cn.raxcl.model.dto.FriendDTO;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cn.raxcl.annotation.OperationLogger;
import cn.raxcl.entity.Friend;
import cn.raxcl.util.common.Result;
import cn.raxcl.service.FriendService;

import java.util.Map;

/**
 * @Description: 友链页面后台管理
 * @author Raxcl
 * @date 2022-01-07 10:19:27
 */
@RestController
@RequestMapping("/admin")
public class FriendAdminController {
	private final FriendService friendService;

	public FriendAdminController(FriendService friendService) {
		this.friendService = friendService;
	}

	/**
	 * 分页获取友链列表
	 *
	 * @param pageNum  页码
	 * @param pageSize 每页条数
	 * @return Result
	 */
	@GetMapping("/friends")
	public Result friends(@RequestParam(defaultValue = "1") Integer pageNum,
	                      @RequestParam(defaultValue = "10") Integer pageSize) {
		String orderBy = "create_time asc";
		PageMethod.startPage(pageNum, pageSize, orderBy);
		PageInfo<Friend> pageInfo = new PageInfo<>(friendService.getFriendList());
		return Result.success("请求成功", pageInfo);
	}

	/**
	 * 更新友链公开状态
	 *
	 * @param id        友链id
	 * @param published 是否公开
	 * @return Result
	 */
	@OperationLogger("更新友链公开状态")
	@PutMapping("/friend/published")
	public Result updatePublished(@RequestParam Long id, @RequestParam Boolean published) {
		friendService.updateFriendPublishedById(id, published);
		return Result.success("操作成功");
	}

	/**
	 * 添加友链
	 *
	 * @param friend 友链DTO
	 * @return Result
	 */
	@OperationLogger("添加友链")
	@PostMapping("/friend")
	public Result saveFriend(@RequestBody Friend friend) {
		friendService.saveFriend(friend);
		return Result.success("添加成功");
	}

	/**
	 * 更新友链
	 *
	 * @param friendDTO 友链DTO
	 * @return Result
	 */
	@OperationLogger("更新友链")
	@PutMapping("/friend")
	public Result updateFriend(@RequestBody FriendDTO friendDTO) {
		friendService.updateFriend(friendDTO);
		return Result.success("修改成功");
	}

	/**
	 * 按id删除友链
	 *
	 * @param id id
	 * @return Result
	 */
	@OperationLogger("删除友链")
	@DeleteMapping("/friend")
	public Result deleteFriend(@RequestParam Long id) {
		friendService.deleteFriend(id);
		return Result.success("删除成功");
	}

	/**
	 * 获取友链页面信息
	 */
	@GetMapping("/friendInfo")
	public Result friendInfo() {
		return Result.success("请求成功", friendService.getFriendInfo(false, false));
	}

	/**
	 * 修改友链页面评论开放状态
	 *
	 * @param commentEnabled 是否开放评论
	 * @return Result
	 */
	@OperationLogger("修改友链页面评论开放状态")
	@PutMapping("/friendInfo/commentEnabled")
	public Result updateFriendInfoCommentEnabled(@RequestParam Boolean commentEnabled) {
		friendService.updateFriendInfoCommentEnabled(commentEnabled);
		return Result.success("修改成功");
	}

	/**
	 * 修改友链页面content
	 *
	 * @param map 包含content的JSON对象
	 * @return Result
	 */
	@OperationLogger("修改友链页面信息")
	@PutMapping("/friendInfo/content")
	public Result updateFriendInfoContent(@RequestBody Map<String, String> map) {
		friendService.updateFriendInfoContent(map.get("content"));
		return Result.success("修改成功");
	}
}
