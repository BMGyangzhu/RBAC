package org.example.rbac.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.example.rbac.domain.WxUser;
import org.example.rbac.mapper.WxUserMapper;
import org.example.rbac.service.WxUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author bgmyangzhu
 * @date 2025/2/26 14:25
 */
@Service
@Slf4j
public class WxUserServiceImpl extends ServiceImpl<WxUserMapper, WxUser> implements WxUserService {
    
    @Autowired
    private WxUserMapper wxUserMapper;

    private static final String WX_API_URL = "https://api.weixin.qq.com/sns/jscode2session";
    private static final String APPID = "wx8e43b90773ed7e9d";
    private static final String SECRET = "69cd268b1ed1a2747eb966d5f290d444";

    @Override
    public WxUser wechatLogin(String code) throws IOException {
        
        String url = WX_API_URL + "?appid=" + APPID + "&secret=" + SECRET + "&js_code=" + code + "&grant_type=authorization_code";
        String response = sendGetRequest(url);
        log.info("微信登录response: {}", response);
        String openId = parseOpenId(response);
        LambdaQueryWrapper<WxUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WxUser::getOpenId,openId);
        WxUser wxUser = getOne(queryWrapper);
        if (wxUser == null) {
            // 如果用户不存在，创建新的用户
            wxUser = new WxUser();
            wxUser.setOpenId(openId);
//            wxUser.setNickname(nickname);
//            wxUser.setAvatarUrl(avatar);
        }
//        wxUser.setNickname(nickname);
//        wxUser.setAvatarUrl(avatar);
        saveOrUpdate(wxUser);
        
        return wxUser;
        
    }

    /**
     * 发送 HTTP GET 请求
     * @param url
     * @return
     * @throws IOException
     */
    private String sendGetRequest(String url) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        return EntityUtils.toString(response.getEntity());
    }

    /**
     * 解析 openId
     */
    private String parseOpenId(String response) {
        // 示例响应：{"openid":"OPENID","session_key":"SESSION_KEY"}
        return response.split("\"openid\":\"")[1].split("\"")[0];
    }
}
