package org.example.rbac.controller.shop;

import lombok.extern.slf4j.Slf4j;
import org.example.rbac.domain.dto.Order;
import org.example.rbac.domain.dto.OrderResult;
import org.example.rbac.domain.dto.ShoppingCartDTO;
import org.example.rbac.domain.shop.ShoppingCart;
import org.example.rbac.service.shop.ShoppingCartService;
import org.example.rbac.util.Result;
import org.example.rbac.websocket.OrderWebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author bgmyangzhu
 * @date 2025/2/27 21:17
 */
@RestController
@CrossOrigin
@Slf4j
public class ShoppingCartController {

    long orderCode = 2000;
    
    @Autowired
    OrderWebSocketServer orderWebSocketServer;
    
    @Autowired
    private ShoppingCartService shoppingCartService;
    @PostMapping("/wx/shoppingCart/add")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("添加购物车，商品信息: {}", shoppingCartDTO);
        shoppingCartService.addShoppingCart(shoppingCartDTO);
        return Result.success();
    }

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/wx/shoppingCart/list")
    public Result list() {
        return Result.success(shoppingCartService.showShoppingCart());
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/wx/shoppingCart/clean")
    public Result clean() {
        shoppingCartService.cleanShoppingCart();
        return Result.success();
    }

    @PostMapping("/wx/shoppingCart/sub")
    public Result sub(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("删除购物车中的一个, 商品：{}", shoppingCartDTO);
        shoppingCartService.subShoppingCart(shoppingCartDTO);
        return Result.success();
    }
    
    @PostMapping("/wx/shoppingCart/pay")
    public Result pay(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("用户下单: {}",shoppingCartDTO);
       
        boolean result = shoppingCartService.checkStatus(shoppingCartDTO);
        if (!result) return  Result.error("500","菜品下架或店铺打烊");
        shoppingCartService.pay(shoppingCartDTO);
        return Result.success();
    }
    
    @GetMapping("/wx/shoppingCart/order/{wxUserId}")
    public Result getOrdersByWxUserId(@PathVariable Long wxUserId) {
        Order order = shoppingCartService.getOrderByWxUserId(wxUserId);
        return Result.success(order);
    }
    
    @GetMapping("/shoppingCart/order/businessmen/{shopId}")
    public Result listOrder(@PathVariable Long shopId) {
        Order order = shoppingCartService.listOrderByShopId(shopId);
        return Result.success(order);
    }
    
    @GetMapping("/shoppingCart/order/setHistory/{id}")
    public Result setHistory(@PathVariable Long id){
        shoppingCartService.setHistory(id);
        return Result.success();
    }
}
