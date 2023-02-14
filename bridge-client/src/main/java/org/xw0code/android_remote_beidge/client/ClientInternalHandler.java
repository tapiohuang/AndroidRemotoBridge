package org.xw0code.android_remote_beidge.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.xw0code.android_remote_beidge.common.BridgeInvokeResult;
import org.xw0code.android_remote_beidge.common.BridgeInvoker;
import org.xw0code.android_remote_beidge.common.InternalData;
import org.xw0code.android_remote_beidge.common.RuntimeContainer;

public class ClientInternalHandler extends SimpleChannelInboundHandler<InternalData> {
    private ClientBridge clientBridge;

    public ClientInternalHandler(ClientBridge clientBridge) {
        this.clientBridge = clientBridge;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, InternalData internalData) throws Exception {
        switch (internalData.getType()) {
            case InternalData.INVOKE:
                channelHandlerContext.writeAndFlush(toInvoke(internalData));
                return;
            default:
                throw new RuntimeException("Unknown internal data type: " + internalData.getType());
        }
    }

    private InternalData toInvoke(InternalData internalData) {
        BridgeInvoker bridgeInvoker = RuntimeContainer.BRIDGE_PROTOCOL.deserializeInvoke(internalData.getData());
        BridgeInvokeResult bridgeInvokeResult = clientBridge.invoke(bridgeInvoker);
        byte[] data = RuntimeContainer.BRIDGE_PROTOCOL.serializeResult(bridgeInvokeResult);
        return new InternalData(internalData.getId(), InternalData.INVOKE_RESULT, data);
    }
}
