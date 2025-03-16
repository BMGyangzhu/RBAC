package org.example.rbac.domain.dto;

import lombok.Data;
import org.example.rbac.domain.shop.Shop;
import org.example.rbac.domain.shop.ShoppingCart;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

/**
 * @author bgmyangzhu
 * @date 2025/3/11 20:56
 */
@Data
public class Order {
    
//    private HashMap<String, List<ShoppingCart>> shoppingCart;
    
    private List<OrderItem> orderItems;
    
    private List<HistoryOrderItem> historyOrderItems;
    
//    private Double totalPrice;
    
//    private String orderCode;
    
//    private Long shopId;
    
//    private Shop shop;
    
//    private LocalDateTime payedTime;
    
//    private HashMap<String, List<ShoppingCart>> historyShoppingCart;
    
}
