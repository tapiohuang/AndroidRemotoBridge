package org.xw0code.android_remote_beidge.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.xw0code.android_remote_beidge.common.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;


public class Client extends ReqResInternalTunnel {
    private final int clientId;
    private final Channel channel;
    private final HashMap<Long, CompletableFuture<Object>> invokeFutureMap = new HashMap<>();


    public Client(Channel channel) {
        super(channel);
        this.channel = channel;
        this.clientId = channel.hashCode();
    }

    public int getClientId() {
        return clientId;
    }

    @Override
    public int hashCode() {
        return channel.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return channel.equals(client.channel);
    }

    public Channel getChannel() {
        return channel;
    }

    public void completeInvoke(BridgeInvokeResult bridgeInvokeResult) {
        CompletableFuture<Object> future = invokeFutureMap.get(bridgeInvokeResult.getInvokeId());
        if (future != null) {
            if (bridgeInvokeResult.getResult() instanceof Throwable)
                future.completeExceptionally((Throwable) bridgeInvokeResult.getResult());
            else
                future.complete(bridgeInvokeResult.getResult());
        }
    }

    public CompletableFuture<Object> send(BridgeInvoker bridgeInvoker) {
        CompletableFuture<Object> future = new CompletableFuture<>();
        invokeFutureMap.put(bridgeInvoker.getInvokeId(), future);
        BridgeInvokerManager.getInstance().addInvoker(bridgeInvoker, this);
        future.whenComplete((o, throwable) -> {
            LogUtils.info("Invoke complete: {}", bridgeInvoker);
            invokeFutureMap.remove(bridgeInvoker.getInvokeId());
            BridgeInvokerManager.getInstance().removeInvoker(bridgeInvoker);
        });
        if (!channel.isActive()) {
            future.completeExceptionally(new RuntimeException("channel is not active"));
            return future;
        }
        channel.writeAndFlush(wrapBridgeInvoker(bridgeInvoker)).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (!channelFuture.isSuccess()) {
                    future.completeExceptionally(channelFuture.cause());
                }
            }
        });
        return future;
    }


    private InternalData wrapBridgeInvoker(BridgeInvoker bridgeInvoker) {
        InternalData internalData = new InternalData(System.currentTimeMillis(),
                InternalData.INVOKE,
                RuntimeContainer.BRIDGE_PROTOCOL.serializeInvoke(bridgeInvoker));
        return internalData;
    }
}
