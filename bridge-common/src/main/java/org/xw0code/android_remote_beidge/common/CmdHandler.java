package org.xw0code.android_remote_beidge.common;

import io.netty.channel.ChannelHandlerContext;

public interface CmdHandler {
    void handle(ChannelHandlerContext channelHandlerContext, byte[] data, HandlerAttributes handlerAttributes);

    int getCmdType();

}
