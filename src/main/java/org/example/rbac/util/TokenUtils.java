package org.example.rbac.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.example.rbac.domain.User;
import org.example.rbac.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author bgmyangzhu
 * @date 2025/1/2 14:58
 */
@Component
public class TokenUtils {
    
    private static UserService staticUserService;
    
    @Autowired
    private UserService userService;
    
    @PostConstruct
    public void setUserService(){
        staticUserService = userService;
    }
    /**
     * 生成token
     * @param userId
     * @param sign
     * @return
     */
    public static String getToken(String userId, String sign){
        return JWT.create().withAudience(userId) //将userid保存到token里面，作为载荷
                .withExpiresAt(DateUtil.offsetHour(new Date(),2)) // 2小时后token过期
                .sign(Algorithm.HMAC256(sign)); //以password作为token的密钥
    }
    
    public static User getCurrentUser(){
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader("token");
            if (StrUtil.isNotBlank(token)){
                String userId = JWT.decode(token).getAudience().get(0);
                return staticUserService.getById(Integer.valueOf(userId));
                
            }
        } catch (Exception e){
            return null;
        }
        return null;
    }
}
