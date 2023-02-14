package org.xw0code.android_remote_beidge.common;

import com.alibaba.com.caucho.hessian.io.Hessian2Input;
import com.alibaba.com.caucho.hessian.io.Hessian2Output;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @Author lcx
 * @Description
 * @Date 2022/8/8  17:28
 */
public class ProtostuffUtil {


    public static <T> byte[] serializer(T o) {
        try {
            final ByteArrayOutputStream bout = new ByteArrayOutputStream();
            final Hessian2Output hessian2Output = new Hessian2Output(bout);
            hessian2Output.writeObject(o);
            hessian2Output.flush();
            return bout.toByteArray();
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static <T> T deserializer(byte[] bytes, Class<T> clazz) {
        try {
            final ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
            final Hessian2Input hessian2Input = new Hessian2Input(bin);
            return (T) hessian2Input.readObject();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new RuntimeException(throwable);
        }
    }
}


