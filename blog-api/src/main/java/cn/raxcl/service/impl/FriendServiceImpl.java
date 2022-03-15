package cn.raxcl.service.impl;

import cn.raxcl.model.dto.FriendDTO;
import cn.raxcl.model.vo.FriendVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.raxcl.constant.RedisKeyConstants;
import cn.raxcl.entity.Friend;
import cn.raxcl.entity.SiteSetting;
import cn.raxcl.exception.PersistenceException;
import cn.raxcl.mapper.FriendMapper;
import cn.raxcl.mapper.SiteSettingMapper;
import cn.raxcl.model.vo.FriendInfoVO;
import cn.raxcl.service.FriendService;
import cn.raxcl.service.RedisService;
import cn.raxcl.util.markdown.MarkdownUtils;

import java.util.Date;
import java.util.List;

/**
 * 友链业务层实现
 * @author Raxcl
 * @date 2021-12-31 17:45:13
 */
@Service
public class FriendServiceImpl implements FriendService {
	private final FriendMapper friendMapper;
	private final SiteSettingMapper siteSettingMapper;
	private final RedisService redisService;

	public FriendServiceImpl(FriendMapper friendMapper, SiteSettingMapper siteSettingMapper, RedisService redisService) {
		this.friendMapper = friendMapper;
		this.siteSettingMapper = siteSettingMapper;
		this.redisService = redisService;
	}

	@Override
	public List<Friend> getFriendList() {
		return friendMapper.getFriendList();
	}

	@Override
	public List<FriendVO> getFriendVOList() {
		return friendMapper.getFriendVOList();
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updateFriendPublishedById(Long friendId, Boolean published) {
		if (friendMapper.updateFriendPublishedById(friendId, published) != 1) {
			throw new PersistenceException("操作失败");
		}
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void saveFriend(Friend friend) {
		friend.setViews(0);
		friend.setCreateTime(new Date());
		if (friendMapper.saveFriend(friend) != 1) {
			throw new PersistenceException("添加失败");
		}
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updateFriend(FriendDTO friendDTO) {
		if (friendMapper.updateFriend(friendDTO) != 1) {
			throw new PersistenceException("修改失败");
		}
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void deleteFriend(Long id) {
		if (friendMapper.deleteFriend(id) != 1) {
			throw new PersistenceException("删除失败");
		}
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updateViewsByNickname(String nickname) {
		if (friendMapper.updateViewsByNickname(nickname) != 1) {
			throw new PersistenceException("操作失败");
		}
	}

	@Override
	public FriendInfoVO getFriendInfo(boolean cache, boolean md) {
		//友链页面信息key
		String redisKey = RedisKeyConstants.FRIEND_INFO_MAP;
		if (cache) {
			FriendInfoVO friendInfoVOFromRedis = redisService.getObjectByValue(redisKey, FriendInfoVO.class);
			if (friendInfoVOFromRedis != null) {
				return friendInfoVOFromRedis;
			}
		}
		List<SiteSetting> siteSettings = siteSettingMapper.getFriendInfo();
		FriendInfoVO friendInfoVO = new FriendInfoVO();
		for (SiteSetting siteSetting : siteSettings) {
			if ("friendContent".equals(siteSetting.getNameEn())) {
				if (md) {
					friendInfoVO.setContent(MarkdownUtils.markdownToHtmlExtensions(siteSetting.getValue()));
				} else {
					friendInfoVO.setContent(siteSetting.getValue());
				}
			} else if ("friendCommentEnabled".equals(siteSetting.getNameEn())) {
				friendInfoVO.setCommentEnabled("1".equals(siteSetting.getValue()));
			}
		}
		if (cache && md) {
			redisService.saveObjectToValue(redisKey, friendInfoVO);
		}
		return friendInfoVO;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updateFriendInfoContent(String content) {
		if (siteSettingMapper.updateFriendInfoContent(content) != 1) {
			throw new PersistenceException("修改失败");
		}
		deleteFriendInfoRedisCache();
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updateFriendInfoCommentEnabled(Boolean commentEnabled) {
		if (siteSettingMapper.updateFriendInfoCommentEnabled(commentEnabled) != 1) {
			throw new PersistenceException("修改失败");
		}
		deleteFriendInfoRedisCache();
	}

	/**
	 * 删除友链页面缓存
	 */
	private void deleteFriendInfoRedisCache() {
		redisService.deleteCacheByKey(RedisKeyConstants.FRIEND_INFO_MAP);
	}
}
