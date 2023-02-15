package org.xw0code.android_remote_beidge.common;

import java.util.HashSet;

public abstract class CommonBoostrap {
    protected final HashSet<ReqHandler> reqHandlers = new HashSet<>();
    protected final HashSet<CmdHandler> cmdHandlers = new HashSet<>();

    public CommonBoostrap addReqHandler(ReqHandler reqHandler) {
        this.reqHandlers.add(reqHandler);
        return this;
    }

    public CommonBoostrap addCmdHandler(CmdHandler cmdHandler) {
        this.cmdHandlers.add(cmdHandler);
        return this;
    }
}
