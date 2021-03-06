package cn.raxcl.service.impl;

import cn.raxcl.exception.NotFoundException;
import cn.raxcl.model.vo.BlogInfoVO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import cn.raxcl.model.vo.PageResultVO;
import cn.raxcl.service.RedisService;
import cn.raxcl.util.JacksonUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 读写Redis相关操作
 * @author Raxcl
 * @date 2022-01-04 10:55:51
 */
@Service
public class RedisServiceImpl implements RedisService {
	@Resource
	private RedisTemplate<String,Object> jsonRedisTemplate;


	@Override
	@SuppressWarnings("unchecked")
	public PageResultVO<BlogInfoVO> getBlogInfoPageResultByHash(String hash, Integer pageNum) {
		if (Boolean.TRUE.equals(jsonRedisTemplate.opsForHash().hasKey(hash, pageNum))) {
			Object redisResult = jsonRedisTemplate.opsForHash().get(hash, pageNum);
			return JacksonUtils.convertValue(redisResult, PageResultVO.class);
		} else {
			return null;
		}
	}

	@Override
	public void saveKvToHash(String hash, Object key, Object value) {
		jsonRedisTemplate.opsForHash().put(hash, key, value);
	}

	@Override
	public void saveMapToHash(String hash, Map<Long, Integer> map) {
		jsonRedisTemplate.opsForHash().putAll(hash, map);
	}

	@Override
	public Map<Object, Object> getMapByHash(String hash) {
		return jsonRedisTemplate.opsForHash().entries(hash);
	}

	@Override
	public Object getValueByHashKey(String hash, Object key) {
		return jsonRedisTemplate.opsForHash().get(hash, key);
	}

	@Override
	public void incrementByHashKey(String hash, Object key, int increment) {
		if (increment < 0) {
			throw new NotFoundException("递增因子必须大于0");
		}
		jsonRedisTemplate.opsForHash().increment(hash, key, increment);
	}

	@Override
	public void deleteByHashKey(String hash, Object key) {
		jsonRedisTemplate.opsForHash().delete(hash, key);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> getListByValue(String key) {
		return (List<T>) jsonRedisTemplate.opsForValue().get(key);
	}

	@Override
	public <T> void saveListToValue(String key, List<T> list) {
		jsonRedisTemplate.opsForValue().set(key, list);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Map<String, T> getMapByValue(String key) {
		return (Map<String, T>) jsonRedisTemplate.opsForValue().get(key);
	}

	@Override
	public <T> void saveMapToValue(String key, Map<String, T> map) {
		jsonRedisTemplate.opsForValue().set(key, map);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getObjectByValue(String key, Class<?> t) {
		Object redisResult = jsonRedisTemplate.opsForValue().get(key);
		return (T) JacksonUtils.convertValue(redisResult, t);
	}

	@Override
	public void incrementByKey(String key, int increment) {
		if (increment < 0) {
			throw new NotFoundException("递增因子必须大于0");
		}
		jsonRedisTemplate.opsForValue().increment(key, increment);
	}

	@Override
	public void saveObjectToValue(String key, Object object) {
		jsonRedisTemplate.opsForValue().set(key, object);
	}

	@Override
	public void saveValueToSet(String key, Object value) {
		jsonRedisTemplate.opsForSet().add(key, value);
	}

	@Override
	public int countBySet(String key) {
		return Objects.requireNonNull(jsonRedisTemplate.opsForSet().size(key)).intValue();
	}

	@Override
	public void deleteValueBySet(String key, Object value) {
		jsonRedisTemplate.opsForSet().remove(key, value);
	}

	@Override
	public boolean hasValueInSet(String key, Object value) {
		return Boolean.TRUE.equals(jsonRedisTemplate.opsForSet().isMember(key, value));
	}

	@Override
	public void deleteCacheByKey(String key) {
		jsonRedisTemplate.delete(key);
	}

	@Override
	public boolean hasKey(String key) {
		return Boolean.TRUE.equals(jsonRedisTemplate.hasKey(key));
	}

	@Override
	public void expire(String key, long time) {
		jsonRedisTemplate.expire(key, time, TimeUnit.SECONDS);
	}
}
