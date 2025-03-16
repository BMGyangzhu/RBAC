package org.example.rbac.domain.dto;

import lombok.Data;
import org.example.rbac.domain.shop.DishFlavor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bgmyangzhu
 * @date 2025/2/28 14:18
 */
@Data
public class DishDTO implements Serializable {
    
    private Long id;
    
    private String name;
    
    private Long categoryId;
    
    private Long shopId;
    
    private Double price;
    
    private String image;
    
    private String description;
    
    private Integer status;
    
    private List<DishFlavor> flavors = new ArrayList<>();
}
