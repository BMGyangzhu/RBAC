package org.example.rbac.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author bgmyangzhu
 * @date 2025/2/27 14:22
 */
@Component
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {

    /**
     * 微信小程序用户登录生成jwt令牌相关配置
     */
    private  String wxSecretKey;
    private long wxTtl;
    private String wxTokenName;
}
