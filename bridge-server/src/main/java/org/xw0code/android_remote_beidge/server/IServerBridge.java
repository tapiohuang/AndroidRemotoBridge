package org.xw0code.android_remote_beidge.server;

import io.netty.channel.Channel;

public interface IServerBridge {
    <T> T getBridge(Class<T> bridgeClass);


    void registerClient(Client client);

    void unregisterClient(Client client);

    Client getClient(Channel channel);
}
