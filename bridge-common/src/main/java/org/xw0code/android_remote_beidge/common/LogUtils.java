package org.xw0code.android_remote_beidge.common;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogUtils {
    public static void info(String msg, Object... args) {
        if (RuntimeContainer.DEBUG) {
            log.info(msg, args);
        }
    }

    public static void error(String msg, Object... args) {
        log.error(msg, args);
    }
}
