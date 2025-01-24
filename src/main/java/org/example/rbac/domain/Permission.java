package org.example.rbac.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import lombok.Data;

/**
 * @TableName permission
 */
@TableName(value ="permission")
@Data
public class Permission implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private String path;

    private String comment;
    
    private String icon;
    
    private Integer parentId;

    @TableLogic
    private Integer deleted;
    
    private Long deletedTime;
    
    @TableField(exist = false)
    private Set<Permission> children;

    private static final long serialVersionUID = 1L;

    /**
     * 深拷贝 Permission 对象
     *
     * @param permission 原始 Permission 对象
     * @return 深拷贝后的 Permission 对象
     */
    public static Permission deepCopyPermission(Permission permission) {
        
        if (permission == null){
            return null;
        }
        
        Permission copy = new Permission();
        copy.setId(permission.getId());
        copy.setName(permission.getName());
        copy.setPath(permission.getPath());
        copy.setComment(permission.getComment());
        copy.setIcon(permission.getIcon());
        copy.setParentId(permission.getParentId());
        copy.setDeleted(permission.getDeleted());
        copy.setDeletedTime(permission.getDeletedTime());
        copy.setChildren(new LinkedHashSet<>()); // 初始化空子节点集合
        return copy;
    }
}