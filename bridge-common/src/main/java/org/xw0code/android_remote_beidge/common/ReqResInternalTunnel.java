package org.xw0code.android_remote_beidge.common;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public abstract class ReqResInternalTunnel {
    protected final Channel channel;
    protected final int id;

    private final HashMap<Long, InternalReqCompletableFuture<?>> internalReqFutureMap = new HashMap<>();

    protected ReqResInternalTunnel(Channel channel) {
        this.channel = channel;
        this.id = channel.hashCode();
    }

    public <T> InternalReqCompletableFuture<T> req(Object o, int regType, Class<T> resClass) {
        InternalReqCompletableFuture<T> future = new InternalReqCompletableFuture<>(resClass);
        long id = IdGenerator.nextId();
        internalReqFutureMap.put(id, future);
        future.whenComplete(((t, throwable) -> {
            LogUtils.info("req complete: " + id);
            internalReqFutureMap.remove(id);
        }));
        if (!channel.isActive()) {
            future.completeExceptionally(new RuntimeException("channel is not active"));
            return future;
        }
        byte[] data = ProtostuffUtil.serializer(o);
        int totalLen = data.length + 4;
        byte[] payload = new byte[totalLen];
        System.arraycopy(ByteUtils.fromInt(regType), 0, payload, 0, 4);
        System.arraycopy(data, 0, payload, 4, data.length);
        InternalData internalData = new InternalData(id, InternalData.INTERNAL_REQ, payload);
        channel.writeAndFlush(internalData).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (!channelFuture.isSuccess()) {
                    LogUtils.info("req failed: " + id);
                    future.completeExceptionally(channelFuture.cause());
                }
            }
        });
        return future;
    }

    public void res(InternalData internalData) {
        InternalReqCompletableFuture<?> future = internalReqFutureMap.get(internalData.getId());
        if (future != null) {
            Object res = ProtostuffUtil.deserializer(internalData.getData(), future.getResClass());
            future.complete(res);
        }
    }

    public void cmd(int cmdType, Object o) {
        if (!channel.isActive()) {
            LogUtils.info("channel is not active");
            return;
        }
        byte[] data = ProtostuffUtil.serializer(o);
        byte[] payload = new byte[4 + data.length];
        System.arraycopy(ByteUtils.fromInt(cmdType), 0, payload, 0, 4);
        System.arraycopy(data, 0, payload, 4, data.length);
        long id = IdGenerator.nextId();
        InternalData internalData = new InternalData(id, InternalData.INTERNAL_CMD, payload);
        channel.writeAndFlush(internalData);
    }

}
