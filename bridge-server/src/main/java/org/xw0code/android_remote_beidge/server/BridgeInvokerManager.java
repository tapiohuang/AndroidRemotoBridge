package org.xw0code.android_remote_beidge.server;

import org.xw0code.android_remote_beidge.common.BridgeInvoker;

import java.util.HashMap;

public class BridgeInvokerManager {
    private static final BridgeInvokerManager instance = new BridgeInvokerManager();

    private final HashMap<Long, Client> invokerClient = new HashMap<>();

    private BridgeInvokerManager() {
    }

    public static BridgeInvokerManager getInstance() {
        return instance;
    }

    public void addInvoker(BridgeInvoker bridgeInvoker, Client client) {
        invokerClient.put(bridgeInvoker.getInvokeId(), client);
    }

    public Client getClient(BridgeInvoker bridgeInvoker) {
        return invokerClient.get(bridgeInvoker.getInvokeId());
    }

    public void removeInvoker(BridgeInvoker bridgeInvoker) {
        invokerClient.remove(bridgeInvoker.getInvokeId());
    }
}
