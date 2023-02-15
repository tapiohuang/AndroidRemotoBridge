package org.xw0code.android_remote_beidge.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.xw0code.android_remote_beidge.common.ByteUtils;
import org.xw0code.android_remote_beidge.common.InternalData;
import org.xw0code.android_remote_beidge.common.LogUtils;

import java.util.Date;

public class HeartBeatClientHandler extends ChannelInboundHandlerAdapter {
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        LogUtils.info("HeartBeatClientHandler userEventTriggered: {}", evt);
        if (evt instanceof IdleStateEvent) {
            InternalData internalData = new InternalData(
                    Long.MIN_VALUE, InternalData.IDLE, ByteUtils.fromLong(System.currentTimeMillis())
            );
            ctx.writeAndFlush(internalData);
        }
    }

}
