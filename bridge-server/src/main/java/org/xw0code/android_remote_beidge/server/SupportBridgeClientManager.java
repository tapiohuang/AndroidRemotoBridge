package org.xw0code.android_remote_beidge.server;

import org.xw0code.android_remote_beidge.common.LogUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SupportBridgeClientManager {
    private final Class<?> supportBridgeClass;
    private final List<Client> clients = new ArrayList<>();
    private final AtomicInteger nextClientId = new AtomicInteger(0);

    public SupportBridgeClientManager(Class<?> supportBridgeClass) {
        this.supportBridgeClass = supportBridgeClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SupportBridgeClientManager)) return false;
        SupportBridgeClientManager that = (SupportBridgeClientManager) o;
        return supportBridgeClass.equals(that.supportBridgeClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(supportBridgeClass);
    }

    public void registerClient(Client client) {
        synchronized (clients) {
            if (clients.contains(client)) {
                return;
            }
            LogUtils.info("registerClient: {}", client);
            clients.add(client);
        }
    }

    public Client selectClient() {
        synchronized (clients) {
            LogUtils.info("select client");
            if (clients.isEmpty()) {
                return null;
            }
            int index = nextClientId.getAndIncrement() % clients.size();
            return clients.get(index);
        }
    }

    public Client selectClient(Method method) {
        synchronized (clients) {
            LogUtils.info("select client by method");
            if (clients.isEmpty()) {
                LogUtils.info("no clients");
                return null;
            }
            int index = nextClientId.getAndIncrement() % clients.size();
            return clients.get(index);
        }
    }

    public void unregisterClient(Client client) {
        synchronized (clients) {
            LogUtils.info("unregisterClient: {}", client);
            clients.remove(client);
        }
    }
}
