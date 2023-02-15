package org.xw0code.android_remote_beidge.common;

import java.util.concurrent.CompletableFuture;

public class InternalReqCompletableFuture<T> extends CompletableFuture<T> {
    private final Class<T> resClass;

    public InternalReqCompletableFuture(Class<T> resClass) {
        this.resClass = resClass;
    }

    public Class<T> getResClass() {
        return resClass;
    }

    public boolean complete(Object res) {
        return super.complete((T) res);
    }
}
