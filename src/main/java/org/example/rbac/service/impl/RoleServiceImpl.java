package org.example.rbac.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.rbac.domain.Role;
import org.example.rbac.domain.RolePermission;
import org.example.rbac.domain.RoleUser;
import org.example.rbac.service.RoleService;
import org.example.rbac.mapper.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author bgmyangzhu
* @description 针对表【role】的数据库操作Service实现
* @createDate 2024-12-31 15:54:19
*/
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>
    implements RoleService{
    
    @Autowired
    RoleMapper roleMapper;
    @Override
    public List<RoleUser> listByUserId(Integer userId){
        return roleMapper.listByUserId(userId);
    }

    @Override
    public List<RoleUser> listRoleUsers() {
        return roleMapper.listRoleUsers();
    }

    @Override
    public void deleteRolePermissionByRoleId(Integer roleId){  roleMapper.deleteRolePermissionByRoleId(roleId);}

    @Override
    public List<Integer> listPermissionIdsByRoleId(Integer roleId) {
        List<RolePermission> rolePermission = roleMapper.listRolePermissionByRoleId(roleId);
        // 抽取permissionIds
        return rolePermission.stream()
                             .map(RolePermission::getPermissionId)
                             .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void saveRolePermission(Integer roleId, List<Integer> permissionIds){
        roleMapper.deleteRolePermissions(roleId);
        roleMapper.saveRolePermissions(roleId, permissionIds);
    }

    @Override
    public void saveUserRoles(Integer userId, List<Integer> roleIds) {
        roleMapper.saveUserRoles(userId, roleIds);
    }
    
    @Override
    public void saveUserRole(Integer userId, Integer roleId) {
        roleMapper.saveUserRole(userId,roleId);
    }

    @Override
    public void addPermissionForAdmin(Integer permissionId){
        // 添加权限时为管理员添加该权限
        roleMapper.addPermissionForAdmin(permissionId);
    }
    
    @Override
    public void deleteRoleUserByUserId(Integer userId){
        roleMapper.deleteRoleUserByUserId(userId);
    }
    
    @Override
    public void deleteRoleUserByUserIds(List<Integer> userIds){
        roleMapper.deleteRoleUserByUserIds(userIds);
    }
}




