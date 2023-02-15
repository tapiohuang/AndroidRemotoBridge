package org.xw0code.android_remote_beidge.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.xw0code.android_remote_beidge.common.*;

public class ServerInvokeHandler extends SimpleChannelInboundHandler<InternalData> {
    private Client client;

    public ServerInvokeHandler(Client client) {
        this.client = client;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, InternalData internalData) throws Exception {
        if (internalData.getType() == InternalData.INVOKE_RESULT) {
            onInvokeResult(channelHandlerContext, internalData);
        } else {
            channelHandlerContext.fireChannelRead(internalData);
        }
    }

    private void onInvokeResult(ChannelHandlerContext channelHandlerContext, InternalData internalData) {
        IBridgeProtocol bridgeProtocol = RuntimeContainer.BRIDGE_PROTOCOL;
        BridgeInvokeResult bridgeInvokeResult = bridgeProtocol.deserializeResult(internalData.getData());
        client.completeInvoke(bridgeInvokeResult);
    }
}
