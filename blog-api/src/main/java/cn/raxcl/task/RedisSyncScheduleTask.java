package cn.raxcl.task;

import org.springframework.stereotype.Component;
import cn.raxcl.constant.RedisKeyConstant;
import cn.raxcl.service.BlogService;
import cn.raxcl.service.RedisService;

import java.util.Map;
import java.util.Set;

/**
 * @Description: Redis相关定时任务
 * @author Raxcl
 * @date 2022-01-07 18:53:08
 */
@Component
public class RedisSyncScheduleTask {
	private final RedisService redisService;
	private final BlogService blogService;

	public RedisSyncScheduleTask(RedisService redisService, BlogService blogService) {
		this.redisService = redisService;
		this.blogService = blogService;
	}

	//TODO 数据持久化，后期优化
	/**
	 * 从Redis同步博客文章浏览量到数据库
	 */
	public void syncBlogViewsToDatabase() {
		String redisKey = RedisKeyConstant.BLOG_VIEWS_MAP;
		Map blogViewsMap = redisService.getMapByHash(redisKey);
		Set<Integer> keys = blogViewsMap.keySet();
		for (Integer key : keys) {
			Integer views = (Integer) blogViewsMap.get(key);
			blogService.updateViews(key.longValue(), views);
		}
	}
}
