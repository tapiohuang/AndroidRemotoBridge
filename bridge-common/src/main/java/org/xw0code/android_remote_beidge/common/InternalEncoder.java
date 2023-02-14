package org.xw0code.android_remote_beidge.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InternalEncoder extends MessageToByteEncoder<InternalData> {
    private IInternalProtocol internalProtocol;

    public InternalEncoder(IInternalProtocol internalProtocol) {
        this.internalProtocol = internalProtocol;
    }

    public InternalEncoder() {
        this.internalProtocol = RuntimeContainer.INTERNAL_PROTOCOL;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, InternalData internalData, ByteBuf byteBuf) throws Exception {
        byte[] data = this.internalProtocol.serializeInternalData(internalData);
        //log.info("Send internal data: {}", ByteUtils.toHexString(data));
        byteBuf.writeBytes(data);
    }
}
