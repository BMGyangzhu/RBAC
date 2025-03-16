package org.example.rbac.domain.shop;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.util.List;

/**
 * @author bgmyangzhu
 * @date 2025/2/2 20:18
 */
@Data
public class Dish {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private Long categoryId;
    
    @TableField(exist = false)
    private String categoryName;
    
    private Double price;
    
    private String image;
    
    private String description;
    
    private Integer status;
    
    private Integer shopId;
    
    @TableField(exist = false)
    private List<DishFlavor> flavors;

    @TableLogic
    private Integer deleted;
    
    private Long deletedTime;
    
}
