package com.github.fmjsjx.libcommon.util;

import java.util.Base64;

/**
 * Utility class for {@code BASE64}.
 *
 * @since 3.10
 */
public final class Base64Util {

    private static final class Encoders {
        private static final Base64.Encoder RFC4648_PADDING = Base64.getEncoder();
        private static final Base64.Encoder RFC4648_NOPADDING = Base64.getEncoder().withoutPadding();
        private static final Base64.Encoder RFC4648_URLSAFE_PADDING = Base64.getUrlEncoder();
        private static final Base64.Encoder RFC4648_URLSAFE_NOPADDING = Base64.getUrlEncoder().withoutPadding();
    }

    /**
     * Chooses and returns the appropriate {@link Base64.Encoder} by the specified parameters given.
     *
     * @param urlSafe        URL and Filename safe if {@code true}
     * @param withoutPadding without adding any padding character at the end of the encoded byte data if {@code true}
     * @return an {@code Encoder}
     */
    public static Base64.Encoder encoder(boolean urlSafe, boolean withoutPadding) {
        return urlSafe
                ? (withoutPadding ? Encoders.RFC4648_URLSAFE_NOPADDING : Encoders.RFC4648_URLSAFE_PADDING)
                : (withoutPadding ? Encoders.RFC4648_NOPADDING : Encoders.RFC4648_PADDING);
    }

    private Base64Util() {
    }
}
