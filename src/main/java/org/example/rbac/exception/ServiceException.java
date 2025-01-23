package org.example.rbac.exception;

import lombok.Getter;

/**
 * @author bgmyangzhu
 * @date 2025/1/2 15:51
 */

/**
 * 自定义异常类
 */
@Getter
public class ServiceException extends RuntimeException{
    
    private String code;
    
    public ServiceException(String code, String msg){
        super(msg);
        this.code = code;
    }
}
