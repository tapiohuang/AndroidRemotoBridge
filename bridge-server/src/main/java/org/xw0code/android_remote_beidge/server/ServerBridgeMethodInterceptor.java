package org.xw0code.android_remote_beidge.server;

import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.xw0code.android_remote_beidge.common.BridgeInvoker;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ServerBridgeMethodInterceptor implements MethodInterceptor {
    private final Client client;

    public ServerBridgeMethodInterceptor(Client client) {
        this.client = client;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        //log.info("intercept: {} {}", o.getClass().getName(), method.getName());
        BridgeInvoker bridgeInvoker = new BridgeInvoker(
                o.getClass().getName().split("\\$\\$")[0],
                method.getName(),
                method.getParameterTypes(),
                objects, 1L
        );
        try {
            return client.send(bridgeInvoker).get(60, TimeUnit.SECONDS);
        } catch (Throwable throwable) {
            throw throwable.getCause().getCause();
        }

    }

}
