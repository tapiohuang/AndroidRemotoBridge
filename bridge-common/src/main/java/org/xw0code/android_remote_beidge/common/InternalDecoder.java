package org.xw0code.android_remote_beidge.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class InternalDecoder extends ByteToMessageDecoder {
    private IInternalProtocol internalProtocol;

    public InternalDecoder(IInternalProtocol internalProtocol) {
        this.internalProtocol = internalProtocol;
    }

    public InternalDecoder() {
        this.internalProtocol = RuntimeContainer.INTERNAL_PROTOCOL;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        InternalData internalData = this.internalProtocol.deserializeInternalData(byteBuf);
        if (internalData != null) {
            list.add(internalData);
        } else {
            byteBuf.resetReaderIndex();
        }
    }
}
