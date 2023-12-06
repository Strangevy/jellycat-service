package com.jellycat.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jellycat.util.ResponseUtils;
import com.jellycat.vo.ResponseVo;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseVo<Object> handleException(Exception e) {
        return ResponseUtils.fail(e.getMessage());
    }
}
