package cn.raxcl.controller.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.raxcl.constant.RedisKeyConstant;
import cn.raxcl.entity.CityVisitor;
import cn.raxcl.util.common.Result;
import cn.raxcl.service.DashboardService;
import cn.raxcl.service.RedisService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台管理仪表盘
 * @author Raxcl
 * @date 2022-01-07 15:43:53
 */
@RestController
@RequestMapping("/admin")
public class DashboardAdminController {
	private final DashboardService dashboardService;
	private final RedisService redisService;

	public DashboardAdminController(DashboardService dashboardService, RedisService redisService) {
		this.dashboardService = dashboardService;
		this.redisService = redisService;
	}

	@GetMapping("/dashboard")
	public Result dashboard() {
		int todayPv = dashboardService.countVisitLogByToday();
		int todayUv = redisService.countBySet(RedisKeyConstant.IDENTIFICATION_SET);
		int blogCount = dashboardService.getBlogCount();
		int commentCount = dashboardService.getCommentCount();
		Map<String, Object> categoryBlogCountMap = dashboardService.getCategoryBlogCountMap();
		Map<String, Object> tagBlogCountMap = dashboardService.getTagBlogCountMap();
		Map<String, Object> visitRecordMap = dashboardService.getVisitRecordMap();
		List<CityVisitor> cityVisitorList = dashboardService.getCityVisitorList();

		Map<String, Object> map = new HashMap<>(16);
		map.put("pv", todayPv);
		map.put("uv", todayUv);
		map.put("blogCount", blogCount);
		map.put("commentCount", commentCount);
		map.put("category", categoryBlogCountMap);
		map.put("tag", tagBlogCountMap);
		map.put("visitRecord", visitRecordMap);
		map.put("cityVisitor", cityVisitorList);
		return Result.success("获取成功", map);
	}
}
