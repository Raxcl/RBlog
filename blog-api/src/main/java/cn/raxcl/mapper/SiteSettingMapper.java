package cn.raxcl.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import cn.raxcl.entity.SiteSetting;

import java.util.List;

/**
 * @Description: 站点设置持久层接口
 * @Author: Raxcl
 * @Date: 2020-08-03
 */
@Mapper
@Repository
public interface SiteSettingMapper {
	List<SiteSetting> getList();

	List<SiteSetting> getFriendInfo();

	String getWebTitleSuffix();

	int updateSiteSetting(SiteSetting siteSetting);

	int deleteSiteSettingById(Integer id);

	int saveSiteSetting(SiteSetting siteSetting);

	int updateFriendInfoContent(String content);

	int updateFriendInfoCommentEnabled(Boolean commentEnabled);
}
