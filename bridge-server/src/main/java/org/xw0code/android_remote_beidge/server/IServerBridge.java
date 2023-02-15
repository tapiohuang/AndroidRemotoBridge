package org.xw0code.android_remote_beidge.server;

import io.netty.channel.Channel;

import java.util.concurrent.TimeUnit;

public interface IServerBridge {
    <T> T getBridge(Class<T> bridgeClass);


    <T> T getBridge(Class<T> bridgeClass, int timeout, TimeUnit unit);

    void registerClient(Client client);

    void unregisterClient(Client client);

}
