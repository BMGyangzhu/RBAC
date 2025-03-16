package org.example.rbac.service.shop;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.rbac.domain.dto.DishDTO;
import org.example.rbac.domain.shop.Dish;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author bgmyangzhu
 * @date 2025/2/2 20:27
 */
public interface DishService extends IService<Dish> {
    void deleteBatch(List<Long> dishIds);

    List<Dish> listWithFlavors(Long categoryId);

    @Transactional
    void saveWithFlavor(DishDTO dishDTO);
}
