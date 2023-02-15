package org.xw0code.android_remote_beidge.common;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.HashMap;
import java.util.HashSet;

public class InternalReqHandler extends SimpleChannelInboundHandler<InternalData>
        implements HandlerAttributesSupport {
    private final HandlerAttributes handlerAttributes = new HandlerAttributes();

    private final HashMap<Integer, ReqHandler> regHandlerMap = new HashMap<>();

    public InternalReqHandler(HashSet<ReqHandler> reqHandlers) {
        for (ReqHandler reqHandler : reqHandlers) {
            registerRegHandler(reqHandler);
        }
    }

    public void registerRegHandler(ReqHandler regHandler) {
        regHandlerMap.put(regHandler.getReqType(), regHandler);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, InternalData internalData) throws Exception {
        if (internalData.getType() != InternalData.INTERNAL_REQ
                && internalData.getType() != InternalData.INTERNAL_RES) {
            channelHandlerContext.fireChannelRead(internalData);
            return;
        }
        if (internalData.getType() == InternalData.INTERNAL_RES) {
            onInternalRes(channelHandlerContext, internalData);
        } else {
            byte[] payload = internalData.getData();
            byte[] reqTypeBytes = ByteUtils.subBytes(payload, 0, 4);
            int reqType = ByteUtils.toInt(reqTypeBytes);
            ReqHandler regHandler = regHandlerMap.get(reqType);
            if (regHandler == null) {
                LogUtils.error("Unknown req type: {}", reqType);
                return;
            }
            channelHandlerContext.writeAndFlush(regHandler.handle(channelHandlerContext, internalData, handlerAttributes));

        }
    }

    private void onInternalRes(ChannelHandlerContext channelHandlerContext, InternalData internalData) {
        if (handlerAttributes.containsKey("server")) {
            ReqResInternalTunnel reqResInternalTunnel = handlerAttributes.getAttribute("server", ReqResInternalTunnel.class);
            reqResInternalTunnel.res(internalData);
        } else if (handlerAttributes.containsKey("client")) {
            ReqResInternalTunnel reqResInternalTunnel = handlerAttributes.getAttribute("client", ReqResInternalTunnel.class);
            reqResInternalTunnel.res(internalData);
        } else {
            LogUtils.info("could not res data");
        }
    }

    @Override
    public void addAttribute(String key, Object val) {
        this.handlerAttributes.addAttribute(key, val);
    }
}
