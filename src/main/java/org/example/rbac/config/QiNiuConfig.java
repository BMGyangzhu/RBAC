package org.example.rbac.config;

import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;




@Data
@Component
@ConfigurationProperties(prefix = "qiniu.kodo")
public class QiNiuConfig {
    /**
     * 账号
     */
    private String accessKey;
    /**
     * 密钥
     */
    private String secretKey;
    /**
     * bucketName
     */
    private String bucketName;
    
    private String domain;

    public Auth buildAuth() {
        String accessKey = this.getAccessKey();
        String secretKey = this.getSecretKey();
        return Auth.create(accessKey, secretKey);
    }



}