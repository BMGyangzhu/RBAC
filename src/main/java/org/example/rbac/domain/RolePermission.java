package org.example.rbac.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * @TableName role_permission
 */
@TableName(value ="role_permission")
@Data
public class RolePermission implements Serializable {
    private Integer roleId;

    private Integer permissionId;

    private static final long serialVersionUID = 1L;
}