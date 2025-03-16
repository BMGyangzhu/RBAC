package org.example.rbac.service.impl.shop;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.rbac.constant.Constants;
import org.example.rbac.domain.shop.ShopApplication;
import org.example.rbac.mapper.shop.ShopApplicationMapper;
import org.example.rbac.service.shop.ShopApplicationService;
import org.springframework.stereotype.Service;

/**
 * @author bgmyangzhu
 * @date 2025/2/22 16:51
 */
@Service
public class ShopApplicationServiceImpl extends ServiceImpl<ShopApplicationMapper, ShopApplication> implements ShopApplicationService {
    
//    public boolean approveApplication(Integer id, String status, String comment, Integer adminUserId) {
//        ShopApplication application = getById(id);
//        if (application != null && "PENDING".equals(application.getStatus())) {
//            application.setStatus(status);
//            application.setApproveTime(LocalDateTime.now());
//            application.setAdminUserId(adminUserId);
//            application.setComment(comment);
//            return updateById(application);
//        }
//        return false;
//    }
    
    @Override
    public boolean applyForShop(ShopApplication shopApplication){
        return save(shopApplication);
    }

    @Override
    public boolean checkApplication(Integer userId) {
        LambdaQueryWrapper<ShopApplication> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShopApplication::getUserId, userId);
        lambdaQueryWrapper.eq(ShopApplication::getStatus, Constants.PENDING);
        ShopApplication one = getOne(lambdaQueryWrapper);
        
        return one == null ? false : true;
    }
    
    @Override
    public boolean checkRejectedApplication(Integer userId){
        LambdaQueryWrapper<ShopApplication> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShopApplication::getUserId, userId);
        lambdaQueryWrapper.eq(ShopApplication::getStatus, Constants.REJECTED);
        ShopApplication one = getOne(lambdaQueryWrapper);
        
        return one == null ? false : true;
    }

    @Override
    public Long checkPendingApplication() {
        LambdaQueryWrapper<ShopApplication> lambdaQueryWrapper =  new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShopApplication::getStatus, Constants.PENDING);
        Long count = count(lambdaQueryWrapper);
        return count;
    }

   
}
