package cn.raxcl.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import cn.raxcl.interceptor.AccessLimitInterceptor;

/**
 * @Description: 配置CORS跨域支持、拦截器
 * @author Raxcl
 * @date 2022-01-07 17:59:49
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
	private final AccessLimitInterceptor accessLimitInterceptor;
	private String accessPath;
	private String resourcesLocations;

	public WebConfig(AccessLimitInterceptor accessLimitInterceptor) {
		this.accessLimitInterceptor = accessLimitInterceptor;
	}

	/**
	 * @param accessPath 请求地址映射
	 */
	@Value("${upload.access.path}")
	public void setAccessPath(String accessPath) {
		this.accessPath = accessPath;
	}

	/**
	 * @param resourcesLocations 本地文件路径映射
	 */
	@Value("${upload.resources.locations}")
	public void setResourcesLocations(String resourcesLocations) {
		this.resourcesLocations = resourcesLocations;
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
		registry.addResourceHandler(accessPath).addResourceLocations(resourcesLocations);
	}
}
