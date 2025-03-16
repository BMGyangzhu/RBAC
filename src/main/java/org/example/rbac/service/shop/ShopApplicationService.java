package org.example.rbac.service.shop;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.rbac.domain.shop.ShopApplication;

import java.util.List;

/**
 * @author bgmyangzhu
 * @date 2025/2/22 16:51
 */
public interface ShopApplicationService extends IService<ShopApplication> {
    boolean applyForShop(ShopApplication shopApplication);
    
    boolean checkApplication(Integer userId);

    boolean checkRejectedApplication(Integer userId);

    Long checkPendingApplication();

    
}
