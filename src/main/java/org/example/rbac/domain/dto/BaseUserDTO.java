package org.example.rbac.domain.dto;

import lombok.Data;

/**
 * @author bgmyangzhu
 * @date 2025/3/16 16:40
 */
@Data
public class BaseUserDTO {
    
    private String username;
    
    private String nickname;
    
    private String email;
    
    private String address;
}
