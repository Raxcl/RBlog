package cn.raxcl.config.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 腾讯云配置(目前用于评论中QQ头像的图床)
 * @author dragon
 * @date 2022/11/6 12:09
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
@Configuration
@ConfigurationProperties(prefix = "upload.txyun")
public class TxYunProperties {
    /**
     * id
     */
    private String secretId;
    /**
     * key
     */
    private String secretKey;
    /**
     * 腾讯云存储空间名称
     */
    private String bucketName;
    /**
     * 地域
     */
    private String region;
    /**
     * 存储路径
     */
    private String path;
    /**
     * CDN访问域名
     */
    private String domain;

}
