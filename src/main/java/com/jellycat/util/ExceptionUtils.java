package com.jellycat.util;

public class ExceptionUtils {
    public static RuntimeException buildException(String msg) {
        return new RuntimeException(msg);
    }
}
