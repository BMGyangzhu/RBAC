package org.example.rbac.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author bgmyangzhu
 * @date 2025/2/26 14:20
 */

@Data
@TableName("wx_user")
public class WxUser {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String openId;
    
//    private String nickname;
    
//    private String avatarUrl;
    
}
