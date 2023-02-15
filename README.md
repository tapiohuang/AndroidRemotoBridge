# AndroidRemotoBridge

这是一个用于特殊调用的RPC框架，用于Android应用程序和服务器。

例如需要使用某加固APP中的sign方法，但是又不想去破解加固，那么就可以使用这个框架。

### 1. 服务端

服务端通过Cglib动态代理，将需要调用的方法暴露出来，然后通过Socket通信，将方法名，参数，返回值传递给bridge-client

java代码如下：

```java
        ServerBridgeBoostrap serverBridgeBoostrap=new ServerBridgeBoostrap();
        IServerBridge serverBridge=serverBridgeBoostrap.bridgeProtocol(new DefaultBridgeProtocol())
        .internalProtocol(new DefaultInternalProtocol())
        .registerBridge(TestRpcService.class)
        .start();//启动服务
        TestRpcService testrpcService=serverBridge.getBridge(TestRpcService.class);//获取代理对象
        testrpcService.test();//调用方法
```

### 2. 客户端

客户端实现接口，通过ClientBridgeBoostrap.registerBridge()注册接口，然后通过ClientBridgeBoostrap.start()启动服务。
代码如下：

```java
        ClientBridgeBoostrap clientBridgeBoostrap=new ClientBridgeBoostrap();
        clientBridgeBoostrap.server("127.0.0.1",23333)//设置服务端地址
        .registerBridge(TestRpcService.class,new TestRpcServiceImpl())//注册接口
        .bridgeProtocol(new DefaultBridgeProtocol())
        .internalProtocol(new DefaultInternalProtocol())
        .start();//启动服务
```

### 3. 通信协议
通过实现IBridgeProtocol接口，可以自定义Invoke通信协议，框架默认使用的是DefaultBridgeProtocol
>DefaultBridgeProtocol使用阿里的hessian-lite进行序列化。
> 
通过实现IInternalProtocol接口，可以自定义server与client之间通信协议，框架默认使用的是DefaultInternalProtocol
>DefaultInternalProtocol协议格式如下：
> 
| 4byte | 8byte | 4byte | 4byte    | ?byte |
|-------|-------|-------|----------|-------|
| total | id    | type  | data_len | data  |
> 
### 4.自定义ReqHandler
实现RegHandler接口
```Java
public interface ReqHandler {
    InternalData handle(ChannelHandlerContext channelHandlerContext, InternalData internalData, HandlerAttributes handlerAttributes);

    int getReqType();
}
```
### 5.自定义CmdHandler
实现CmdHandler接口
```Java
public interface CmdHandler {
    void handle(ChannelHandlerContext channelHandlerContext, byte[] data, HandlerAttributes handlerAttributes);

    int getCmdType();

}
```
### 6.注册自定义的CmdHandler,ReqHandler
```Java
        serverBridgeBoostrap.addCmdHandler(new RegisterSupportBridge())//server
        .addCmdHandler(new ClientReadyHandler());
```
```Java
       clientBridgeBoostrap.addReqHandler(new SayHelloHandler());//client
```