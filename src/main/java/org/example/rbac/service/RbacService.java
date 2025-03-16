package org.example.rbac.service;

import org.example.rbac.domain.Permission;
import org.example.rbac.domain.User;
import org.example.rbac.domain.dto.UserDTO;
import org.example.rbac.domain.dto.UserLoginDTO;
import org.example.rbac.domain.vo.UserVO;

import java.util.List;
import java.util.Set;

/**
* @author bgmyangzhu

* @createDate 2024-12-31 15:54:19
*/
public interface RbacService  {

    UserVO login(UserLoginDTO userLoginDTO);

    Set<Permission> buildUserPermissionTree(Integer userId);

    Boolean verifyBusinessmenByUserId(Integer userId);

    Boolean verifyAdministrator(Integer userId);

    List<User> listUsers(List<User> users);

    void AddPermissionForAdministrators(Integer id);
    
    UserVO reload(String username);

    void updateUserRoles(Integer userId, List<Integer> roleIds);

    boolean saveUser(UserDTO userDTO);

    String register(UserDTO userDTO);

    void deleteRoleUserByUserId(Integer userId);

    void deleteRoleUserByUserIds(List<Integer> userIds);
}
