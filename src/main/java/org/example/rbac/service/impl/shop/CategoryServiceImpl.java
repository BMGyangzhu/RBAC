package org.example.rbac.service.impl.shop;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.rbac.domain.shop.Category;
import org.example.rbac.mapper.shop.CategoryMapper;
import org.example.rbac.service.shop.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author bgmyangzhu
 * @date 2025/2/2 20:27
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> listCategoriesByShopId(Long shopId) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getShopId, shopId);
        List<Category> categories = list(queryWrapper);
        return categories;
    }
    
    @Override
    public List<Category> listCategoriesByShopIdForWx(Long shopId) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getShopId, shopId)
                .eq(Category::getStatus,1);
        List<Category> categories = list(queryWrapper);
        return categories;
    }
    
}
