package org.example.rbac.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.rbac.domain.WxUser;
import org.example.rbac.mapper.WxUserMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * @author bgmyangzhu
 * @date 2025/2/26 14:25
 */
public interface WxUserService extends IService<WxUser> {
    
    WxUser wechatLogin(String code) throws IOException;
}
