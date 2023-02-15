package org.xw0code.android_remote_beidge.client;

import io.netty.channel.ChannelHandlerContext;
import org.xw0code.android_remote_beidge.common.*;

public class SayHelloHandler implements ReqHandler {
    @Override
    public InternalData handle(ChannelHandlerContext channelHandlerContext, InternalData internalData, HandlerAttributes handlerAttributes) {
        byte[] payload = internalData.getData();
        byte[] helloBytes = ByteUtils.subBytes(payload, 4, payload.length - 4);
        String hello = ProtostuffUtil.deserializer(helloBytes, String.class);
        LogUtils.info("Client say hello: {}", hello);
        return new InternalData(internalData.getId(), InternalData.INTERNAL_RES, ProtostuffUtil.serializer("hellooooo"));
    }

    @Override
    public int getReqType() {
        return 1;
    }
}
