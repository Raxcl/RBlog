package cn.raxcl.service;

import cn.raxcl.entity.Friend;
import cn.raxcl.model.vo.FriendInfo;

import java.util.List;

public interface FriendService {
	List<Friend> getFriendList();

	List<cn.raxcl.model.vo.Friend> getFriendVOList();

	void updateFriendPublishedById(Long friendId, Boolean published);

	void saveFriend(Friend friend);

	void updateFriend(cn.raxcl.model.dto.Friend friend);

	void deleteFriend(Long id);

	void updateViewsByNickname(String nickname);

	/**
	 * 获取友链页面信息
	 * @param cache 是否开启缓存
	 * @param md cache
	 * @return FriendInfo
	 */
	FriendInfo getFriendInfo(boolean cache, boolean md);

	/**
	 * 修改友链页面信息
	 * @param content 具体内容
	 */
	void updateFriendInfoContent(String content);

	void updateFriendInfoCommentEnabled(Boolean commentEnabled);
}
