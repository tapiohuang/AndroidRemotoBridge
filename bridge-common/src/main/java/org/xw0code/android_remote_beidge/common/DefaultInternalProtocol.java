package org.xw0code.android_remote_beidge.common;

import io.netty.buffer.ByteBuf;

public class DefaultInternalProtocol implements IInternalProtocol {
    @Override
    public byte[] serializeInternalData(InternalData internalData) {
        int dataLength = internalData.getData().length;
        int totalLength = Integer.BYTES + Long.BYTES + Integer.BYTES + Integer.BYTES + dataLength;
        byte[] bytes = new byte[Integer.BYTES + Long.BYTES + Integer.BYTES + Integer.BYTES + dataLength];
        int offset = 0;
        System.arraycopy(ByteUtils.fromInt(totalLength), 0, bytes, offset, Integer.BYTES);
        offset += Integer.BYTES;
        System.arraycopy(ByteUtils.fromLong(internalData.getId()), 0, bytes, offset, Long.BYTES);
        offset += Long.BYTES;
        System.arraycopy(ByteUtils.fromInt(internalData.getType()), 0, bytes, offset, Integer.BYTES);
        offset += Integer.BYTES;
        System.arraycopy(ByteUtils.fromInt(dataLength), 0, bytes, offset, Integer.BYTES);
        offset += Integer.BYTES;
        System.arraycopy(internalData.getData(), 0, bytes, offset, dataLength);
        return bytes;
    }

    @Override
    public InternalData deserializeInternalData(ByteBuf byteBuf) {
        if (byteBuf.readableBytes() < Integer.BYTES) {
            return null;
        }
        int totalLength = byteBuf.readInt();
        int payloadLength = totalLength - Integer.BYTES;
        if (byteBuf.readableBytes() < payloadLength) {
            return null;
        }
        long id = byteBuf.readLong();
        int type = byteBuf.readInt();
        int dataLength = byteBuf.readInt();
        byte[] data = new byte[dataLength];
        byteBuf.readBytes(data);
        return new InternalData(id, type, data);
    }
}
