package org.xw0code.android_remote_beidge.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.xw0code.android_remote_beidge.common.*;


@Slf4j
public class ServerBridgeBoostrap implements IServerBridgeBootstrap {
    private int port = 23333;
    private IBridgeProtocol bridgeProtocol;
    private IInternalProtocol internalProtocol;

    private ChannelFuture serverChannelFuture;
    private final ServerBridge serverBridge = new ServerBridge();

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
        this.serverBridge.registerBridge(bridgeClass);
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
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            log.info("new client connected");
                            ChannelPipeline p = ch.pipeline();
                            //p.addLast(new LoggingHandler(LogLevel.INFO));
                            p.addLast("encoder", new InternalEncoder(internalProtocol));
                            p.addLast("decoder", new InternalDecoder(internalProtocol));
                            p.addLast(new ServerInternalHandler(serverBridge));
                            serverBridge.registerClient(new Client(ch));
                        }
                    });
            this.serverChannelFuture = b.bind(port).sync();
            log.info("server started at port {}", port);
            return this.serverBridge;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
