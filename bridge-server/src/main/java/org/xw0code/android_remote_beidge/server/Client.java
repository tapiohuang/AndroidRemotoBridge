package org.xw0code.android_remote_beidge.server;

import io.netty.channel.Channel;
import org.xw0code.android_remote_beidge.common.BridgeInvokeResult;
import org.xw0code.android_remote_beidge.common.BridgeInvoker;
import org.xw0code.android_remote_beidge.common.InternalData;
import org.xw0code.android_remote_beidge.common.RuntimeContainer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;


public class Client {
    private final int clientId;
    private final Channel channel;
    private final HashSet<Class<?>> supportedBridgeSet = new HashSet<>();
    private final HashMap<Long, CompletableFuture<Object>> invokeFutureMap = new HashMap<>();

    public Client(Channel channel) {
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

    public void addSupportedBridge(Class<?> bridgeClass) {
        supportedBridgeSet.add(bridgeClass);
    }

    public boolean isSupportedBridge(Class<?> bridgeClass) {
        //return true;
        return supportedBridgeSet.contains(bridgeClass);
    }

    public CompletableFuture<Object> send(BridgeInvoker bridgeInvoker) {
        CompletableFuture<Object> future = new CompletableFuture<>();
        invokeFutureMap.put(bridgeInvoker.getInvokeId(), future);
        BridgeInvokerManager.getInstance().addInvoker(bridgeInvoker, this);
        try {
            channel.writeAndFlush(wrapBridgeInvoker(bridgeInvoker)).sync();
        } catch (InterruptedException e) {
            future.completeExceptionally(e);
        }
        future.whenComplete((o, throwable) -> {
            invokeFutureMap.remove(bridgeInvoker.getInvokeId());
            BridgeInvokerManager.getInstance().removeInvoker(bridgeInvoker);
        });
        return future;
    }

    public HashSet<Class<?>> getSupportedBridgeSet() {
        return supportedBridgeSet;
    }

    private InternalData wrapBridgeInvoker(BridgeInvoker bridgeInvoker) {
        InternalData internalData = new InternalData(2L,
                InternalData.INVOKE,
                RuntimeContainer.BRIDGE_PROTOCOL.serializeInvoke(bridgeInvoker));
        return internalData;
    }
}
