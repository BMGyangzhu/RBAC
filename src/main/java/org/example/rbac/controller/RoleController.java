package org.example.rbac.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.rbac.domain.Role;
import org.example.rbac.domain.RolePermission;
import org.example.rbac.service.RbacService;
import org.example.rbac.service.RoleService;
import org.example.rbac.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author bgmyangzhu
 * @date 2025/1/7 14:04
 */
@RestController
@CrossOrigin
@RequestMapping("/role")
public class RoleController {

    @Autowired
    RoleService roleService;
    
    @Autowired
    RbacService rbacService;
    
    @GetMapping
    public Result listRoles(){
        return Result.success(roleService.list());
    }
    @GetMapping("/page")
    public Result findPage(@RequestParam String name,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Role::getName, name);
        queryWrapper.orderByDesc(Role::getId);
        queryWrapper.eq(Role::getDeleted,0);
        return Result.success(roleService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }
    
    @PostMapping("/save")
    public Result save(@RequestBody Role role){
        roleService.saveOrUpdate(role);
        return Result.success();
    }
    
    @PostMapping("/saveRolePermission/{roleId}")
    public Result saveRolePermission(@PathVariable Integer roleId, @RequestBody List<Integer> permissionIds){
        
        roleService.saveRolePermission(roleId,permissionIds);
        return Result.success();
    }
    
    @DeleteMapping("/{roleId}")
    public Result delete(@PathVariable Integer roleId){
        roleService.removeById(roleId);
        roleService.deleteRolePermissionByRoleId(roleId);
        return Result.success();
    }
    
    @GetMapping("/listPermissionIdsByRoleId/{roleId}")
    public Result listPermissionIdsByRoleId(@PathVariable Integer roleId){
        List<Integer> permissionIds = roleService.listPermissionIdsByRoleId(roleId);
        return Result.success(permissionIds);
    }
    
}
