package com.github.fmjsjx.libcommon.jwt.crypto;

import java.util.stream.Stream;

final class JwsCryptoAlgorithms {

    static final MacCryptoAlgorithm HS256 = MacCryptoAlgorithm.createHS(256);
    static final MacCryptoAlgorithm HS384 = MacCryptoAlgorithm.createHS(384);
    static final MacCryptoAlgorithm HS512 = MacCryptoAlgorithm.createHS(512);

    static final SignatureCryptoAlgorithm RS256 = SignatureCryptoAlgorithm.createRS(256);
    static final SignatureCryptoAlgorithm RS384 = SignatureCryptoAlgorithm.createRS(384);
    static final SignatureCryptoAlgorithm RS512 = SignatureCryptoAlgorithm.createRS(512);

    static final SignatureCryptoAlgorithm PS256 = SignatureCryptoAlgorithm.createPS(256);
    static final SignatureCryptoAlgorithm PS384 = SignatureCryptoAlgorithm.createPS(384);
    static final SignatureCryptoAlgorithm PS512 = SignatureCryptoAlgorithm.createPS(512);

    static void registerAll() {
        Stream.of(HS256, HS384, HS512, RS256, RS384, RS512, /* append ES??? here... */ PS256, PS384, PS512).forEach(CryptoAlgorithms::registerAlgorithm);
    }

    private JwsCryptoAlgorithms() {
    }

}
