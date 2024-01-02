package com.github.fmjsjx.libcommon.prometheus.client;

/**
 * Contains utilities to check if we are running inside or building for native image. Default behavior is to check
 * if specific for graalvm runtime property is present.
 */
class NativeImageChecker {
    static final boolean isGraalVmNativeImage = System.getProperty("org.graalvm.nativeimage.imagecode") != null;

    private NativeImageChecker() {}
}

