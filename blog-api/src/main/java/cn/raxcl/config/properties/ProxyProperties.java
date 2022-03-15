package cn.raxcl.config.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

/**
 * 代理配置(目前用于RestTemplate发送tg消息)
 *
 * @author Raxcl
 * @date 2022-03-15 23:47:17
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
@Configuration
@ConfigurationProperties(prefix = "http.proxy.server")
public class ProxyProperties {
	/**
	 * 代理服务器地址
	 */
	private String host;
	/**
	 * 代理服务器端口
	 */
	private Integer port;
	/**
	 * 连接超时(单位毫秒)，通常不应该为0，0为无限超时时间，-1为系统的默认超时时间
	 *
	 * @see SimpleClientHttpRequestFactory#setConnectTimeout(int)
	 */
	private Integer timeout;
}
