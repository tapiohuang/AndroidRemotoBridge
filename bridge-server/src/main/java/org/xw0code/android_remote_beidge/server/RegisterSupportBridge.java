package org.xw0code.android_remote_beidge.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.xw0code.android_remote_beidge.common.*;

public class RegisterSupportBridge implements CmdHandler {

    @Override
    public void handle(ChannelHandlerContext ctx, byte[] data, HandlerAttributes handlerAttributes) {
        String[] supportBridgeClassNames = ProtostuffUtil.deserializer(data, String[].class);
        Client client = handlerAttributes.getAttribute("client", Client.class);
        for (String supportBridgeClassName : supportBridgeClassNames
        ) {
            try {
                Class<?> supportBridgeClass = Class.forName(supportBridgeClassName);
                LogUtils.info("Client {} support bridge: {}", client, supportBridgeClass);
                client.addSupportedBridge(supportBridgeClass);
            } catch (ClassNotFoundException e) {
                LogUtils.error("Class not found: {}", supportBridgeClassName);
            }
        }
    }

    @Override
    public int getCmdType() {
        return 10;
    }

}
