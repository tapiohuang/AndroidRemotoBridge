package org.xw0code.android_remote_beidge.common;


import lombok.extern.slf4j.Slf4j;

public class DefaultBridgeProtocol implements IBridgeProtocol {

    @Override
    public byte[] serializeInvoke(BridgeInvoker bridgeInvoker) {
        return ProtostuffUtil.serializer(bridgeInvoker);
    }

    @Override
    public BridgeInvoker deserializeInvoke(byte[] bytes) {
        return ProtostuffUtil.deserializer(bytes, BridgeInvoker.class);
    }

    @Override
    public byte[] serializeResult(BridgeInvokeResult result) {
        return ProtostuffUtil.serializer(result);
    }

    @Override
    public BridgeInvokeResult deserializeResult(byte[] bytes) {
        return ProtostuffUtil.deserializer(bytes, BridgeInvokeResult.class);
    }
}
