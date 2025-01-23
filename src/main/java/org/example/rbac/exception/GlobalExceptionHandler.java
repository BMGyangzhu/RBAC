package org.example.rbac.exception;

import org.example.rbac.util.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author bgmyangzhu
 * @date 2025/1/2 15:49
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 如果抛出的是ServiceException则调用该方法
     * @param se
     * @return
     */
    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public Result handle(ServiceException se){
        return Result.error(se.getCode(), se.getMessage());
    }
}
