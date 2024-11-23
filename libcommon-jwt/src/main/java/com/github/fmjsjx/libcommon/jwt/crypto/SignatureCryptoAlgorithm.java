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

    static SignatureCryptoAlgorithm createES(int digestBitLength) {
        var jcaName = "SHA" + digestBitLength + "withECDSA";
        return new SignatureCryptoAlgorithm(
                "ES" + digestBitLength,
                "ECDSA using P-" + digestBitLength + " and SHA-" + digestBitLength,
                jcaName,
                true,
                new EcdsaSignatureProvider(jcaName));
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

    private static abstract class ThreadLocalSignatureProvider implements SignatureProvider {

        protected final EasyThreadLocal<Signature> threadLocalSignature = EasyThreadLocal.create(() -> {
            try {
                return getInstance();
            } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
                throw new RuntimeException(e);
            }
        });

        @Override
        public boolean verify(PublicKey publicKey, byte[] data, byte[] signature) throws NoSuchAlgorithmException,
                InvalidAlgorithmParameterException, InvalidKeyException, SignatureException {
            var sig = getThreadLocalSignature();
            sig.initVerify(publicKey);
            sig.update(data);
            return sig.verify(signature);
        }

        protected Signature getThreadLocalSignature() throws NoSuchAlgorithmException,
                InvalidAlgorithmParameterException {
            try {
                return threadLocalSignature.get();
            } catch (RuntimeException e) {
                if (e.getCause() != null) {
                    if (e.getCause() instanceof NoSuchAlgorithmException nsa) {
                        throw nsa;
                    } else if (e.getCause() instanceof InvalidAlgorithmParameterException iap) {
                        throw iap;
                    }
                }
                throw e;
            }
        }

        @Override
        public byte[] sign(PrivateKey privateKey, byte[] data) throws NoSuchAlgorithmException,
                InvalidAlgorithmParameterException, InvalidKeyException, SignatureException {
            var sig = getThreadLocalSignature();
            sig.initSign(privateKey);
            sig.update(data);
            return sig.sign();
        }
    }

    private static class RsaSignatureProvider extends ThreadLocalSignatureProvider implements SignatureProvider {

        private static final KeyFactory RSA_KEY_FACTORY;

        static {
            try {
                RSA_KEY_FACTORY = KeyFactory.getInstance("RSA");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        private final String algorithm;

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

    }

    private static class EcdsaSignatureProvider extends ThreadLocalSignatureProvider implements SignatureProvider {

        private static final KeyFactory ECDSA_KEY_FACTORY;

        static {
            try {
                ECDSA_KEY_FACTORY = KeyFactory.getInstance("EC");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        private final String algorithm;

        private EcdsaSignatureProvider(String algorithm) {
            this.algorithm = algorithm;
        }

        @Override
        public String getAlgorithm() {
            return algorithm;
        }

        @Override
        public KeyFactory getKeyFactory() {
            return ECDSA_KEY_FACTORY;
        }

    }

    private static class PssSignatureProvider extends ThreadLocalSignatureProvider implements SignatureProvider {

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
        public Signature getInstance() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
            var signature = super.getInstance();
            signature.setParameter(getParameterSpec());
            return signature;
        }

    }

}
