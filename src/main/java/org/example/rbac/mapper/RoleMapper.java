package org.example.rbac.mapper;

import org.apache.ibatis.annotations.*;
import org.example.rbac.domain.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.rbac.domain.RolePermission;
import org.example.rbac.domain.RoleUser;

import java.util.List;

/**
* @author bgmyangzhu
* @description 针对表【role】的数据库操作Mapper
* @createDate 2024-12-31 15:54:19
* @Entity org.example.rbac.domain.Role
*/
public interface RoleMapper extends BaseMapper<Role> {
    
    @Select("select * from role_user where user_id = #{userId}")
    List<RoleUser> listByUserId(Integer userId);
    
    @Select("select * from role_user")
    List<RoleUser> listRoleUsers();
    
    @Delete("delete from role_permission where role_id = #{roleId}")
    void deleteRolePermissionByRoleId(Integer roleId);
    
    @Delete("delete from role_user where user_id = #{userId}")
    void deleteRoleUserByUserId(Integer userId);
    
    void deleteRoleUserByUserIds(@Param("userIds") List<Integer> userIds);

    @Select("select * from role_permission where role_id = #{roleId}")
    List<RolePermission> listRolePermissionByRoleId(Integer roleId);
    
    void saveRolePermissions(@Param("roleId") Integer roleId, @Param("permissionIds") List<Integer> permissionIds);
    
    void saveUserRoles(@Param("userId") Integer userId, @Param(("roleIds")) List<Integer> roleIds);
    void deleteRolePermissions(@Param("roleId") Integer roleId);
    
    @Insert("insert into role_permission (role_id, permission_id) values (1, #{permissionId});")
    void addPermissionForAdmin(@Param("permissionId") Integer permissionId);
}




