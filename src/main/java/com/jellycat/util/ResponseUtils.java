package com.jellycat.util;

import com.jellycat.vo.ResponseVo;

public class ResponseUtils {
    public static final int SUCCESS = 200;

    public static final int FAIL = 500;

    public static final String SUCCESS_MSG = "success";

    public static final String FAIL_MSG = "fail";

    public static <T> ResponseVo<T> success() {
        return new ResponseVo<T>(SUCCESS, SUCCESS_MSG, null);
    }

    public static <T> ResponseVo<T> success(T data) {
        return new ResponseVo<T>(SUCCESS, SUCCESS_MSG, data);
    }

    public static <T> ResponseVo<T> fail() {
        return new ResponseVo<T>(FAIL, FAIL_MSG, null);
    }

    public static <T> ResponseVo<T> fail(String msg) {
        return new ResponseVo<T>(FAIL, msg, null);
    }

    public static <T> ResponseVo<T> build(int status, String msg, T data) {
        return new ResponseVo<T>(status, msg, data);
    }
}
