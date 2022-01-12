package cn.raxcl.service;

import cn.raxcl.model.vo.BlogInfoVO;
import cn.raxcl.model.vo.PageResultVO;

import java.util.List;
import java.util.Map;
/**
 * redis 工具类接口
 * @author c-long.chan
 * @date 2022-01-12 15:45:56
 */
public interface RedisService {

	/**
	 * 根据hash值获取博客页面信息
	 * @param hash hash
	 * @param pageNum pageNum
	 * @return PageResultVO<BlogInfoVO>
	 */
	PageResultVO<BlogInfoVO> getBlogInfoPageResultByHash(String hash, Integer pageNum);

	/**
	 * 保存键值到hash中
	 * @param hash hash
	 * @param key key
	 * @param value value
	 */
	void saveKvToHash(String hash, Object key, Object value);

	/**
	 * 保持map到hash中
	 * @param hash hash
	 * @param map map
	 */
	void saveMapToHash(String hash, Map<Long, Integer> map);

	/**
	 * 根据hash值获取map
	 * @param hash hash
	 * @return Map<Object, Object>
	 */
	Map<Object, Object> getMapByHash(String hash);

	/**
	 * 根据hashKey获取value
	 * @param hash hash
	 * @param key key
	 * @return Object
	 */
	Object getValueByHashKey(String hash, Object key);

	/**
	 * 更具hashkey新增
	 * @param hash hash
	 * @param key key
	 * @param increment increment
	 */
	void incrementByHashKey(String hash, Object key, int increment);

	/**
	 * 删除信息
	 * @param hash hash
	 * @param key key
	 */
	void deleteByHashKey(String hash, Object key);

	/**
	 * 根据值获取list
	 * @param key key
	 * @param <T> <T>
	 * @return List<T>
	 */
	<T> List<T> getListByValue(String key);

	/**
	 * 保存list到value
	 * @param key key
	 * @param list list
	 * @param <T> <T>
	 */
	<T> void saveListToValue(String key, List<T> list);

	/**
	 * 根据值获取map
	 * @param key key
	 * @param <T> <T>
	 * @return Map<String, T>
	 */
	<T> Map<String, T> getMapByValue(String key);

	/**
	 * 保存map到value
	 * @param key key
	 * @param map map
	 * @param <T> <T>
	 */
	<T> void saveMapToValue(String key, Map<String, T> map);

	/**
	 * 根据value获取Object
	 * @param key key
	 * @param t t
	 * @param <T> <T>
	 * @return T
	 */
	<T> T getObjectByValue(String key, Class<?> t);

	/**
	 * 根据key插入信息
	 * @param key key
	 * @param increment increment
	 */
	void incrementByKey(String key, int increment);

	/**
	 * 保存object到value
	 * @param key key
	 * @param object object
	 */
	void saveObjectToValue(String key, Object object);

	/**
	 * 保存value到set
	 * @param key key
	 * @param value value
	 */
	void saveValueToSet(String key, Object value);

	/**
	 * 根据set获取总数
	 * @param key key
	 * @return int
	 */
	int countBySet(String key);

	/**
	 * 根据set删除值
	 * @param key key
	 * @param value value
	 */
	void deleteValueBySet(String key, Object value);

	/**
	 * 校验Redis中是否存在uuid
	 * @param key key
	 * @param value value
	 * @return boolean
	 */
	boolean hasValueInSet(String key, Object value);

	/**
	 * 删除友链页面缓存
	 * @param key 键
	 */
	void deleteCacheByKey(String key);

	/**
	 * 校验key是否存在
	 * @param key key
	 * @return boolean
	 */
	boolean hasKey(String key);

	/**
	 * 设置key的过期时间
	 * @param key key
	 * @param time time
	 */
	void expire(String key, long time);
}
