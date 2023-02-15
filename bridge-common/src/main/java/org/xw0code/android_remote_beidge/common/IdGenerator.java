package org.xw0code.android_remote_beidge.common;

import org.xw0code.android_remote_beidge.common.LogUtils;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {
    private static final AtomicLong ids = new AtomicLong(System.currentTimeMillis());

    public static synchronized long nextId() {
        return ids.incrementAndGet();
    }
}
