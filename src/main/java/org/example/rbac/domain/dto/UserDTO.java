package org.example.rbac.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import org.example.rbac.domain.Permission;
import org.example.rbac.domain.Role;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author bgmyangzhu
 * @date 2025/1/23 13:49
 */
@Data
public class UserDTO {

    private Integer id;

    private String username;

    private String password;

    private String nickname;

    private String email;

    private String phone;

    private String address;

    
    private List<Integer> roles;

}
