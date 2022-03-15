package cn.raxcl.mapper;

import cn.raxcl.model.dto.FriendDTO;
import cn.raxcl.model.vo.FriendVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import cn.raxcl.entity.Friend;

import java.util.List;

/**
 * 友链持久层接口
 * @author Raxcl
 * @date 2022-01-07 10:23:39
 */
@Mapper
@Repository
public interface FriendMapper {
	/**
	 * 获取友链list
	 * @return List<Friend>
	 */
	List<Friend> getFriendList();

	/**
	 * 获取友链vo列表
	 * @return List<FriendVO>
	 */
	List<FriendVO> getFriendVOList();

	/**
	 * 更新可发版友链
	 * @param id id
	 * @param published published
	 * @return int
	 */
	int updateFriendPublishedById(Long id, Boolean published);

	/**
	 * 保存友链信息
	 * @param friend friend
	 * @return int
	 */
	int saveFriend(Friend friend);

	/**
	 * 更新友链信息
	 * @param friendDTO friendDTO
	 * @return int
	 */
	int updateFriend(FriendDTO friendDTO);

	/**
	 * 删除友链信息
	 * @param id id
	 * @return int
	 */
	int deleteFriend(Long id);

	/**
	 * 按昵称增加友链浏览次数
	 * @param nickname nickname
	 * @return int
	 */
	int updateViewsByNickname(String nickname);
}
