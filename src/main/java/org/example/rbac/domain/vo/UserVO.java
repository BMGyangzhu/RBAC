package org.example.rbac.domain.vo;

import lombok.Data;
import org.example.rbac.domain.Permission;
import org.example.rbac.domain.Role;

import java.util.Set;

/**
 * @author bgmyangzhu
 * @date 2025/1/21 21:56
 */
@Data
public class UserVO {

    private String username;
    private String nickname;
    private String token;
    private Set<Permission> permissions;
    private Set<Role> roles;
}
