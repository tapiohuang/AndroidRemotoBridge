package org.xw0code.android_remote_beidge.common;

import lombok.Data;

@Data
public class InternalData {
    public static final int INVOKE = 2;
    public static final int INVOKE_RESULT = 3;
    public static final int IDLE = 0;
    public static final int REG_SUPPORT_BRIDGE = 1;
    private final long id;
    private final Integer type;
    private final byte[] data;

}
