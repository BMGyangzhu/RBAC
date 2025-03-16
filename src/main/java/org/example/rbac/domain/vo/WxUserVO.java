package org.example.rbac.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author bgmyangzhu
 * @date 2025/2/27 14:49
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WxUserVO implements Serializable {
    
    private Long id;
    private String openId;
    private String token;
}
