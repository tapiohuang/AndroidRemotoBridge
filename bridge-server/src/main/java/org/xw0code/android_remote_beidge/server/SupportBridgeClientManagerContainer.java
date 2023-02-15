package org.xw0code.android_remote_beidge.server;

import java.util.HashMap;

class SupportBridgeClientManagerContainer {
    private static final SupportBridgeClientManagerContainer instance = new SupportBridgeClientManagerContainer();
    private final HashMap<Class<?>, SupportBridgeClientManager> supportBridgeClientManagers = new HashMap<>();

    private SupportBridgeClientManagerContainer() {
    }

    static SupportBridgeClientManagerContainer getInstance() {
        return instance;
    }

    void registerBridge(Class<?> bridgeClass) {
        this.supportBridgeClientManagers.put(bridgeClass, new SupportBridgeClientManager(bridgeClass));
    }

    public <T> boolean contain(Class<T> bridgeClass) {
        return this.supportBridgeClientManagers.containsKey(bridgeClass);
    }

    public <T> SupportBridgeClientManager get(Class<T> bridgeClass) {
        return this.supportBridgeClientManagers.get(bridgeClass);
    }

    public void unregisterClient(Client client) {
        synchronized (this.supportBridgeClientManagers) {
            for (SupportBridgeClientManager supportBridgeClientManager : this.supportBridgeClientManagers.values()) {
                supportBridgeClientManager.unregisterClient(client);
            }
        }
    }
}
