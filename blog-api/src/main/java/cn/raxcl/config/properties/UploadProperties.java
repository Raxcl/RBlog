package cn.raxcl.config.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 静态文件上传访问路径配置(目前用于评论中QQ头像的本地存储)
 *
 * @author Raxcl
 * @date 2022-03-15 23:47:01
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
@Configuration
@ConfigurationProperties(prefix = "upload.file")
public class UploadProperties {
	/**
	 * 本地文件路径
	 */
	private String path;
	/**
	 * 请求地址映射
	 */
	private String accessPath;
	/**
	 * 本地文件路径映射
	 */
	private String resourcesLocations;
}
