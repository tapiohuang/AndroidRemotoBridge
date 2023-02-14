package org.xw0code.android_remote_beidge.client;


import org.xw0code.android_remote_beidge.common.DefaultBridgeProtocol;
import org.xw0code.android_remote_beidge.common.DefaultInternalProtocol;
import org.xw0code.android_remote_beidge.common.TestRpcService;

class ClientBridgeBoostrapTest {
    public static void main(String[] args) {
        ClientBridgeBoostrap clientBridgeBoostrap = new ClientBridgeBoostrap();
        clientBridgeBoostrap.server("127.0.0.1", 23333)
                .registerBridge(TestRpcService.class, new TestRpcServiceImpl())
                .bridgeProtocol(new DefaultBridgeProtocol())
                .internalProtocol(new DefaultInternalProtocol())
                .start();
    }
}