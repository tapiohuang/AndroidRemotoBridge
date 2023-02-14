package org.xw0code.android_remote_beidge.common;

import io.netty.buffer.ByteBuf;

public interface IInternalProtocol {

    byte[] serializeInternalData(InternalData internalData);

    InternalData deserializeInternalData(ByteBuf byteBuf);
}
