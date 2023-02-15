package org.xw0code.android_remote_beidge.common;

import io.netty.channel.ChannelHandlerContext;

public interface ReqHandler {
    InternalData handle(ChannelHandlerContext channelHandlerContext, InternalData internalData, HandlerAttributes handlerAttributes);

    int getReqType();
}
