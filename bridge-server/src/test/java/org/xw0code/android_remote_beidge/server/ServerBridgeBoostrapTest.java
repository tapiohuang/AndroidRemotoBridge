package org.xw0code.android_remote_beidge.server;

import lombok.extern.slf4j.Slf4j;
import org.xw0code.android_remote_beidge.common.*;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
class ServerBridgeBoostrapTest {

    public static void main(String[] args) {
        ServerBridgeBoostrap serverBridgeBoostrap = new ServerBridgeBoostrap();
        serverBridgeBoostrap.addCmdHandler(new RegisterSupportBridge())
                .addCmdHandler(new ClientReadyHandler());
        IServerBridge serverBridge = serverBridgeBoostrap
                .bridgeProtocol(new DefaultBridgeProtocol())
                .internalProtocol(new DefaultInternalProtocol())
                .registerBridge(TestRpcService.class)
                .debug(true)
                .start();
    }
}