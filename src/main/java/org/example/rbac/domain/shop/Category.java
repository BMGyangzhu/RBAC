package org.example.rbac.domain.shop;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

/**
 * @author bgmyangzhu
 * @date 2025/2/2 20:22
 */
@Data
public class Category {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private Integer sort;
    
    private Integer status;
    
    private Long shopId;
    
    @TableLogic
    private Integer deleted;
    
    private Long deletedTime;
}
