package cn.raxcl.controller.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.raxcl.annotation.OperationLogger;
import cn.raxcl.entity.SiteSetting;
import cn.raxcl.util.common.Result;
import cn.raxcl.service.SiteSettingService;

import java.util.*;

/**
 * @Description: 站点设置后台管理
 * @author Raxcl
 * @date 2022-01-07 10:33:36
 */
@RestController
@RequestMapping("/admin")
public class SiteSettingAdminController {
	private final SiteSettingService siteSettingService;

	public SiteSettingAdminController(SiteSettingService siteSettingService) {
		this.siteSettingService = siteSettingService;
	}

	/**
	 * 获取所有站点配置信息
	 *
	 * @return Result
	 */
	@GetMapping("/siteSettings")
	public Result siteSettings() {
		Map<String, List<SiteSetting>> typeMap = siteSettingService.getList();
		return Result.success("请求成功", typeMap);
	}

	/**
	 * 修改、删除(部分配置可为空，但不可删除)、添加(只能添加部分)站点配置
	 *
	 * @param map 包含所有站点信息更新后的数据 map => {settings=[更新后的所有配置List], deleteIds=[要删除的配置id List]}
	 * @return Result
	 */
	@OperationLogger("更新站点配置信息")
	@PostMapping("/siteSettings")
	public Result updateAll(@RequestBody Map<String, Object> map) {
		//TODO 类型转换失败，待后期解决
		List<LinkedHashMap<String,Object>> siteSettings = (List<LinkedHashMap<String,Object>>) map.get("settings");
		List<Integer> deleteIds = (List<Integer>) map.get("deleteIds");
		siteSettingService.updateSiteSetting(siteSettings, deleteIds);
		return Result.success("更新成功");
	}

	/**
	 * 查询网页标题后缀
	 *
	 * @return Result
	 */
	@GetMapping("/webTitleSuffix")
	public Result getWebTitleSuffix() {
		return Result.success("请求成功", siteSettingService.getWebTitleSuffix());
	}
}
