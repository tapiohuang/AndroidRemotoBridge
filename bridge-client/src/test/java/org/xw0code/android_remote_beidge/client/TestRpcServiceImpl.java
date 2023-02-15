package org.xw0code.android_remote_beidge.client;

import lombok.extern.slf4j.Slf4j;
import org.xw0code.android_remote_beidge.common.LogUtils;
import org.xw0code.android_remote_beidge.common.TestRpcService;


public class TestRpcServiceImpl implements TestRpcService {
    @Override
    public String encryptHttpSign(String str, String str1, String str2, String str3) {
        return "success";
    }
}
