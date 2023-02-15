package org.xw0code.android_remote_beidge.common;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.jar.Attributes;

public class InternalCmdHandler
        extends SimpleChannelInboundHandler<InternalData>
        implements HandlerAttributesSupport {
    private final HashMap<Integer, CmdHandler> cmdHandlerMap = new HashMap<>();

    private final HandlerAttributes attributes = new HandlerAttributes();

    public InternalCmdHandler(HashSet<CmdHandler> cmdHandlers) {
        for (CmdHandler cmdHandler : cmdHandlers) {
            registerCmdHandler(cmdHandler);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, InternalData internalData) throws Exception {
        if (internalData.getType() != InternalData.INTERNAL_CMD) {
            channelHandlerContext.fireChannelRead(internalData);
            return;
        }
        byte[] payload = internalData.getData();
        byte[] cmdTypeBytes = ByteUtils.subBytes(payload, 0, 4);
        int cmdType = ByteUtils.toInt(cmdTypeBytes);
        CmdHandler cmdHandler = cmdHandlerMap.get(cmdType);
        if (cmdHandler == null) {
            LogUtils.error("Unknown cmd type: {}", cmdType);
            return;
        }
        byte[] data = ByteUtils.subBytes(payload, 4, payload.length - 4);
        cmdHandler.handle(channelHandlerContext, data, attributes);
    }

    public void addAttribute(String key, Object value) {
        attributes.addAttribute(key, value);
    }


    public void registerCmdHandler(CmdHandler cmdHandler) {
        cmdHandlerMap.put(cmdHandler.getCmdType(), cmdHandler);
    }
}
