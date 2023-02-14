package org.xw0code.android_remote_beidge.server;

import org.xw0code.android_remote_beidge.common.IBridgeProtocol;
import org.xw0code.android_remote_beidge.common.IInternalProtocol;

public interface IServerBridgeBootstrap {

    IServerBridgeBootstrap port(int port);

    IServerBridgeBootstrap bridgeProtocol(IBridgeProtocol bridgeProtocol);

    IServerBridgeBootstrap internalProtocol(IInternalProtocol internalProtocol);

    IServerBridgeBootstrap registerBridge(Class<?> bridgeClass);

    void close();
    IServerBridge start();
}
