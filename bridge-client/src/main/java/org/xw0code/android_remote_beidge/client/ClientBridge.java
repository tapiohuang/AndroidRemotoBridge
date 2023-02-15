package org.xw0code.android_remote_beidge.client;

import io.netty.channel.Channel;
import org.xw0code.android_remote_beidge.common.BridgeInvokeResult;
import org.xw0code.android_remote_beidge.common.BridgeInvoker;

import java.lang.reflect.Method;
import java.util.HashMap;

public class ClientBridge implements IClientBridge {
    private final HashMap<String, Object> bridgeImplMap = new HashMap<>();


    @Override
    public BridgeInvokeResult invoke(BridgeInvoker bridgeInvoker) {
        try {
            String className = bridgeInvoker.getClassName();
            String methodName = bridgeInvoker.getMethodName();
            Object[] args = bridgeInvoker.getArgs();
            Class<?>[] parameterTypes = bridgeInvoker.getParameterTypes();
            Object bridgeImpl = bridgeImplMap.get(className);
            if (bridgeImpl == null) {
                throw new RuntimeException("bridgeImpl is null");
            }
            Class<?> bridgeImplClass = bridgeImpl.getClass();
            Method method = bridgeImplClass.getMethod(methodName, parameterTypes);
            Object result = method.invoke(bridgeImpl, args);
            return new BridgeInvokeResult(bridgeInvoker.getInvokeId(), result);
        } catch (Throwable throwable) {
            return new BridgeInvokeResult(bridgeInvoker.getInvokeId(), throwable);
        }
    }

    @Override
    public String[] getSupportedBridgeClassName() {
        return bridgeImplMap.keySet().toArray(new String[0]);
    }

    @Override
    public <T> void registerBridge(Class<T> bridgeClazz, T bridgeImpl) {
        bridgeImplMap.put(bridgeClazz.getName(), bridgeImpl);
    }

}
