package com.github.fmjsjx.libcommon.jwt.crypto;

import java.util.stream.Stream;

final class JwsCryptoAlgorithms {

    static final MacCryptoAlgorithm HS256 = new MacCryptoAlgorithm(CryptoAlgorithmNames.HS256, "HMAC using SHA-256", "HmacSHA256", true);
    static final MacCryptoAlgorithm HS384 = new MacCryptoAlgorithm(CryptoAlgorithmNames.HS384, "HMAC using SHA-384", "HmacSHA384", true);
    static final MacCryptoAlgorithm HS512 = new MacCryptoAlgorithm(CryptoAlgorithmNames.HS512, "HMAC using SHA-512", "HmacSHA512", true);

    static void registerAll() {
        Stream.of(HS256, HS384, HS512 /* append others here... */).forEach(CryptoAlgorithms::registerAlgorithm);
    }

    private JwsCryptoAlgorithms() {
    }

}
