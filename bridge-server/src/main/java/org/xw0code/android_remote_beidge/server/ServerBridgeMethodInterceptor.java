package org.xw0code.android_remote_beidge.server;

import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.xw0code.android_remote_beidge.common.BridgeInvoker;
import org.xw0code.android_remote_beidge.common.IdGenerator;
import org.xw0code.android_remote_beidge.common.LogUtils;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


public class ServerBridgeMethodInterceptor implements MethodInterceptor {
    private final Client client;

    private final int timeout;

    private final TimeUnit timeUnit;

    public ServerBridgeMethodInterceptor(Client client, int timeout, TimeUnit timeUnit) {
        this.client = client;
        this.timeUnit = timeUnit;
        this.timeout = timeout;
    }

    public ServerBridgeMethodInterceptor(Client client) {
        this(client, 60, TimeUnit.SECONDS);
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        LogUtils.info("intercept: {} {}", o.getClass().getName(), method.getName());
        BridgeInvoker bridgeInvoker = new BridgeInvoker(
                o.getClass().getName().split("\\$\\$")[0],
                method.getName(),
                method.getParameterTypes(),
                objects, IdGenerator.nextId()
        );
        CompletableFuture<Object> future = client.send(bridgeInvoker);
        try {
            return future.get(timeout, timeUnit);
        } catch (Throwable throwable) {
            future.completeExceptionally(throwable);
            throw throwable;
        }

    }

}
