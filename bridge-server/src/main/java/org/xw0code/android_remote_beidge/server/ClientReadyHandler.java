package org.xw0code.android_remote_beidge.server;

import io.netty.channel.ChannelHandlerContext;
import org.xw0code.android_remote_beidge.common.CmdHandler;
import org.xw0code.android_remote_beidge.common.HandlerAttributes;
import org.xw0code.android_remote_beidge.common.LogUtils;

public class ClientReadyHandler implements CmdHandler {
    @Override
    public void handle(ChannelHandlerContext channelHandlerContext, byte[] data, HandlerAttributes handlerAttributes) {
        LogUtils.info("client is ready");
    }

    @Override
    public int getCmdType() {
        return 11;
    }
}
