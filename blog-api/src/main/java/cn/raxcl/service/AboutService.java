package cn.raxcl.service;

import java.util.Map;

/**
 * @author c-long.chan
 * 2022-01-07 08:53:58
 */
public interface AboutService {
	/**
	 * 获取关于我页面信息
	 * @return Map<String, String>
	 */
	Map<String, String> getAboutInfo();

	/**
	 * 获取关于我页面配置
	 * @return Map<String, String>
	 */
	Map<String, String> getAboutSetting();

	/**
	 * 修改关于我页面
	 * @param map map
	 */
	void updateAbout(Map<String, String> map);

	/**
	 * 判断评论是否已关闭
	 * @return boolean
	 */
	boolean getAboutCommentEnabled();

	/**
	 * 更新关于我页面
	 * @param nameEn nameEn
	 * @param value value
	 */
	void updateOneAbout(String nameEn, String value);
}
