package org.xw0code.android_remote_beidge.client;

import io.netty.channel.Channel;
import org.xw0code.android_remote_beidge.common.ReqResInternalTunnel;

public class Server extends ReqResInternalTunnel {
    protected Server(Channel channel) {
        super(channel);
    }
}
