package org.example.rbac.domain.dto;

import lombok.Data;
import org.example.rbac.domain.shop.ShoppingCart;

import java.io.Serializable;
import java.util.List;

/**
 * @author bgmyangzhu
 * @date 2025/2/27 21:16
 */
@Data
public class ShoppingCartDTO implements Serializable {
    
    private Long dishId;
    private String dishFlavor;

    
    private Long shopId;
    
    private Long wxUserId; 
    
    private List<ShoppingCart> shoppingCart;
    
    
    
}
