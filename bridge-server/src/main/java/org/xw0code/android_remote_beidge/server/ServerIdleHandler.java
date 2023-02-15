package org.xw0code.android_remote_beidge.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.xw0code.android_remote_beidge.common.ByteUtils;
import org.xw0code.android_remote_beidge.common.InternalData;
import org.xw0code.android_remote_beidge.common.InternalReqCompletableFuture;
import org.xw0code.android_remote_beidge.common.LogUtils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ServerIdleHandler extends SimpleChannelInboundHandler<InternalData> {
    private final Client client;

    public ServerIdleHandler(Client client) {
        this.client = client;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            Executors.newCachedThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    InternalReqCompletableFuture<String> future = client.req("hello?", 1, String.class);
                    String result = null;
                    try {
                        result = future.get(5, TimeUnit.SECONDS);
                    } catch (InterruptedException | ExecutionException | TimeoutException e) {
                        throw new RuntimeException(e);
                    }
                    LogUtils.info("result: {}", result);
                }
            });
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, InternalData internalData) throws Exception {
        if (internalData.getType() == InternalData.IDLE) {
            LogUtils.info("Client {} send idle", client.getChannel());
        } else {
            channelHandlerContext.fireChannelRead(internalData);
        }
    }
}
