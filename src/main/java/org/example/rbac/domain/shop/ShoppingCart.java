package org.example.rbac.domain.shop;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author bgmyangzhu
 * @date 2025/2/27 21:21
 */
@Data
public class ShoppingCart {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String image;
    
    private Long wxUserId;
    
    private Long dishId;

    @JsonProperty("flavors")
    private String dishFlavor;
    
    private String orderId;
    
    private Integer number;
    
    private Double amount;
    
    private Integer status;
    
    private Long shopId;
    
    private LocalDateTime createTime;
}
