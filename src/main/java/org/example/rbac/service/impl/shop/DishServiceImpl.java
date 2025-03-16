package org.example.rbac.service.impl.shop;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.rbac.domain.dto.DishDTO;
import org.example.rbac.domain.shop.Dish;
import org.example.rbac.domain.shop.DishFlavor;
import org.example.rbac.mapper.shop.DishMapper;
import org.example.rbac.service.shop.DishFlavorService;
import org.example.rbac.service.shop.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author bgmyangzhu
 * @date 2025/2/2 20:27
 */
@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    DishMapper dishMapper;

    @Autowired
    DishFlavorService dishFlavorService;

    @Override
    public void deleteBatch(List<Long> dishIds) {
        List<Dish> dishes = listByIds(dishIds);
        dishes.forEach(dish -> {
            dish.setDeleted(1);
            dish.setDeletedTime(System.currentTimeMillis());
        });
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Dish::getId, dishIds)
                     .set(Dish::getDeleted, 1)
                     .set(Dish::getDeletedTime, System.currentTimeMillis());
        update(updateWrapper);
    }

    @Override
    public List<Dish> listWithFlavors(Long categoryId) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId, categoryId);
        List<Dish> dishes = list(queryWrapper);

        Map<Long, List<DishFlavor>> flavorsMap = dishFlavorService.list()
                                                              .stream()
                                                              .collect(Collectors.groupingBy(DishFlavor::getDishId));
        dishes.forEach(dish -> {
            dish.setFlavors(flavorsMap.get(dish.getId()));
        });
        return dishes;

    }

    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        log.info("saveWithFlavor: {}", dishDTO);
        Dish dish = BeanUtil.copyProperties(dishDTO, Dish.class);
        save(dish);

        Long dishId = dish.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            //  向口味表插入n条数据

            dishFlavorService.saveBatch(flavors);
        }

    }

}
