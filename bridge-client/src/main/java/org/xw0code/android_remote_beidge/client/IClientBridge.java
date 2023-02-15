package org.xw0code.android_remote_beidge.client;

import org.xw0code.android_remote_beidge.common.BridgeInvokeResult;
import org.xw0code.android_remote_beidge.common.BridgeInvoker;

public interface IClientBridge {

    BridgeInvokeResult invoke(BridgeInvoker bridgeInvoker);

    String[] getSupportedBridgeClassName();

    <T> void registerBridge(Class<T> bridgeClazz, T bridgeImpl);

}
