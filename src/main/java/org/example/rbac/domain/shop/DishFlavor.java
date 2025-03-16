package org.example.rbac.domain.shop;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author bgmyangzhu
 * @date 2025/2/2 21:18
 */
@Data
public class DishFlavor {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long dishId;
    
    private String name;
    
    private String value;
}
