package org.xw0code.android_remote_beidge.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.xw0code.android_remote_beidge.common.*;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ClientBridgeBoostrap extends CommonBoostrap
        implements IClientBridgeBoostrap {
    private final ClientBridge clientBridge = new ClientBridge();
    private String ip = "127.0.0.1";
    private int port = 23333;
    private ChannelFuture clientChannelFuture;
    private IBridgeProtocol bridgeProtocol;
    private IInternalProtocol internalProtocol;
    private Server server;


    @Override
    public IClientBridgeBoostrap server(String ip, int port) {
        this.ip = ip;
        this.port = port;
        return this;
    }

    @Override
    public <T> IClientBridgeBoostrap registerBridge(Class<T> bridgeClazz, T bridgeImpl) {
        this.clientBridge.registerBridge(bridgeClazz, bridgeImpl);
        return this;
    }


    @Override
    public IClientBridgeBoostrap bridgeProtocol(IBridgeProtocol bridgeProtocol) {
        RuntimeContainer.BRIDGE_PROTOCOL = bridgeProtocol;
        this.bridgeProtocol = bridgeProtocol;
        return this;
    }

    @Override
    public IClientBridgeBoostrap internalProtocol(IInternalProtocol internalProtocol) {
        RuntimeContainer.INTERNAL_PROTOCOL = internalProtocol;
        this.internalProtocol = internalProtocol;
        return this;
    }


    @Override
    public Server start() {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            LogUtils.info("connect to server");
                            ChannelPipeline p = ch.pipeline();
                            if (RuntimeContainer.DEBUG) {
                                p.addLast(new LoggingHandler(LogLevel.INFO));
                            }
                            server = new Server(ch);
                            p.addLast("encoder", new InternalEncoder(internalProtocol));
                            p.addLast("decoder", new InternalDecoder(internalProtocol));
                            p.addLast(new ClientInvokeHandler(clientBridge));
                            p.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            p.addLast(new HeartBeatClientHandler());
                            InternalCmdHandler internalCmdHandler = new InternalCmdHandler(cmdHandlers);
                            internalCmdHandler.addAttribute("server", server);
                            p.addLast(internalCmdHandler);
                            InternalReqHandler internalReqHandler = new InternalReqHandler(reqHandlers);
                            internalReqHandler.addAttribute("server", server);
                            p.addLast(internalReqHandler);

                        }
                    });
            clientChannelFuture = bootstrap.connect(this.ip, this.port).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        LogUtils.info("connect to server success");
                        server.cmd(10, clientBridge.getSupportedBridgeClassName());
                    } else {
                        LogUtils.info("connect to server fail,reconnect after 15s");
                        Executors.newScheduledThreadPool(1).schedule(new Runnable() {
                            @Override
                            public void run() {
                                start();
                            }
                        }, 15, TimeUnit.SECONDS);
                    }
                }
            }).sync();
            clientChannelFuture.channel().closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    eventLoopGroup.shutdownGracefully();
                    LogUtils.info("reconnect after 15s");
                    Executors.newScheduledThreadPool(1).schedule(new Runnable() {
                        @Override
                        public void run() {
                            start();
                        }
                    }, 15, TimeUnit.SECONDS);
                }
            });
            return server;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public IClientBridgeBoostrap debug(boolean debug) {
        RuntimeContainer.DEBUG = debug;
        return this;
    }

    @Override
    public void close() {
        if (clientChannelFuture != null) {
            clientChannelFuture.channel().close();
        }
    }


}
