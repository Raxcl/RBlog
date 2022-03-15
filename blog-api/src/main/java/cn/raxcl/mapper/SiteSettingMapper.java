package cn.raxcl.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import cn.raxcl.entity.SiteSetting;

import java.util.List;

/**
 * 站点设置持久层接口
 * @author Raxcl
 * @date 2022-01-07 10:30:19
 */
@Mapper
@Repository
public interface SiteSettingMapper {
	/**
	 * 获取所有站点配置信息
	 * @return List<SiteSetting>
	 */
	List<SiteSetting> getList();

	/**
	 * 查询友链页面信息
	 * @return List<SiteSetting>
	 */
	List<SiteSetting> getFriendInfo();

	/**
	 * 查询网页标题后缀
	 * @return String
	 */
	String getWebTitleSuffix();

	/**
	 * 更新
	 * @param siteSetting siteSetting
	 * @return int
	 */
	int updateSiteSetting(SiteSetting siteSetting);

	/**
	 * 根据id删除站点信息
	 * @param id id
	 * @return int
	 */
	int deleteSiteSettingById(Integer id);

	/**
	 * 添加
	 * @param siteSetting siteSetting
	 * @return int
	 */
	int saveSiteSetting(SiteSetting siteSetting);

	/**
	 * 修改友链页面信息
	 * @param content 具体内容
	 * @return int
	 */
	int updateFriendInfoContent(String content);

	/**
	 * 修改友链页面评论开放状态
	 * @param commentEnabled commentEnabled
	 * @return int
	 */
	int updateFriendInfoCommentEnabled(Boolean commentEnabled);
}
