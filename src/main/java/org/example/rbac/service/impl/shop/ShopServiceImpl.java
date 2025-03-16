package org.example.rbac.service.impl.shop;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.rbac.domain.shop.Shop;
import org.example.rbac.mapper.shop.ShopMapper;
import org.example.rbac.service.shop.ShopService;
import org.springframework.stereotype.Service;

/**
 * @author bgmyangzhu
 * @date 2025/2/2 20:28
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements ShopService {
    @Override
    public Shop getShopByUserId(Integer userId) {
        LambdaQueryWrapper<Shop> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Shop::getUserId,userId);
        Shop shop = getOne(queryWrapper);
        return shop;
    }

    @Override
    public Long getShopIdByUserId(Integer userId) {
        LambdaQueryWrapper<Shop> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Shop::getUserId,userId);
        Shop shop = getOne(queryWrapper);
        Long shopId = shop.getId();
        return shopId;
    }

    @Override
    public void toggleStatus(Long shopId) {
        // 查询当前的 Shop 对象
        Shop shop = getById(shopId);
        if (shop == null) {
            throw new RuntimeException("Shop not found");
        }

        // 切换 status
        shop.setStatus(shop.getStatus() == 1 ? 0 : 1);

        // 更新 Shop 对象
        updateById(shop);
    }
}
