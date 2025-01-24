package org.example.rbac.service;

import org.example.rbac.domain.Permission;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.rbac.domain.RolePermission;

import java.util.List;
import java.util.Set;

/**
* @author bgmyangzhu
* @description 针对表【permission】的数据库操作Service
* @createDate 2024-12-31 15:54:19
*/
public interface PermissionService extends IService<Permission> {

    List<RolePermission> listByRoleIds(List<Integer> roleIds);

    Set<Permission> buildPermissionTree(Set<Permission> permissions);

    Set<Permission> buildAllPermissionTree(Set<Permission> permissions);
    
    void delete(Integer id);

    boolean existsByComment(String comment);
}
