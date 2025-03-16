package org.example.rbac.controller.shop;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.rbac.domain.shop.Category;
import org.example.rbac.service.shop.CategoryService;
import org.example.rbac.service.shop.ShopBusinessLogicService;
import org.example.rbac.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author bgmyangzhu
 * @date 2025/2/2 20:33
 */
@RestController
@RequestMapping("/category")
@CrossOrigin
public class CategoryController {
    
    @Autowired
    CategoryService categoryService;
    
    @Autowired
    ShopBusinessLogicService shopBusinessLogicService;
    

    @PostMapping("/add")
    public Result addCategory(@RequestBody Category category){
        categoryService.saveOrUpdate(category);
        return Result.success();
    }
    
    @PostMapping
    public Result editCategory(@RequestBody Category category){
        categoryService.saveOrUpdate(category);
        return Result.success();
    }
    
    @GetMapping("/listAll/{userId}")
    public Result listByUserId(@PathVariable Integer userId){
        
        List<Category> categories = shopBusinessLogicService.listCategoriesByUserId(userId);
        return Result.success(categories);
    }
    
    @PostMapping("/status/{categoryId}")
    public Result editStatus(@PathVariable Long categoryId,@RequestParam Integer status){
        Category category = categoryService.getById(categoryId);
        category.setStatus(status);
        categoryService.saveOrUpdate(category);
        return Result.success();
    }
    
    @DeleteMapping("/{categoryId}")
    public Result deletedCategory(@PathVariable Long categoryId){
        shopBusinessLogicService.deleteCategoryById(categoryId);
        return Result.success();
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam Long shopId) {
        IPage<Category> page = Page.of(pageNum,pageSize);
        LambdaQueryWrapper<Category> lambdaQueryWrapper = Wrappers.lambdaQuery(Category.class);
//        if(!"".equals(name)){
//            lambdaQueryWrapper.like(Category::getName,name);
//        }
        lambdaQueryWrapper.orderByAsc(Category::getId);
        IPage<Category> iPage = categoryService.page(page, lambdaQueryWrapper);
        List<Category> categories = categoryService.listCategoriesByShopId(shopId);
        iPage.setRecords(categories);
        return Result.success(iPage);
    }
    
    @GetMapping("/{shopId}")
    public Result getByShopId(@PathVariable Long shopId) {
        List<Category> categories = categoryService.listCategoriesByShopId(shopId);
        return Result.success(categories);
    }

    @GetMapping("/wx/{shopId}")
    public Result getByShopIdForWx(@PathVariable Long shopId) {
        List<Category> categories = categoryService.listCategoriesByShopIdForWx(shopId);
        return Result.success(categories);
    }
    
}
