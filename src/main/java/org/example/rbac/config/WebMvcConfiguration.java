package org.example.rbac.config;

import lombok.extern.slf4j.Slf4j;
import org.example.rbac.interceptor.JwtTokenWxUserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * 配置类，注册web层相关组件
 * @author bgmyangzhu
 * @date 2025/2/27 15:30
 */
@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport {
    
    @Autowired
    private JwtTokenWxUserInterceptor jwtTokenWxUserInterceptor;

    
    protected void addInterceptors(InterceptorRegistry registry){
        log.info("开始注册自定义拦截器...");
        registry.addInterceptor(jwtTokenWxUserInterceptor)
                .addPathPatterns("/**/wx/**")
                .excludePathPatterns("/**/login")
                .excludePathPatterns("/shop/wx");
    }
}
