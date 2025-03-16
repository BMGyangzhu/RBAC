package org.example.rbac.domain.dto;

import lombok.Data;
import org.example.rbac.domain.shop.Shop;
import org.example.rbac.domain.shop.ShoppingCart;

import java.util.HashMap;
import java.util.List;

/**
 * @author bgmyangzhu
 * @date 2025/3/12 2:02
 */
@Data
public class HistoryOrderItem {
    
    private Shop shop;
    
    private Long wxUserId;

    private List<ShoppingCart> shoppingCart;
}
