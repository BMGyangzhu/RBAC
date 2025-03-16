package org.example.rbac.domain.dto;

import lombok.Data;

/**
 * @author bgmyangzhu
 * @date 2025/3/16 2:14
 */
@Data
public class PasswordDTO {
    
    private String username;
    
    private String oldPassword;
    
    private String newPassword;
    
}
