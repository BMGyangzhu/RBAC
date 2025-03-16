package org.example.rbac.service.shop;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.rbac.domain.dto.Order;
import org.example.rbac.domain.dto.ShoppingCartDTO;
import org.example.rbac.domain.shop.ShoppingCart;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author bgmyangzhu
 * @date 2025/2/27 21:21
 */
public interface ShoppingCartService extends IService<ShoppingCart> {


    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    List<ShoppingCart> showShoppingCart();

    void cleanShoppingCart();

    void subShoppingCart(ShoppingCartDTO shoppingCartDTO);

    String pay(ShoppingCartDTO shoppingCartDTO);

//    Order getOrderDetail(String orderId);

    Order getOrderByWxUserId(Long wxUserId);

    Order listOrderByShopId(Long shopId);

    @Transactional
    void setHistory(Long dishId);

    boolean checkStatus(ShoppingCartDTO shoppingCartDTO);
}
