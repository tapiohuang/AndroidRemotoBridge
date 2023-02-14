package org.xw0code.android_remote_beidge.server;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class ServerBridge implements IServerBridge {
    private final CglibServerBridgeFactory cglibServerBridgeFactory = new CglibServerBridgeFactory();
    private final HashSet<Class<?>> supportedBridgeSet = new HashSet<>();
    private final HashMap<Integer, Client> clientMap = new HashMap<>();

    @Override
    public <T> T getBridge(Class<T> bridgeClass) {
        if (!supportedBridgeSet.contains(bridgeClass)) {
            throw new IllegalArgumentException(String.format(Locale.ENGLISH, "Bridge %s is not supported", bridgeClass.getName()));
        }
        synchronized (ServerBridge.class) {
            for (Client client : clientMap.values()) {
                if (client.isSupportedBridge(bridgeClass)) {
                    return cglibServerBridgeFactory.create(bridgeClass, client);
                }
            }
        }
        throw new RuntimeException(String.format(Locale.ENGLISH, "Bridge %s is not supported", bridgeClass.getName()));
    }

    void registerBridge(Class<?> bridgeClass) {
        this.supportedBridgeSet.add(bridgeClass);
    }

    @Override
    public void registerClient(Client client) {
        log.info("Register client: {}", client);
        synchronized (ServerBridge.class) {
            this.clientMap.put(client.getClientId(), client);
            client.getChannel().closeFuture().addListener(future -> unregisterClient(client));
        }
    }

    @Override
    public void unregisterClient(Client client) {
        log.info("Unregister client: {}", client);
        synchronized (ServerBridge.class) {
            this.clientMap.remove(client.getClientId());
        }
    }

    @Override
    public Client getClient(Channel channel) {
        return clientMap.get(channel.hashCode());
    }

}
