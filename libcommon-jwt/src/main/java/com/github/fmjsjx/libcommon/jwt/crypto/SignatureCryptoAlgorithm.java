package com.github.fmjsjx.libcommon.jwt.crypto;

import com.github.fmjsjx.libcommon.util.concurrent.EasyThreadLocal;

import java.security.*;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;

/**
 * The implementation of {@link JwsCryptoAlgorithm} for Digital
 * Signatures.
 *
 * @author MJ Fang
 * @since 3.10
 */
final class SignatureCryptoAlgorithm extends AbstractCryptoAlgorithm implements JwsCryptoAlgorithm {

    static SignatureCryptoAlgorithm createRS(int digestBitLength) {
        var jcaName = "SHA" + digestBitLength + "withRSA";
        return new SignatureCryptoAlgorithm(
                "RS" + digestBitLength,
                "RSASSA-PKCS1-v1_5 using SHA" + digestBitLength,
                jcaName,
                true,
                new RsaSignatureProvider(jcaName));
    }

    static SignatureCryptoAlgorithm createPS(int digestBitLength) {
        return new SignatureCryptoAlgorithm(
                "PS" + digestBitLength,
                "RSASSA-PSS using SHA-" + digestBitLength + " and MGF1 with SHA-" + digestBitLength,
                "SHA" + digestBitLength + "withRSAandMGF1",
                true,
                new PssSignatureProvider(digestBitLength));
    }

    private final SignatureProvider signatureProvider;

    SignatureCryptoAlgorithm(String name, String description, String jcaName, boolean jdkStandard, SignatureProvider signatureProvider) {
        super(name, description, jcaName, jdkStandard);
        this.signatureProvider = signatureProvider;
    }

    @Override
    public boolean isMac() {
        return false;
    }

    @Override
    public SignatureProvider getSignatureProvider() {
        return signatureProvider;
    }

    private static class RsaSignatureProvider implements SignatureProvider {

        private static final KeyFactory RSA_KEY_FACTORY;

        static {
            try {
                RSA_KEY_FACTORY = KeyFactory.getInstance("RSA");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        private final String algorithm;
        private final EasyThreadLocal<Signature> threadLocalSignature = EasyThreadLocal.create(() -> {
            try {
                return SignatureProvider.super.getInstance();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        });

        private RsaSignatureProvider(String algorithm) {
            this.algorithm = algorithm;
        }


        @Override
        public String getAlgorithm() {
            return algorithm;
        }

        @Override
        public KeyFactory getKeyFactory() {
            return RSA_KEY_FACTORY;
        }

        @Override
        public Signature getInstance() throws NoSuchAlgorithmException {
            try {
                return threadLocalSignature.get();
            } catch (RuntimeException e) {
                if (e.getCause() != null && e.getCause() instanceof NoSuchAlgorithmException nsa) {
                    throw nsa;
                }
                throw e;
            }
        }

    }


    private static class PssSignatureProvider implements SignatureProvider {

        private static final String ALGORITHM = "RSASSA-PSS";

        private static final KeyFactory PSS_KEY_FACTORY;

        static {
            try {
                PSS_KEY_FACTORY = KeyFactory.getInstance(ALGORITHM);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        private final PSSParameterSpec parameterSpec;

        private final EasyThreadLocal<Signature> threadLocalSignature = EasyThreadLocal.create(() -> {
            try {
                var signature = SignatureProvider.super.getInstance();
                signature.setParameter(getParameterSpec());
                return signature;
            } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
                throw new RuntimeException(e);
            }
        });

        private PssSignatureProvider(int digestBitLength) {
            var mdName = "SHA-" + digestBitLength;
            parameterSpec = new PSSParameterSpec(mdName, "MGF1", new MGF1ParameterSpec(mdName), digestBitLength / Byte.SIZE, 1);
        }

        private PSSParameterSpec getParameterSpec() {
            return parameterSpec;
        }

        @Override
        public String getAlgorithm() {
            return ALGORITHM;
        }

        @Override
        public KeyFactory getKeyFactory() {
            return PSS_KEY_FACTORY;
        }

        @Override
        public Signature getInstance() throws NoSuchAlgorithmException {
            try {
                return threadLocalSignature.get();
            } catch (RuntimeException e) {
                if (e.getCause() != null) {
                    if (e.getCause() instanceof NoSuchAlgorithmException nsa) {
                        throw nsa;
                    }
                }
                throw e;
            }
        }

    }

}
