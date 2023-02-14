package org.xw0code.android_remote_beidge.server;

import lombok.extern.slf4j.Slf4j;
import org.xw0code.android_remote_beidge.common.*;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
class ServerBridgeBoostrapTest {

    public static void main(String[] args) {
        ServerBridgeBoostrap serverBridgeBoostrap = new ServerBridgeBoostrap();
        IServerBridge serverBridge = serverBridgeBoostrap.bridgeProtocol(new DefaultBridgeProtocol())
                .internalProtocol(new DefaultInternalProtocol())
                .registerBridge(TestRpcService.class)
                .start();
        Executors.newScheduledThreadPool(4).schedule(() -> {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 1000; i++) {
                TestRpcService testRpcService = serverBridge.getBridge(TestRpcService.class);
                testRpcService.encryptHttpSign("httpSign" + i);
            }
            log.info("cost:{}", System.currentTimeMillis() - start);
        }, 20, TimeUnit.SECONDS);
    }
}