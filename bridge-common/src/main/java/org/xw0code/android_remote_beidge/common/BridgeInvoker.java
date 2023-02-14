package org.xw0code.android_remote_beidge.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BridgeInvoker implements Serializable {
    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] args;

    private long invokeId;
}
