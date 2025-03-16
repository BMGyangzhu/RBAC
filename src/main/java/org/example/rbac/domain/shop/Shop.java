package org.example.rbac.domain.shop;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

/**
 * @author bgmyangzhu
 * @date 2025/2/2 20:25
 */
@Data
public class Shop {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String image;
    
    @TableLogic
    private Integer deleted;
    
    private Long deletedTime;
    
    private Integer userId;
    
    @TableField(exist = false)
    private String username;
    
    private String address;
    
    private String phone;
    
    private Integer status;
}
