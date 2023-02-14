package org.xw0code.android_remote_beidge.server;

import net.sf.cglib.proxy.Enhancer;

public class CglibServerBridgeFactory {

    <T> T create(Class<T> bridgeClass, Client client) {
        Enhancer enhancer = new Enhancer();  // 通过CGLIB动态代理获取代理对象的过程
        enhancer.setInterfaces(new Class[]{bridgeClass});     // 设置enhancer对象的父类
        enhancer.setCallback(new ServerBridgeMethodInterceptor(client));
        return (T) enhancer.create();
    }
}
