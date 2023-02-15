package org.xw0code.android_remote_beidge.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.xw0code.android_remote_beidge.common.*;

import java.util.concurrent.ThreadFactory;


public class ServerBridgeBoostrap extends CommonBoostrap
        implements IServerBridgeBootstrap {
    private final ServerBridge serverBridge = new ServerBridge();
    private int port = 23333;
    private IBridgeProtocol bridgeProtocol;
    private IInternalProtocol internalProtocol;
    private ChannelFuture serverChannelFuture;


    @Override
    public IServerBridgeBootstrap port(int port) {
        this.port = port;
        return this;
    }


    @Override
    public IServerBridgeBootstrap bridgeProtocol(IBridgeProtocol bridgeProtocol) {
        this.bridgeProtocol = bridgeProtocol;
        RuntimeContainer.BRIDGE_PROTOCOL = bridgeProtocol;
        return this;
    }

    @Override
    public IServerBridgeBootstrap internalProtocol(IInternalProtocol internalProtocol) {
        this.internalProtocol = internalProtocol;
        RuntimeContainer.INTERNAL_PROTOCOL = internalProtocol;
        return this;
    }

    @Override
    public synchronized IServerBridgeBootstrap registerBridge(Class<?> bridgeClass) {
        SupportBridgeClientManagerContainer.getInstance().registerBridge(bridgeClass);
        return this;
    }

    @Override
    public void close() {
        if (serverChannelFuture != null) {
            serverChannelFuture.channel().close();
        }
    }

    @Override
    public IServerBridge start() {
        if (bridgeProtocol == null || internalProtocol == null) {
            throw new RuntimeException("bridgeProtocol or internalProtocol is null");
        }
        //start netty server
        EventLoopGroup bossGroup = new NioEventLoopGroup(1, new ThreadFactory() {
            int i = 0;

            @Override
            public synchronized Thread newThread(Runnable r) {
                return new Thread(r, "boss-thread" + i++);
            }

        });
        EventLoopGroup workerGroup = new NioEventLoopGroup(new ThreadFactory() {
            int i = 0;

            @Override
            public synchronized Thread newThread(Runnable r) {
                return new Thread(r, "worker-thread" + i++);
            }
        });
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            LogUtils.info("new client connected");
                            Client client = new Client(ch);
                            ChannelPipeline p = ch.pipeline();
                            if (RuntimeContainer.DEBUG) {
                                p.addLast(new LoggingHandler(LogLevel.INFO));
                            }
                            p.addLast("encoder", new InternalEncoder(internalProtocol));
                            p.addLast("decoder", new InternalDecoder(internalProtocol));
                            p.addLast(new ServerInvokeHandler(client));
                            p.addLast(new IdleStateHandler(0, 0, 30));
                            InternalCmdHandler internalCmdHandler = new InternalCmdHandler(cmdHandlers);
                            internalCmdHandler.addAttribute("client", client);
                            p.addLast(internalCmdHandler);
                            InternalReqHandler internalReqHandler = new InternalReqHandler(reqHandlers);
                            internalReqHandler.addAttribute("client",client);
                            p.addLast(internalReqHandler);
                            p.addLast(new ServerIdleHandler(client));
                            serverBridge.registerClient(client);
                        }
                    });
            this.serverChannelFuture = b.bind(port).sync();
            LogUtils.info("server started at port {}", port);
            return this.serverBridge;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IServerBridgeBootstrap debug(boolean debug) {
        RuntimeContainer.DEBUG = debug;
        return this;
    }

}
