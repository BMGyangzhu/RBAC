package org.example.rbac.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import lombok.Data;

/**
 * @TableName role
 */
@TableName(value ="role")
@Data
public class Role implements Serializable {
    @TableId(type = IdType.AUTO)

    private Integer id;

    private String name;

    private String comment;
    
    @TableLogic
    private Integer deleted;
    
    private static final long serialVersionUID = 1L;
}