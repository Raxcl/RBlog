package cn.raxcl.service.impl;

import cn.raxcl.annotation.AopProxy;
import cn.raxcl.constant.SiteSettingConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.raxcl.constant.RedisKeyConstants;
import cn.raxcl.entity.SiteSetting;
import cn.raxcl.exception.PersistenceException;
import cn.raxcl.mapper.SiteSettingMapper;
import cn.raxcl.model.bean.Badge;
import cn.raxcl.model.bean.Copyright;
import cn.raxcl.model.bean.Favorite;
import cn.raxcl.model.vo.IntroductionVO;
import cn.raxcl.service.RedisService;
import cn.raxcl.service.SiteSettingService;
import cn.raxcl.util.JacksonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 站点设置业务层实现
 * @author Raxcl
 * @date 2022-03-16 15:11:07
 */
@Service
public class SiteSettingServiceImpl implements SiteSettingService, AopProxy<SiteSettingServiceImpl> {
	private final SiteSettingMapper siteSettingMapper;
	private final RedisService redisService;

	private static final Pattern PATTERN = Pattern.compile("\"(.*?)\"");

	public SiteSettingServiceImpl(SiteSettingMapper siteSettingMapper, RedisService redisService) {
		this.siteSettingMapper = siteSettingMapper;
		this.redisService = redisService;
	}

	@Override
	public Map<String, List<SiteSetting>> getList() {
		List<SiteSetting> siteSettings = siteSettingMapper.getList();
		Map<String, List<SiteSetting>> map = new HashMap<>(16);
		List<SiteSetting> type1 = new ArrayList<>();
		List<SiteSetting> type2 = new ArrayList<>();
		List<SiteSetting> type3 = new ArrayList<>();
		for (SiteSetting s : siteSettings) {
			if (s.getType() == 1) {
				type1.add(s);
			} else if (s.getType() == 2) {
				type2.add(s);
			} else if (s.getType() == 3) {
				type3.add(s);
			}
		}
		map.put("type1", type1);
		map.put("type2", type2);
		map.put("type3", type3);
		return map;
	}

	@Override
	public Map<String, Object> getSiteInfo() {
		String redisKey = RedisKeyConstants.SITE_INFO_MAP;
		Map<String, Object> siteInfoMapFromRedis = redisService.getMapByValue(redisKey);
		if (siteInfoMapFromRedis != null) {
			return siteInfoMapFromRedis;
		}
		List<SiteSetting> siteSettings = siteSettingMapper.getList();
		Map<String, Object> map = new HashMap<>(16);
		Map<String, Object> siteInfo = new HashMap<>(16);
		List<Badge> badges = new ArrayList<>();
		IntroductionVO introductionVO = new IntroductionVO();
		List<Favorite> favorites = new ArrayList<>();
		List<String> rollTexts = new ArrayList<>();
		for (SiteSetting s : siteSettings) {
			if (s.getType() == 1) {
				if ("copyright".equals(s.getNameEn())) {
					Copyright copyright = JacksonUtils.readValue(s.getValue(), Copyright.class);
					siteInfo.put(s.getNameEn(), copyright);
				} else {
					siteInfo.put(s.getNameEn(), s.getValue());
				}
			} else if (s.getType() == 2) {
				Badge badge = JacksonUtils.readValue(s.getValue(), Badge.class);
				badges.add(badge);
			} else if (s.getType() == 3) {
				siteInfoDispose(s, introductionVO, favorites, rollTexts);

			}
		}

		introductionVO.setFavorites(favorites);
		introductionVO.setRollText(rollTexts);
		map.put("introduction", introductionVO);
		map.put("siteInfo", siteInfo);
		map.put("badges", badges);
		redisService.saveMapToValue(redisKey, map);
		return map;
	}

	private void siteInfoDispose(SiteSetting s, IntroductionVO introductionVO, List<Favorite> favorites, List<String> rollTexts) {
		if (SiteSettingConstants.AVATAR.equals(s.getNameEn())) {
			introductionVO.setAvatar(s.getValue());
		} else if (SiteSettingConstants.NAME.equals(s.getNameEn())) {
			introductionVO.setName(s.getValue());
		} else if (SiteSettingConstants.GITHUB.equals(s.getNameEn())) {
			introductionVO.setGithub(s.getValue());
		} else if (SiteSettingConstants.QQ.equals(s.getNameEn())) {
			introductionVO.setQq(s.getValue());
		} else if (SiteSettingConstants.BILI_BILI.equals(s.getNameEn())) {
			introductionVO.setBilibili(s.getValue());
		} else if (SiteSettingConstants.NET_EASE.equals(s.getNameEn())) {
			introductionVO.setNetease(s.getValue());
		} else if (SiteSettingConstants.EMAIL.equals(s.getNameEn())) {
			introductionVO.setEmail(s.getValue());
		} else if (SiteSettingConstants.FAVORITE.equals(s.getNameEn())) {
			Favorite favorite = JacksonUtils.readValue(s.getValue(), Favorite.class);
			favorites.add(favorite);
		} else if (SiteSettingConstants.ROLL_TEXT.equals(s.getNameEn())) {
			Matcher m = PATTERN.matcher(s.getValue());
			while (m.find()) {
				rollTexts.add(m.group(1));
			}
		}
	}

	@Override
	public String getWebTitleSuffix() {
		return siteSettingMapper.getWebTitleSuffix();
	}

	@Override
	public void updateSiteSetting(List<LinkedHashMap<String,Object>> siteSettings, List<Integer> deleteIds) {
		//删除
		for (Integer id : deleteIds) {
			self().deleteOneSiteSettingById(id);
		}
		for (LinkedHashMap<String,Object> s : siteSettings) {
			SiteSetting siteSetting = JacksonUtils.convertValue(s, SiteSetting.class);
			//修改
			if (siteSetting.getId() != null) {
				self().updateOneSiteSetting(siteSetting);
			} else {
				self().saveOneSiteSetting(siteSetting);
			}
		}
		deleteSiteInfoRedisCache();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveOneSiteSetting(SiteSetting siteSetting) {
		if (siteSettingMapper.saveSiteSetting(siteSetting) != 1) {
			throw new PersistenceException("配置添加失败");
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateOneSiteSetting(SiteSetting siteSetting) {
		if (siteSettingMapper.updateSiteSetting(siteSetting) != 1) {
			throw new PersistenceException("配置修改失败");
		}
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void deleteOneSiteSettingById(Integer id) {
		if (siteSettingMapper.deleteSiteSettingById(id) != 1) {
			throw new PersistenceException("配置删除失败");
		}
	}

	/**
	 * 删除站点信息缓存
	 */
	private void deleteSiteInfoRedisCache() {
		redisService.deleteCacheByKey(RedisKeyConstants.SITE_INFO_MAP);
	}
}
