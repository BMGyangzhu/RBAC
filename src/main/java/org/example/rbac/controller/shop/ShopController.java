package org.example.rbac.controller.shop;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.rbac.domain.User;
import org.example.rbac.domain.shop.Shop;
import org.example.rbac.service.shop.ShopBusinessLogicService;
import org.example.rbac.service.shop.ShopService;
import org.example.rbac.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author bgmyangzhu
 * @date 2025/2/2 20:33
 */
@RestController
@RequestMapping("/shop")
@CrossOrigin
public class ShopController {
    
    @Autowired
    ShopService shopService;
    
    @Autowired
    ShopBusinessLogicService shopBusinessLogicService;
    
    
    @GetMapping("/{userId}")
    public Result getShopByUserId(@PathVariable Integer userId){
        Shop shop = shopService.getShopByUserId(userId);
        return Result.success(shop);
    }

//    @GetMapping("/wx/{userId}")
//    public Result getShopByUserIdForWx(@PathVariable Integer userId){
//        Shop shop = shopService.getShopByUserId(userId);
//        return Result.success(shop);
//    }
    
    
    @GetMapping("/getShopId/{userId}")
    public Result getShopIdByUserId(@PathVariable Integer userId){
        Long shopId = shopService.getShopIdByUserId(userId);
        return Result.success(shopId);
    }
    
    @GetMapping("/status/{shopId}")
    public Result toggleStatus(@PathVariable Long shopId) {
        shopService.toggleStatus(shopId);
        return Result.success();
    }
    
    @GetMapping("/wx/{shopId}")
    public Result getShopById(@PathVariable Long shopId){
        Shop shop = shopService.getById(shopId);
        return Result.success(shop);
    }
    
    @GetMapping
    public Result listShops(){
        List<Shop> shopList = shopService.list();
        return Result.success(shopList);
    }
    
    @GetMapping("/wx")
    public Result listShopsForWx(){
        List<Shop> shopList = shopService.list();
        return Result.success(shopList);
    }


    @PostMapping("/add")
    public Result addShop(@RequestBody Shop shop){
        shopService.save(shop);
        return Result.success();
    }
    
    @PostMapping("/edit")
    public Result editShop(@RequestBody Shop shop){
        shopService.saveOrUpdate(shop);
        return Result.success();
    }
    
    @GetMapping("/shops/page")
    public Result page(@RequestParam Integer pageNum,
                       @RequestParam Integer pageSize,
                       @RequestParam(defaultValue = "") String name,
                       @RequestParam(defaultValue = "") String username,
                       @RequestParam(defaultValue = "") String address) {
        IPage<Shop> iPage = shopBusinessLogicService.shopIPage(pageNum, pageSize, name, username, address);
        return Result.success(iPage);
    }
    
    
    
    
}
