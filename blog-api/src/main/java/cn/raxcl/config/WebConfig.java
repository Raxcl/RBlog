package cn.raxcl.config;

import cn.raxcl.config.properties.UploadProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import cn.raxcl.interceptor.AccessLimitInterceptor;

/**
 * 配置CORS跨域支持、拦截器
 * @author Raxcl
 * @date 2022-01-07 17:59:49
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
	private final AccessLimitInterceptor accessLimitInterceptor;
	private final UploadProperties uploadProperties;

	public WebConfig(AccessLimitInterceptor accessLimitInterceptor, UploadProperties uploadProperties) {
		this.accessLimitInterceptor = accessLimitInterceptor;
		this.uploadProperties = uploadProperties;
	}

	/**
	 * 跨域请求
	 *
	 * @param registry registry
	 */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins("*")
				.allowedHeaders("*")
				.allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS")
				.maxAge(3600);
	}

	/**
	 * 请求拦截器
	 *
	 * @param registry registry
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(accessLimitInterceptor);
	}

	/**
	 * 本地静态资源路径映射
	 *
	 * @param registry registry
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler(uploadProperties.getAccessPath()).addResourceLocations(uploadProperties.getResourcesLocations());
	}
}
