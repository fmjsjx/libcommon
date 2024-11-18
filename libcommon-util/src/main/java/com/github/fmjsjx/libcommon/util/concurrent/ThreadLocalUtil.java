package com.github.fmjsjx.libcommon.util.concurrent;

import com.github.fmjsjx.libcommon.util.ReflectUtil;

class ThreadLocalUtil {

    static final boolean FAST_THREAD_LOCAL_AVAILABLE;

    static {
        FAST_THREAD_LOCAL_AVAILABLE = ReflectUtil.findForName("io.netty.util.concurrent.FastThreadLocal").isPresent();
    }

    static boolean isFastThreadLocalAvailable() {
        return FAST_THREAD_LOCAL_AVAILABLE;
    }

    private ThreadLocalUtil() {
    }

}
