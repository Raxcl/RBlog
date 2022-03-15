package cn.raxcl.task;

import org.springframework.stereotype.Component;
import cn.raxcl.constant.RedisKeyConstants;
import cn.raxcl.service.BlogService;
import cn.raxcl.service.RedisService;

import java.util.Map;
import java.util.Set;

/**
 * Redis相关定时任务
 * @author Raxcl
 * @date 2022-02-16 17:30:34
 */
@Component
public class RedisSyncScheduleTask {
	private final RedisService redisService;
	private final BlogService blogService;

	public RedisSyncScheduleTask(RedisService redisService, BlogService blogService) {
		this.redisService = redisService;
		this.blogService = blogService;
	}

	/**
	 * 从Redis同步博客文章浏览量到数据库   定时任务反射调用
	 */
	public void syncBlogViewsToDatabase() {
		String redisKey = RedisKeyConstants.BLOG_VIEWS_MAP;
		Map<Object, Object> blogViewsMap = redisService.getMapByHash(redisKey);
		Set<Object> keys = blogViewsMap.keySet();
		for (Object keyObj : keys) {
			Integer key = 0;
			if(keyObj instanceof Integer){
				key = (Integer)keyObj;
			}
			Integer views = (Integer) blogViewsMap.get(key);
			blogService.updateViews(key.longValue(), views);
		}
	}
}
