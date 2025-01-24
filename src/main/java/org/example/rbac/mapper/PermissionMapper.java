package org.example.rbac.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.rbac.domain.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.rbac.domain.RolePermission;

import java.util.List;

/**
* @author bgmyangzhu
* @description 针对表【permission】的数据库操作Mapper
* @createDate 2024-12-31 15:54:19
* @Entity org.example.rbac.domain.Permission
*/
public interface PermissionMapper extends BaseMapper<Permission> {
    
    List<RolePermission> listByRoleIds(@Param("roleIds") List<Integer> roleIds);
    
    @Select("select * from role_permission where role_id = #{roleId}")
    RolePermission listById(@Param(("roleId")) Integer roleId);
    
    void deleteRolePermissionByPermissionId(@Param(("permissionIds")) List<Integer> permissionIds);
}




