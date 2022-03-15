package cn.raxcl.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

/**
 * Redis序列化配置
 * @author Raxcl
 * @date 2022-01-07 17:47:54
 */
@Configuration
public class RedisSerializeConfig {

	/**
	 * 使用JSON序列化方式
	 *
	 * @param redisConnectionFactory redisConnectionFactory
	 * @return RedisTemplate<Object, Object>
	 */
	@Bean
	public RedisTemplate<Object, Object> jsonRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<Object, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
		template.setDefaultSerializer(serializer);
		return template;
	}
}
