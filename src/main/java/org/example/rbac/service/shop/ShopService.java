package org.example.rbac.service.shop;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.rbac.domain.shop.Shop;

/**
 * @author bgmyangzhu
 * @date 2025/2/2 20:27
 */
public interface ShopService extends IService<Shop> {
    
    Shop getShopByUserId(Integer userId);
    
    Long getShopIdByUserId(Integer userId);

    void toggleStatus(Long shopId);
}
