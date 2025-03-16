package org.example.rbac.controller.shop;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.rbac.domain.User;
import org.example.rbac.domain.dto.DishDTO;
import org.example.rbac.domain.shop.Category;
import org.example.rbac.domain.shop.Dish;
import org.example.rbac.service.impl.shop.DishServiceImpl;
import org.example.rbac.service.impl.shop.ShopBusinessLogicServiceImpl;
import org.example.rbac.service.shop.DishService;
import org.example.rbac.service.shop.ShopBusinessLogicService;
import org.example.rbac.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author bgmyangzhu
 * @date 2025/2/2 20:32
 */
@RestController
@RequestMapping("/dish")
@CrossOrigin
@Slf4j
public class DishController {

    @Autowired
    DishService dishService;

    @Autowired
    ShopBusinessLogicService shopBusinessLogicService;

    @GetMapping("/{categoryId}")
    public Result listByCategoryId(@PathVariable Long categoryId) {
        List<Dish> dishes = dishService.listWithFlavors(categoryId);
        return Result.success(dishes);
    }


    @GetMapping("/wx/{categoryId}")
    public Result listByCategoryIdForWx(@PathVariable Long categoryId) {
        List<Dish> dishes = shopBusinessLogicService.listByCategoryIdForWx(categoryId);
        return Result.success(dishes);
    }

    @PostMapping("/add")
    public Result addDish(@RequestBody DishDTO dishDTO) {
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }

    @DeleteMapping("/{dishId}")
    public Result deletedDishByDishId(@PathVariable Long dishId) {
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .eq(Dish::getId, dishId)
                .set(Dish::getDeleted, 1)
                .set(Dish::getDeletedTime, System.currentTimeMillis());
        dishService.update(updateWrapper);
        return Result.success();
    }
    @PostMapping("del/batch")
    public Result deleteBatch(@RequestBody List<Long> dishIds) {
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .in(Dish::getId, dishIds)
                .set(Dish::getDeleted,1)
                .set(Dish::getDeletedTime, System.currentTimeMillis());
        dishService.update(updateWrapper);
        return Result.success();
    }
    

    @PostMapping("/status/{dishId}")
    public Result editStatus(@PathVariable Long dishId, @RequestParam Integer status) {
        Dish dish = dishService.getById(dishId);
        dish.setStatus(status);
        dishService.saveOrUpdate(dish);
        return Result.success();
    }

    @PostMapping("/edit")
    public Result editDish(@RequestBody Dish dish) {
        dishService.saveOrUpdate(dish);
        return Result.success();
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam Long shopId) {
        log.info("Received pageNum: {}, pageSize: {}, shopId: {}", pageNum, pageSize, shopId);
        IPage<Dish> page = Page.of(pageNum, pageSize);
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = Wrappers.lambdaQuery(Dish.class);
        lambdaQueryWrapper.eq(Dish::getShopId, shopId);
        lambdaQueryWrapper.orderByAsc(Dish::getId);
        IPage<Dish> iPage = dishService.page(page, lambdaQueryWrapper);
        List<Dish> records = iPage.getRecords();
        List<Dish> dishes = shopBusinessLogicService.listDishes(records);
        iPage.setRecords(dishes);
        return Result.success(iPage);
    }


}
