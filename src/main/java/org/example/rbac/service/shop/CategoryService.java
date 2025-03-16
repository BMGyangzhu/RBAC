package org.example.rbac.service.shop;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.rbac.domain.shop.Category;

import java.util.List;

/**
 * @author bgmyangzhu
 * @date 2025/2/2 20:27
 */
public interface CategoryService extends IService<Category> {
    List<Category> listCategoriesByShopId(Long shopId);

    List<Category> listCategoriesByShopIdForWx(Long shopId);
}
