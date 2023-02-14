package org.xw0code.android_remote_beidge.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.xw0code.android_remote_beidge.common.BridgeInvokeResult;
import org.xw0code.android_remote_beidge.common.IBridgeProtocol;
import org.xw0code.android_remote_beidge.common.InternalData;
import org.xw0code.android_remote_beidge.common.RuntimeContainer;

@Slf4j
public class ServerInternalHandler extends SimpleChannelInboundHandler<InternalData> {
    private ServerBridge serverBridge;

    public ServerInternalHandler(ServerBridge serverBridge) {
        this.serverBridge = serverBridge;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, InternalData internalData) throws Exception {
        switch (internalData.getType()) {
            case InternalData.REG_SUPPORT_BRIDGE:
                onRegisterSupportBridge(channelHandlerContext, internalData);
                break;
            case InternalData.IDLE:
                log.info("Client {} send idle", this.serverBridge.getClient(channelHandlerContext.channel()));
                break;
            case InternalData.INVOKE_RESULT:
                onInvokeResult(channelHandlerContext, internalData);
                break;
        }
    }

    private void onRegisterSupportBridge(ChannelHandlerContext channelHandlerContext, InternalData internalData) {
        byte[] bytes = internalData.getData();
        String[] supportBridgeClassNames = new String(bytes).split(",");
        Client client = this.serverBridge.getClient(channelHandlerContext.channel());
        for (String supportBridgeClassName : supportBridgeClassNames
        ) {
            try {
                Class<?> supportBridgeClass = Class.forName(supportBridgeClassName);
                log.info("Client {} support bridge: {}", client, supportBridgeClass);
                client.addSupportedBridge(supportBridgeClass);
            } catch (ClassNotFoundException e) {
                log.error("Class not found: {}", supportBridgeClassName);
            }
        }
    }

    private void onInvokeResult(ChannelHandlerContext channelHandlerContext, InternalData internalData) {
        IBridgeProtocol bridgeProtocol = RuntimeContainer.BRIDGE_PROTOCOL;
        BridgeInvokeResult bridgeInvokeResult = bridgeProtocol.deserializeResult(internalData.getData());
        Client client = this.serverBridge.getClient(channelHandlerContext.channel());
        client.completeInvoke(bridgeInvokeResult);
    }
}
