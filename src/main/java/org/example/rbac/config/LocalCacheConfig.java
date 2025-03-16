package org.example.rbac.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author bgmyangzhu
 * @date 2025/2/1 21:04
 */
@Configuration
public class LocalCacheConfig {
    
    @Bean
    public Cache<String, Boolean> caffeineCache(){
        return Caffeine.newBuilder()
                // 设置最后一次写入或访问后过期的固定时间
                .expireAfterWrite(8, TimeUnit.SECONDS)
                // 初始缓存空间的大小
                .initialCapacity(100)
                // 缓存的最大条数
                .maximumSize(100000)
                .build();
        
    }
}
