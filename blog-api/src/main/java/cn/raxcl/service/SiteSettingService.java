package cn.raxcl.service;

import cn.raxcl.entity.SiteSetting;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author c-long.chan
 * @date 2022-01-07 11:25:25
 */
public interface SiteSettingService {
	/**
	 * 获取所有站点配置信息
	 * @return Map<String, List<SiteSetting>>
	 */
	Map<String, List<SiteSetting>> getList();

	/**
	 * 获取站点信息
	 * @return Map<String, Object>
	 */
	Map<String, Object> getSiteInfo();

	/**
	 * 查询网页标题后缀
	 * @return String
	 */
	String getWebTitleSuffix();

	/**
	 * 更新站点配置
	 * @param siteSettings siteSettings
	 * @param deleteIds deleteIds
	 */
	void updateSiteSetting(List<LinkedHashMap<String,Object>> siteSettings, List<Integer> deleteIds);

	/**
	 * 保存站点配置
	 * @param siteSetting siteSetting
	 */
	void saveOneSiteSetting(SiteSetting siteSetting);

	/**
	 * 根据id删除站点信息
	 * @param id id
	 */
	void deleteOneSiteSettingById(Integer id);

	/**
	 * 更新
	 * @param siteSetting siteSetting
	 */
	void updateOneSiteSetting(SiteSetting siteSetting);
}
