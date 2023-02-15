package org.xw0code.android_remote_beidge.client;

import org.xw0code.android_remote_beidge.common.CmdHandler;
import org.xw0code.android_remote_beidge.common.IBridgeProtocol;
import org.xw0code.android_remote_beidge.common.IInternalProtocol;
import org.xw0code.android_remote_beidge.common.ReqHandler;

public interface IClientBridgeBoostrap {
    IClientBridgeBoostrap server(String ip, int port);

    <T> IClientBridgeBoostrap registerBridge(Class<T> bridgeClazz, T bridgeImpl);

    IClientBridgeBoostrap bridgeProtocol(IBridgeProtocol bridgeProtocol);

    IClientBridgeBoostrap internalProtocol(IInternalProtocol internalProtocol);

    Server start();

    IClientBridgeBoostrap debug(boolean debug);


    void close();
}
