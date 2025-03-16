package org.example.rbac.service.shop;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.rbac.domain.shop.Category;
import org.example.rbac.domain.shop.Dish;
import org.example.rbac.domain.shop.Shop;
import org.example.rbac.domain.shop.ShopApplication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author bgmyangzhu
 * @date 2025/2/4 15:00
 */
public interface ShopBusinessLogicService {
    List<Dish> listDishes(List<Dish> dishes);

    void deleteCategoryById(Long categoryId);

    IPage<Shop> shopIPage(Integer pageNum,
                          Integer pageSize,
                          String name,
                          String username,
                          String address);

    List<ShopApplication> listApplicationInfo();
    
    @Transactional
    boolean approveApplication(Integer id, Integer userId, String status, String comment, Integer adminUserId);

    boolean rejectApplication(Integer id, Integer userId, String status, String comment, Integer adminUserId);
    
    List<Category> listCategoriesByUserId(Integer userId);

    List<Dish> listByCategoryIdForWx(Long categoryId);
}
