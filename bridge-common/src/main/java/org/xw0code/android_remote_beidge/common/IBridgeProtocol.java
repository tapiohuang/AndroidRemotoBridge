package org.xw0code.android_remote_beidge.common;

import java.io.Serializable;
import java.lang.reflect.Method;

public interface IBridgeProtocol {

    byte[] serializeInvoke(
            BridgeInvoker bridgeInvoker
    );

    BridgeInvoker deserializeInvoke(
            byte[] bytes
    );

    byte[] serializeResult(
            BridgeInvokeResult result
    );

    BridgeInvokeResult deserializeResult(
            byte[] bytes
    );
}
