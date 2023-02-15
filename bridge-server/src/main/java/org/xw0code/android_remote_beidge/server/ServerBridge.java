package org.xw0code.android_remote_beidge.server;

import io.netty.channel.Channel;
import org.xw0code.android_remote_beidge.common.LogUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ServerBridge implements IServerBridge {
    private final CglibServerBridgeFactory cglibServerBridgeFactory = new CglibServerBridgeFactory();

    @Override
    public <T> T getBridge(Class<T> bridgeClass) {
        return getBridge(bridgeClass, 60, TimeUnit.SECONDS);
    }

    @Override
    public <T> T getBridge(Class<T> bridgeClass, int timeout, TimeUnit unit) {
        if (!SupportBridgeClientManagerContainer.getInstance().contain(bridgeClass)) {
            throw new IllegalArgumentException(String.format(Locale.ENGLISH, "Bridge %s is not supported", bridgeClass.getName()));
        }
        synchronized (ServerBridge.class) {
            SupportBridgeClientManager supportBridgeClientManager = SupportBridgeClientManagerContainer.getInstance().get(bridgeClass);
            return cglibServerBridgeFactory.create(bridgeClass, supportBridgeClientManager, timeout, unit);
        }
    }


    @Override
    public void registerClient(Client client) {
        LogUtils.info("Register client: {}", client);
        client.getChannel().closeFuture().addListener(future -> unregisterClient(client));
    }

    @Override
    public void unregisterClient(Client client) {
        LogUtils.info("Unregister client: {}", client);
        SupportBridgeClientManagerContainer.getInstance().unregisterClient(client);
    }
}
