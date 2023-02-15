package org.xw0code.android_remote_beidge.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.xw0code.android_remote_beidge.common.*;


public class ClientInvokeHandler extends SimpleChannelInboundHandler<InternalData> {
    private ClientBridge clientBridge;

    public ClientInvokeHandler(ClientBridge clientBridge) {
        this.clientBridge = clientBridge;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, InternalData internalData) throws Exception {
        switch (internalData.getType()) {
            case InternalData.INVOKE:
                channelHandlerContext.writeAndFlush(toInvoke(internalData));
                return;
            default:
                channelHandlerContext.fireChannelRead(internalData);
                return;
        }
    }

    private InternalData toInvoke(InternalData internalData) {
        BridgeInvoker bridgeInvoker = RuntimeContainer.BRIDGE_PROTOCOL.deserializeInvoke(internalData.getData());
        BridgeInvokeResult bridgeInvokeResult = clientBridge.invoke(bridgeInvoker);
        byte[] data = RuntimeContainer.BRIDGE_PROTOCOL.serializeResult(bridgeInvokeResult);
        return new InternalData(internalData.getId(), InternalData.INVOKE_RESULT, data);
    }


}
