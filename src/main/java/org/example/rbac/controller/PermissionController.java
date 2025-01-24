package org.example.rbac.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.rbac.domain.Permission;
import org.example.rbac.domain.vo.UserVO;
import org.example.rbac.service.PermissionService;
import org.example.rbac.service.RbacService;
import org.example.rbac.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author bgmyangzhu
 * @date 2025/1/7 14:35
 */
@RestController
@CrossOrigin
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    PermissionService permissionService;
    
    @Autowired
    RbacService rbacService;

    @PostMapping("/saveOrUpdate")
    public Result save(@RequestBody Permission permission) {
            LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Permission::getComment, permission.getComment());
            Permission one = permissionService.getOne(queryWrapper);
            if(one != null && !Objects.equals(permission.getId(), one.getId())){
                String errorMessage = "该项 '" + permission.getComment() + "' 已存在，请选择其他名称";
                return Result.error("409",errorMessage);
            }
            permissionService.saveOrUpdate(permission);
            rbacService.AddPermissionForAdministrators(permission.getId());
            return Result.success();
    }


    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        permissionService.delete(id);
        return Result.success();
    }
    

    @GetMapping
    public Result findAll() {
        Set<Permission> permissionSet = permissionService.list()
                                                         .stream()
                                                         .filter(permission -> permission.getParentId() == 0)
                                                         .collect(Collectors.toSet());
        // 列表转换成树状结构
        Set<Permission> permissions = permissionService.buildAllPermissionTree(permissionSet);
        return Result.success(permissions);
    }
    
    @GetMapping("/{username}")
    public Result reload(@PathVariable String username){
        UserVO userVO = rbacService.reload(username);
        return Result.success(userVO);
    }
    
    
    
}
