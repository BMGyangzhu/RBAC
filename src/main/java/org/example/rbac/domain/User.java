package org.example.rbac.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import lombok.Data;

/**
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String username;

    private String password;
    
    private String nickname;
    
    private String email;

    private String phone;
    
    private String address;

    @TableLogic
    private Integer deleted;

    private Long deletedTime;
    
    @TableField(exist = false)
    private Set<Permission> permissions;
    
    @TableField(exist = false)
    private Set<Role> roles;

    private static final long serialVersionUID = 1L;
    
    
}