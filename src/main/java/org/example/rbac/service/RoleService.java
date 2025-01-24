package org.example.rbac.service;

import org.example.rbac.domain.Role;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.rbac.domain.RolePermission;
import org.example.rbac.domain.RoleUser;

import java.util.List;

/**
* @author bgmyangzhu
* @description 针对表【role】的数据库操作Service
* @createDate 2024-12-31 15:54:19
*/
public interface RoleService extends IService<Role> {

    List<RoleUser> listByUserId(Integer userId);
    
    List<RoleUser> listRoleUsers();

    void deleteRolePermissionByRoleId(Integer roleId);

    List<Integer> listPermissionIdsByRoleId(Integer roleId);

    void saveRolePermission(Integer roleId, List<Integer> permissionIds);

    void addPermissionForAdmin(Integer permissionId);

    void deleteRoleUserByUserId(Integer userId);

    void deleteRoleUserByUserIds(List<Integer> userIds);

    void saveUserRoles(Integer userId, List<Integer> roleIds);
}
