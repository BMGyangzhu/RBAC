package org.example.rbac.controller;

import cn.hutool.jwt.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.rbac.domain.WxUser;
import org.example.rbac.domain.vo.WxUserVO;
import org.example.rbac.properties.JwtProperties;
import org.example.rbac.service.WxUserService;
import org.example.rbac.util.JwtUtil;
import org.example.rbac.util.Result;
import org.example.rbac.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author bgmyangzhu
 * @date 2025/2/26 14:26
 */
@RestController
@CrossOrigin
@RequestMapping("/wx/")
@Slf4j
public class WxUserController {

    @Autowired
    private WxUserService wxUserService;

    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> request) throws IOException {
        log.info("微信登录:{}", request);
        String code = request.get("code");
//        String nickname = request.get("nickname");
//        String avater = request.get("avater");

        WxUser wxUser = wxUserService.wechatLogin(code);
        // 为微信用户生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", wxUser.getId());
        String token = JwtUtil.createJWT(jwtProperties.getWxSecretKey(), jwtProperties.getWxTtl(), claims);

        WxUserVO wxUserVO = WxUserVO.builder()
                                    .id(wxUser.getId())
                                    .openId(wxUser.getOpenId())
                                    .token(token)
                                    .build();

        return Result.success(wxUserVO);
    }
}
