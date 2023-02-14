package org.xw0code.android_remote_beidge.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class BridgeInvokeResult implements Serializable {
    private final long invokeId;
    private final Object result;
}
