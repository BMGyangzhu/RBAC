package org.example.rbac.service.impl.shop;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.rbac.constant.Constants;
import org.example.rbac.domain.Role;
import org.example.rbac.domain.User;
import org.example.rbac.domain.shop.*;
import org.example.rbac.service.RoleService;
import org.example.rbac.service.UserService;
import org.example.rbac.service.shop.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author bgmyangzhu
 * @date 2025/2/4 14:59
 */
@Service
public class ShopBusinessLogicServiceImpl implements ShopBusinessLogicService {

    @Autowired
    CategoryService categoryService;
    @Autowired
    DishService dishService;
    @Autowired
    ShopService shopService;
    @Autowired
    UserService userService;
    @Autowired
    ShopApplicationService shopApplicationService;
    @Autowired
    DishFlavorService dishFlavorService;
    @Autowired
    RoleService roleService;

    @Override
    public List<Dish> listDishes(List<Dish> dishes) {
// 获取所有分类信息
        List<Category> categories = categoryService.list();

// 将分类信息转换为 Map，key 为 categoryId，value 为 categoryName
        Map<Long, String> categoryMap = categories.stream()
                                                  .collect(Collectors.toMap(Category::getId, Category::getName));

        Map<Long, List<DishFlavor>> flavorsMap = dishFlavorService.list()
                                                              .stream()
                                                              .collect(Collectors.groupingBy(DishFlavor::getDishId));
        dishes.forEach(dish -> {
            dish.setFlavors(flavorsMap.get(dish.getId()));
            dish.setCategoryName(categoryMap.get(dish.getCategoryId()));
        });


        return dishes;

    }

    @Override
    @Transactional
    public void deleteCategoryById(Long categoryId) {

        LambdaUpdateWrapper<Category> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Category::getId, categoryId)
                     .set(Category::getDeleted, 1)
                     .set(Category::getDeletedTime, System.currentTimeMillis());
        categoryService.update(updateWrapper);

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId, categoryId);
        List<Long> dishIds = dishService.list(queryWrapper)
                                        .stream()
                                        .map(Dish::getId)
                                        .collect(Collectors.toList());
        if (!dishIds.isEmpty()) {
            dishService.deleteBatch(dishIds);
        }
    }

    @Override
    public IPage<Shop> shopIPage(Integer pageNum,
                                 Integer pageSize,
                                 String name,
                                 String username,
                                 String address) {
        IPage<Shop> page = Page.of(pageNum, pageSize);
        LambdaQueryWrapper<Shop> lambdaQueryWrapper = Wrappers.lambdaQuery(Shop.class);

        User user = null;
        // 根据用户名获取UserId
        LambdaQueryWrapper<User> userLambdaQueryWrapper = Wrappers.lambdaQuery(User.class);
        if (!Objects.equals(username, "")) {
            userLambdaQueryWrapper.eq(User::getUsername, username);
            user = userService.getOne(userLambdaQueryWrapper);
            if (user == null) {
                return null;
            }
            //如果用户存在，根据userId筛选shop
            lambdaQueryWrapper.eq(Shop::getUserId, user.getId());
        }


        if (!StringUtils.isEmpty(address)) {
            lambdaQueryWrapper.like(Shop::getAddress, address);
        }

        if (!StringUtils.isEmpty(name)) {
            lambdaQueryWrapper.like(Shop::getName, name);
        }

        // 排序
        lambdaQueryWrapper.orderByAsc(Shop::getId);

        // 查询 Shop 列表
        IPage<Shop> iPage = shopService.page(page, lambdaQueryWrapper);

        // 获取所有 userId 列表
        List<Shop> shops = iPage.getRecords();

        if (shops.isEmpty()) {
            return iPage;
        }
        Set<Integer> userIds = shops.stream()
                                    .map(Shop::getUserId)
                                    .collect(Collectors.toSet());

        // 批量查询所有对应的 User
        LambdaQueryWrapper<User> userQueryWrapper = Wrappers.lambdaQuery(User.class);
        userQueryWrapper.in(User::getId, userIds);
        List<User> users = userService.list(userQueryWrapper);

        // 将 User 列表转换为 Map, key 为 userId, value 为 username
        Map<Integer, String> userMap = users.stream()
                                            .collect(Collectors.toMap(User::getId, User::getUsername));

        // 为每个 Shop 设置 username
        for (Shop shop : shops) {
            String usernameFromMap = userMap.get(shop.getUserId());
            if (usernameFromMap != null) {
                shop.setUsername(usernameFromMap);
            }
        }
        
        return iPage;
    }

    @Override
    public List<ShopApplication> listApplicationInfo() {
        List<User> users = userService.list();

        Map<Integer, String> userNameMap = users.stream()
                                                .collect(Collectors.toMap(User::getId, User::getUsername));

        Map<Integer, String> userNicknameMap = users.stream()
                                                    .collect(Collectors.toMap(User::getId, User::getNickname));

        List<ShopApplication> applications = shopApplicationService.list();

        applications.forEach(application -> {
            Integer userId = application.getUserId();
            Integer adminUserId = application.getAdminUserId();
            String userName = userNameMap.get(userId);
            String userNickName = userNicknameMap.get(userId);
            String adminUserName = userNameMap.get(adminUserId);
            String adminNickName = userNicknameMap.get(adminUserId);
            application.setUserName(userName);
            application.setUserNickName(userNickName);
            application.setAdminUserName(adminUserName);
            application.setAdminNickName(adminNickName);
        });

        return applications;

    }
    
    @Override
    @Transactional
    public boolean approveApplication(Integer id, Integer userId, String status, String comment, Integer adminUserId) {
        ShopApplication shopApplication = shopApplicationService.getById(id);
        shopApplication.setStatus(status);
        shopApplication.setComment(comment);
        shopApplication.setAdminUserId(adminUserId);
        shopApplication.setApproveTime(LocalDateTime.now());
        
        
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getName, Constants.BUSINESSMEN);
        queryWrapper.eq(Role::getComment, Constants.BUSINESSMEN_COMMENT);
        Role role = roleService.getOne(queryWrapper);
        roleService.saveUserRole(userId,role.getId());

        return shopApplicationService.updateById(shopApplication);

    }

    @Override
    public boolean rejectApplication(Integer id, Integer userId, String status, String comment, Integer adminUserId) {
        ShopApplication shopApplication = shopApplicationService.getById(id);
        shopApplication.setStatus(status);
        shopApplication.setComment(comment);
        shopApplication.setAdminUserId(adminUserId);
        shopApplication.setApproveTime(LocalDateTime.now());
        
        
        
        return shopApplicationService.updateById(shopApplication);
    }

    @Override
    public List<Category> listCategoriesByUserId(Integer userId) {
        LambdaQueryWrapper<Shop> shopQueryWrapper = new LambdaQueryWrapper<>();
        shopQueryWrapper.eq(Shop::getUserId,userId);
        Shop shop = shopService.getOne(shopQueryWrapper);
        List<Category> categories = categoryService.listCategoriesByShopId(shop.getId());
        return categories;
    }
    
    @Override
    public List<Dish> listByCategoryIdForWx(Long categoryId){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId,categoryId);
        queryWrapper.eq(Dish::getStatus, 1);
        List<Dish> dishes = dishService.list(queryWrapper);
        
        Map<Long, List<DishFlavor>> flavorsMap = dishFlavorService.list()
                .stream().collect(Collectors.groupingBy(DishFlavor::getDishId));

        dishes.forEach(dish -> {
            dish.setFlavors(flavorsMap.get(dish.getId()));
        });
        
        return dishes;
        
    }
}
