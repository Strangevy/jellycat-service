package com.jellycat.util;

import org.slf4j.Logger;

public class ExceptionUtils {
    public static RuntimeException buildException(String msg) {
        return new RuntimeException(msg);
    }

    public static RuntimeException buildException(Logger log, Exception e, String msg) {
        log.error(msg, e);
        return new RuntimeException(msg);
    }
}
