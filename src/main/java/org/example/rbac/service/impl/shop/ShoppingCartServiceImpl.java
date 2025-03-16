package org.example.rbac.service.impl.shop;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.rbac.domain.dto.HistoryOrderItem;
import org.example.rbac.domain.dto.Order;
import org.example.rbac.domain.dto.OrderItem;
import org.example.rbac.domain.dto.ShoppingCartDTO;
import org.example.rbac.domain.shop.Category;
import org.example.rbac.domain.shop.Dish;
import org.example.rbac.domain.shop.Shop;
import org.example.rbac.domain.shop.ShoppingCart;
import org.example.rbac.holder.UserHolder;
import org.example.rbac.mapper.shop.ShoppingCartMapper;
import org.example.rbac.service.shop.CategoryService;
import org.example.rbac.service.shop.DishService;
import org.example.rbac.service.shop.ShopService;
import org.example.rbac.service.shop.ShoppingCartService;
import org.example.rbac.websocket.OrderWebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author bgmyangzhu
 * @date 2025/2/27 21:25
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

    @Autowired
    private DishService dishService;
    
    @Autowired
    private OrderWebSocketServer orderWebSocketServer;
    
    @Autowired
    private ShopService shopService;
    
    @Autowired
    private CategoryService categoryService;
    
    
    /**
     * 添加商品到购物车
     *
     * @param shoppingCartDTO
     */
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {

        // 判断商品是否已存在
        ShoppingCart shoppingCart = BeanUtil.copyProperties(shoppingCartDTO, ShoppingCart.class);
        Long wxUserId = UserHolder.get();
        shoppingCart.setId(wxUserId);

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getWxUserId, wxUserId)
                    .eq(ShoppingCart::getDishId, shoppingCartDTO.getDishId())
                    .eq(ShoppingCart::getDishFlavor, shoppingCartDTO.getDishFlavor());
        List<ShoppingCart> list = list(queryWrapper);
        
        // 已存在，则数量加一
        if ( list != null && list.size() > 0){
            // 取出第一条数据，即取出唯一的一条数据
           ShoppingCart cart = list.get(0);
           cart.setNumber(cart.getNumber() + 1);
           updateById(cart);
        } else {
            // 不存在
            Long dishId = shoppingCart.getDishId();
            Dish dish = dishService.getById(dishId);
            shoppingCart.setName(dish.getName());
            shoppingCart.setImage(dish.getImage());
            shoppingCart.setAmount(dish.getPrice());
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            save(shoppingCart);
        } 
        
    }

    /**
     * 查看购物车
     * @return
     */
    @Override
    public List<ShoppingCart> showShoppingCart() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getId,UserHolder.get());
        List<ShoppingCart> list = list(queryWrapper);
        return list;
    }

    /**
     * 清空购物车
     */
    @Override
    public void cleanShoppingCart() {
        removeById(UserHolder.get());
    }

    /**
     * 删除购物车中的一个
     */
    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = BeanUtil.copyProperties(shoppingCartDTO, ShoppingCart.class);
        
        // 设置查询条件，查询当前登录用户的购物车数据
        shoppingCart.setWxUserId(UserHolder.get());

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getWxUserId, UserHolder.get())
                    .eq(ShoppingCart::getDishId, shoppingCartDTO.getDishId())
                    .eq(ShoppingCart::getDishFlavor, shoppingCartDTO.getDishFlavor());
        List<ShoppingCart> list = list(queryWrapper);
        if (list != null && list.size() > 0) {
            shoppingCart = list.get(0);
            
            Integer number = shoppingCart.getNumber();
            if (number == 1) {
                // 当前商品在购物车中份数为1，直接删除当前记录
                removeById(shoppingCart.getId());
            } else {
                shoppingCart.setNumber(shoppingCart.getNumber() - 1);
                updateById(shoppingCart);
            }
        }
    }

    @Override
    @Transactional
    public String pay(ShoppingCartDTO shoppingCartDTO) {
        List<ShoppingCart> shoppingCart = shoppingCartDTO.getShoppingCart();
        
        String orderId =  UUID.randomUUID().toString();
        Long shopId = shoppingCart.get(0).getShopId();
        
        shoppingCart.stream().forEach(item -> {
            item.setWxUserId(shoppingCartDTO.getWxUserId());
            item.setOrderId(orderId);
        });
        orderWebSocketServer.sendOrderAlert(shopId, "true");
        saveBatch(shoppingCart);
        
        return orderId;
    }
    
    public double calculateTotalPrice(List<ShoppingCart> list) {
        if (list == null || list.isEmpty()) {
            return 0.0;
        }

        double totalPrice = list.stream()
                                .mapToDouble(ShoppingCart::getAmount)
                                .sum();

        return totalPrice;
    }

    public boolean checkStatus(ShoppingCartDTO shoppingCartDTO) {

        Long  shopId = shoppingCartDTO.getShoppingCart().get(0).getShopId();
        List<Long> dishIds = shoppingCartDTO.getShoppingCart().stream()
                                            .map(ShoppingCart::getDishId).collect(Collectors.toList());
        
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getShopId,shopId)
                .in(Dish::getId, dishIds);
                
        List<Dish> list = dishService.list(queryWrapper);
        // 检查购物车中所有项的 status 是否都不为 0
        boolean allItemsValid = list.stream()
                                            .noneMatch(item -> item.getStatus() == 0);
        
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper  = new LambdaQueryWrapper<>();
        categoryLambdaQueryWrapper.eq(Category::getShopId,shopId);
        List<Category> categoryList = categoryService.list(categoryLambdaQueryWrapper);
        boolean allCategoryItemsValid = categoryList.stream()
                .noneMatch(item -> item.getStatus() == 0);

        if (!allItemsValid || !allCategoryItemsValid) {
            // 如果有任何一个商品的 status 为 0，直接返回 false
            return false;
        }

        Shop shop = shopService.getById(shopId);
        if (shop == null) {
            return false;
        }

        return shop.getStatus() == 1;
    }

    @Override
    public Order getOrderByWxUserId(Long wxUserId) {
        // 查询状态为1的购物车项
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getWxUserId, wxUserId);
        queryWrapper.eq(ShoppingCart::getStatus, 1);
        List<ShoppingCart> shoppingCarts = list(queryWrapper);

        // 查询状态为0的购物车项（历史购物车）
        LambdaQueryWrapper<ShoppingCart> historyQueryWrapper = new LambdaQueryWrapper<>();
        historyQueryWrapper.eq(ShoppingCart::getWxUserId, wxUserId);
        historyQueryWrapper.eq(ShoppingCart::getStatus, 0);
        List<ShoppingCart> historyShoppingCarts = list(historyQueryWrapper);

        // 创建订单对象并设置购物车列表
        Order order = new Order();
//        order.setShoppingCart(shoppingCarts);
//        order.setHistoryShoppingCart(historyShoppingCarts);

      

        List<OrderItem> orderItems = new ArrayList<>();
        List<HistoryOrderItem> historyOrderItems = new ArrayList<>();

        // 处理当前购物车的分组
        if (shoppingCarts != null && !shoppingCarts.isEmpty()) {
            Map<String, List<ShoppingCart>> shoppingCartMap = shoppingCarts.stream()
                                                                           .collect(Collectors.groupingBy(ShoppingCart::getOrderId));

            for (Map.Entry<String, List<ShoppingCart>> entry : shoppingCartMap.entrySet()) {
                String orderId = entry.getKey();
                List<ShoppingCart> cartList = entry.getValue();

                // 同一个orderId下的所有ShoppingCart的shopId相同，取第一个获取shopId
                Long shopId = cartList.get(0).getShopId();

                // 查询Shop对象
                Shop shop = shopService.getById(shopId); // 有shopService用于查询Shop
                if (shop != null) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setShop(shop);
                    orderItem.setShoppingCart(cartList);
                    orderItems.add(orderItem);
                }
            }
        }

        // 处理历史购物车的分组
        if (historyShoppingCarts != null && !historyShoppingCarts.isEmpty()) {
            Map<String, List<ShoppingCart>> historyShoppingCartMap = historyShoppingCarts.stream()
                                                                                         .collect(Collectors.groupingBy(ShoppingCart::getOrderId));

            for (Map.Entry<String, List<ShoppingCart>> entry : historyShoppingCartMap.entrySet()) {
                String orderId = entry.getKey();
                List<ShoppingCart> cartList = entry.getValue();

                // 同一个orderId下的所有ShoppingCart的shopId相同，取第一个获取shopId
                Long shopId = cartList.get(0).getShopId();

                // 查询Shop对象
                Shop shop = shopService.getById(shopId); // shopService用于查询Shop
                if (shop != null) {
                    HistoryOrderItem historyOrderItem = new HistoryOrderItem();
                    historyOrderItem.setShop(shop);
                    historyOrderItem.setShoppingCart(cartList);
                    historyOrderItems.add(historyOrderItem);
                }
            }
        }

        // 将所有的OrderItem设置到Order对象中
        order.setOrderItems(orderItems);
        order.setHistoryOrderItems(historyOrderItems);

        return order;
    }
    
    @Override
    public Order listOrderByShopId(Long shopId){
        // 查询状态为1的项
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getShopId, shopId);
        queryWrapper.eq(ShoppingCart::getStatus, 1);
        List<ShoppingCart> shoppingCarts = list(queryWrapper);

        // 查询状态为0的项（历史订单）
        LambdaQueryWrapper<ShoppingCart> historyQueryWrapper = new LambdaQueryWrapper<>();
        historyQueryWrapper.eq(ShoppingCart::getShopId, shopId);
        historyQueryWrapper.eq(ShoppingCart::getStatus, 0);
        List<ShoppingCart> historyShoppingCarts = list(historyQueryWrapper);

        // 创建订单对象并设置订单列表
        Order order = new Order();
        List<OrderItem> orderItems = new ArrayList<>();
        List<HistoryOrderItem> historyOrderItems = new ArrayList<>();

        // 处理当前订单的分组
        if (shoppingCarts != null && !shoppingCarts.isEmpty()) {
            Map<String, List<ShoppingCart>> shoppingCartMap = shoppingCarts.stream()
                                                                           .collect(Collectors.groupingBy(ShoppingCart::getOrderId));

            for (Map.Entry<String, List<ShoppingCart>> entry : shoppingCartMap.entrySet()) {
                String orderId = entry.getKey();
                List<ShoppingCart> cartList = entry.getValue();

                // 同一个orderId下的所有ShoppingCart的wxUserId相同，取第一个获取wxUserId
                Long wxUserId = cartList.get(0).getWxUserId();
                    OrderItem orderItem = new OrderItem();
                    orderItem.setShoppingCart(cartList);
                    orderItem.setWxUserId(wxUserId);
                    orderItems.add(orderItem);
                }
            }

        // 处理历史购物车的分组
        if (historyShoppingCarts != null && !historyShoppingCarts.isEmpty()) {
            Map<String, List<ShoppingCart>> historyShoppingCartMap = historyShoppingCarts.stream()
                                                                                         .collect(Collectors.groupingBy(ShoppingCart::getOrderId));

            for (Map.Entry<String, List<ShoppingCart>> entry : historyShoppingCartMap.entrySet()) {
                String orderId = entry.getKey();
                List<ShoppingCart> cartList = entry.getValue();

                // 同一个orderId下的所有ShoppingCart的wxUserId相同，取第一个获取wxUserId
                Long wxUserId = cartList.get(0).getWxUserId();
                    HistoryOrderItem historyOrderItem = new HistoryOrderItem();
                    historyOrderItem.setShoppingCart(cartList);
                    historyOrderItem.setWxUserId(wxUserId);
                    historyOrderItems.add(historyOrderItem);
            }
        }

        // 将所有的OrderItem设置到Order对象中
        order.setOrderItems(orderItems);
        order.setHistoryOrderItems(historyOrderItems);

        return order;
        
    }
    
    @Override
    @Transactional
    public void setHistory(Long id){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getId,id);
        ShoppingCart one = getOne(queryWrapper);
        LambdaQueryWrapper<ShoppingCart> query = new LambdaQueryWrapper<>();
        query.eq(ShoppingCart::getOrderId,one.getOrderId());
        queryWrapper.eq(ShoppingCart::getOrderId,one.getOrderId());
        List<ShoppingCart> list = list(query);
        list.forEach( item -> item.setStatus(0));
        saveOrUpdateBatch(list);
    }

  
    
}
