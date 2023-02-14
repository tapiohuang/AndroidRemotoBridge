package org.xw0code.android_remote_beidge.client;

import com.google.gson.Gson;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.xw0code.android_remote_beidge.common.*;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ClientBridgeBoostrap implements IClientBridgeBoostrap {
    private final ClientBridge clientBridge =
            new ClientBridge();
    private String ip = "127.0.0.1";
    private int port = 23333;
    private ChannelFuture clientChannelFuture;
    private IBridgeProtocol bridgeProtocol;
    private IInternalProtocol internalProtocol;

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
    public void start() {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            log.info("connect to server");
                            ChannelPipeline p = ch.pipeline();
                            //p.addLast(new LoggingHandler(LogLevel.INFO));
                            p.addLast("encoder", new InternalEncoder(internalProtocol));
                            p.addLast("decoder", new InternalDecoder(internalProtocol));
                            p.addLast(new ClientInternalHandler(clientBridge));
                            p.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            p.addLast(new HeartBeatClientHandler());
                        }
                    });
            clientChannelFuture = bootstrap.connect(this.ip, this.port).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        log.info("connect to server success");
                        Gson gson = new Gson();
                        //join ,
                        StringBuilder supportBridgeClassNames = new StringBuilder();
                        for (String name : clientBridge.getSupportedBridgeClassName()) {
                            supportBridgeClassNames.append(name).append(",");
                        }
                        InternalData internalData = new InternalData(
                                0, InternalData.REG_SUPPORT_BRIDGE,
                                supportBridgeClassNames.toString().getBytes());
                        channelFuture.channel().writeAndFlush(internalData);
                    }
                }
            }).sync();
            clientChannelFuture.channel().closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    eventLoopGroup.shutdownGracefully();
                    log.info("reconnect after 15s");
                    Executors.newScheduledThreadPool(1).schedule(new Runnable() {
                        @Override
                        public void run() {
                            start();
                        }
                    }, 15, TimeUnit.SECONDS);
                }
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        if (clientChannelFuture != null) {
            clientChannelFuture.channel().close();
        }
    }


}
