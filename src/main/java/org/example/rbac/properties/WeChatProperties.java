package org.example.rbac.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author bgmyangzhu
 * @date 2025/2/27 14:25
 */
@Component
@ConfigurationProperties(prefix = "wechat")
public class WeChatProperties {
    
    private String appid; // 小程序的appid
    private String secret; // 小程序的密钥
}
