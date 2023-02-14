package org.xw0code.android_remote_beidge.client;

import lombok.extern.slf4j.Slf4j;
import org.xw0code.android_remote_beidge.common.TestRpcService;

@Slf4j
public class TestRpcServiceImpl implements TestRpcService {
    @Override
    public String encryptHttpSign(String httpSign) {
        //log.info("encryptHttpSign:{}", httpSign);
        return "encryptHttpSign";
    }
}
