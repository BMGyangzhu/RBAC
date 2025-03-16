package org.example.rbac.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.rbac.domain.*;
import org.example.rbac.domain.dto.UserDTO;
import org.example.rbac.domain.dto.UserLoginDTO;
import org.example.rbac.domain.vo.UserVO;
import org.example.rbac.service.PermissionService;
import org.example.rbac.service.RbacService;
import org.example.rbac.service.RoleService;
import org.example.rbac.service.UserService;
import org.example.rbac.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author bgmyangzhu
 * @description RBAC核心业务逻辑实现类
 * @createDate 2024-12-31 15:54:19
 */
@Service
public class RbacServiceImpl implements RbacService {

    @Autowired
    private RoleService roleService;
    @Autowired
    private UserService userService;
    @Autowired
    private PermissionService permissionService;

    @Override
    public UserVO login(UserLoginDTO userLoginDTO) {
        // 验证用户
        User user = userService.lambdaQuery()
                               .eq(User::getUsername, userLoginDTO.getUsername())
                               .eq(User::getPassword, userLoginDTO.getPassword())
                               .one();

        if (user == null) {
            // throw new RuntimeException("用户名或密码错误");
            return null;
        }

        // 获取用户权限
        Set<Permission> userPermissions = buildUserPermissionTree(user.getId());

        // 获取用户角色
        Set<Role> userRoles = getUserRoles(user.getId());

        // 设置用户权限、角色和token
        user.setPermissions(userPermissions);

        user.setRoles(userRoles);

        String token = TokenUtils.getToken(user.getId().toString(), user.getPassword());

        UserVO userVO = new UserVO();

        // 拷贝属性到 DTO
        BeanUtil.copyProperties(user, userVO);
        userVO.setToken(token);

        return userVO;
    }


    @Override
    public UserVO reload(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User user = userService.getOne(queryWrapper);
        if (BeanUtil.isEmpty(user)) {
            return null;
        }
        List<RoleUser> roleUsers = roleService.listByUserId(user.getId());
        List<Integer> roleIds = roleUsers.stream()
                                         .map(RoleUser::getRoleId)
                                         .collect(Collectors.toList());
        List<RolePermission> rolePermissions = permissionService.listByRoleIds(roleIds);
        List<Integer> permissionIds = rolePermissions.stream()
                                                     .map(RolePermission::getPermissionId)
                                                     .collect(Collectors.toList());
        List<Permission> permissions = permissionService.listByIds(permissionIds);
        boolean hasPermissionManagement = permissions.stream()
                                                     .anyMatch(permission -> "权限管理".equals(permission.getComment()));
        if (!hasPermissionManagement) {
            return null;
        }
        // 获取用户权限
        Set<Permission> userPermissions = buildUserPermissionTree(user.getId());

        // 获取用户角色
        Set<Role> userRoles = getUserRoles(user.getId());

        // 设置用户权限，角色和 token
        user.setPermissions(userPermissions);

        user.setRoles(userRoles);

        String token = TokenUtils.getToken(user.getId().toString(), user.getPassword());

        UserVO userVO = new UserVO();

        // 拷贝属性到 VO
        BeanUtil.copyProperties(user, userVO);
        userVO.setToken(token);

        return userVO;

    }

    @Override
    public Set<Permission> buildUserPermissionTree(Integer userId) {
        // 获取用户权限
        Set<Permission> userPermissions = getUserPermissions(userId);

        // 构建权限树
        return permissionService.buildPermissionTree(userPermissions);
    }

    /**
     * 获取用户所有的权限（去重）
     *
     * @param userId 用户ID
     * @return 用户权限集合
     */
    private Set<Permission> getUserPermissions(Integer userId) {
        // 获取用户角色 ID 列表
        List<Integer> roleIds = roleService.listByUserId(userId).stream()
                                           .map(RoleUser::getRoleId)
                                           .collect(Collectors.toList());

        if (roleIds.isEmpty()) {
            return Collections.emptySet();
        }

        // 根据角色 ID 获取所有权限 ID
        List<Integer> permissionIds = permissionService.listByRoleIds(roleIds).stream()
                                                       .map(RolePermission::getPermissionId)
                                                       .collect(Collectors.toList());

        if (permissionIds.isEmpty()) {
            return Collections.emptySet();
        }
        LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Permission::getId, permissionIds);
        queryWrapper.eq(Permission::getDeleted, 0);

        // 查询权限，并去重
        return new HashSet<>(permissionService.list(queryWrapper));
    }

    private Set<Role> getUserRoles(Integer userId) {
        List<RoleUser> roleUsers = roleService.listByUserId(userId);
        List<Integer> roleIds = roleUsers.stream()
                                         .map(RoleUser::getRoleId)
                                         .collect(Collectors.toList());
        List<Role> roles = roleService.listByIds(roleIds);
        Set<Role> roleSet = new HashSet<>(roles);
        return roleSet;

    }
    
    @Override
    public Boolean verifyBusinessmenByUserId(Integer userId){
        Set<Role> roles = getUserRoles(userId);
        
        return roles.stream()
                .anyMatch(role -> "businessmen".equals(role.getName()) && "商家".equals(role.getComment()));
    }
    
    @Override
    public Boolean verifyAdministrator(Integer userId){
        Set<Role> roles = getUserRoles(userId);
        
        return roles.stream()
                .anyMatch(role -> "admin".equals(role.getName()) && "管理员".equals(role.getComment()));
    }

    @Override
    public List<User> listUsers(List<User> users) {
        Map<Integer, Set<Integer>> userIdRoleIdsMap = roleService.listRoleUsers().stream()
                                                            .collect(Collectors.groupingBy(
                                                                    RoleUser::getUserId,
                                                                    Collectors.mapping(
                                                                            RoleUser::getRoleId,
                                                                            Collectors.toSet()
                                                                    )));
        List<Role> roles = roleService.list();


        // 将角色列表转为 Map，以便按 roleId 查找 Role 实体
        Map<Integer, Role> roleIdRoleMap = roles.stream()
                                          .collect(Collectors.toMap(Role::getId, role -> role));

        // 将用户的 roleId 集合转换为实际的 Role 实体集合
        Map<Integer, Set<Role>> userRolesMap = userIdRoleIdsMap.entrySet().stream()
                                                          .collect(Collectors.toMap(
                                                                  Map.Entry::getKey, // 保持 userId
                                                                  entry -> entry.getValue().stream()
                                                                                .map(roleIdRoleMap::get) // 使用 roleId 获取 Role 实体
                                                                                .collect(Collectors.toSet()) // 收集成 Set<Role>
                                                          ));

        // 将转换后的角色集合赋值给用户对象
        for (User user : users) {
            Set<Role> rolesForUser = userRolesMap.get(user.getId());
            if (rolesForUser != null) {
                user.setRoles(rolesForUser); 
            }
        }

        return users;
    }

    @Override
    public void AddPermissionForAdministrators(Integer permissionId) {
        List<Integer> permissionIds = roleService.listPermissionIdsByRoleId(1);
        if (!permissionIds.contains(permissionId)) {
            roleService.addPermissionForAdmin(permissionId);
        }
    }
    
    @Override
    public void updateUserRoles(Integer userId, List<Integer> roleIds){
        roleService.deleteRoleUserByUserId(userId);
        roleService.saveUserRoles(userId, roleIds);
    }
    
    @Override
    public boolean saveUser(UserDTO userDTO){
        User user = BeanUtil.copyProperties(userDTO, User.class, "roles");
        
        // 查看用户名是否已被占用
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,user.getUsername());
        User one = userService.getOne(queryWrapper);
        if (one != null) return false;
        user.setPassword("123456"); // 设置默认密码
        userService.save(user);
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUsername,userDTO.getUsername());
        Integer userId = userService.getOne(lambdaQueryWrapper).getId();
        List<Integer> roleIds = userDTO.getRoles();

        roleService.saveUserRoles(userId,roleIds);
        return true;
    }
    
    @Override
    public String register(UserDTO userDTO) {
        String username = userDTO.getUsername();
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<User> eq = userLambdaQueryWrapper.eq(User::getUsername, username);
        User one = userService.getOne(eq);
        if (one != null) {
            return "该用户已被占用";
        }
        User user = BeanUtil.copyProperties(userDTO, User.class, "roles");
        user.setNickname("user");
        userService.save(user);
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUsername, userDTO.getUsername());
        Integer userId = userService.getOne(lambdaQueryWrapper).getId();

        LambdaQueryWrapper<Role> roleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roleLambdaQueryWrapper.eq(Role::getComment, "普通用户");
        Role role = roleService.getOne(roleLambdaQueryWrapper);
        Integer roleId = role.getId();
        roleService.saveUserRole(userId, roleId);
        
        return "注册成功";
    }
    
    
    
    @Override
    public void deleteRoleUserByUserId(Integer userId){
        roleService.deleteRoleUserByUserId(userId);
    }
    
    @Override
    public void deleteRoleUserByUserIds(List<Integer> userIds){
        roleService.deleteRoleUserByUserIds(userIds);
    }

}
