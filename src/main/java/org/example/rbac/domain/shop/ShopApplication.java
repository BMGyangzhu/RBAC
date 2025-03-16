package org.example.rbac.domain.shop;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author bgmyangzhu
 * @date 2025/2/22 16:46
 */
@Data
@TableName("shop_application")
public class ShopApplication {


    @TableId(type = IdType.AUTO)  // 主键自增长
    private Integer id;  // 主键ID

    private Integer userId;  // 申请人用户ID
    
    @TableField(exist = false)
    private String userName; // 申请人userName
    
    @TableField(exist = false)
    private String userNickName; // 申请人NickName
    
    @TableField(exist = false)
    private String adminUserName;
    
    @TableField(exist = false)
    private String adminNickName;

    private String name;  // 店铺名称

    private String address;  // 店铺地址

    private String image;  // 店铺图片的URL

    private String status;  // 申请状态（PENDING, APPROVED, REJECTED）

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime applyTime;  // 申请时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approveTime;  // 审批时间

    private Integer adminUserId;  // 审批管理员的用户ID

    private String comment;  // 审批备注
}
