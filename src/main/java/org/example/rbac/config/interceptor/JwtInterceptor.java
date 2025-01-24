package org.example.rbac.config.interceptor;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.apache.tomcat.util.bcel.Const;
import org.example.rbac.constant.Constants;
import org.example.rbac.domain.User;
import org.example.rbac.exception.ServiceException;
import org.example.rbac.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Struct;

/**
 * @author bgmyangzhu
 * @date 2025/1/2 15:11
 */

public class JwtInterceptor implements HandlerInterceptor {
    
    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        
//        if(token == null || token.isEmpty()){
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("{\"code\": \"401\", \"msg\": \"未找到该用户的token，请重新登录\"}");
//            return false;  // 如果没有token，返回401
//        }
        if (!(handler instanceof HandlerMethod)){
            return true;
        }
       
        // 获取token中的userId
        String userId;
        try {
            userId = JWT.decode(token).getAudience().get(0);
        } catch (JWTDecodeException j){
            throw new ServiceException(Constants.CODE_401,"token验证失败，请重新登录");
        }
        // 用户密码加签名验证token
        User user = userService.getById(userId);
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
        try {
            jwtVerifier.verify(token); // 验证token
        } catch (JWTVerificationException e){
            throw new ServiceException(Constants.CODE_401,"token验证失败，请重新登录");
        }
        return true;
    }

   
}
