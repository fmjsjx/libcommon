package com.github.fmjsjx.libcommon.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

import org.junit.jupiter.api.Test;

import com.github.fmjsjx.libcommon.util.DigestUtil.DigestAlgorithm;

public class DigestUtilTtest {

    @Test
    public void testMd5() {
        DigestUtil util = DigestAlgorithm.MD5.createUtil();
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update("This is a test text string!!!".getBytes());
            assertArrayEquals(digest.digest(), util.digest("This is a test text string!!!"));
            digest.reset();

            byte[] input = "This is a test text string!!!".getBytes();
            digest.update(input, 0, 11);
            assertArrayEquals(digest.digest(), util.digest(input, 0, 11));
            digest.reset();

            byte[] other1 = "Other string 1".getBytes();
            byte[] other2 = "Other string 2".getBytes();
            digest.update(input);
            digest.update(other1);
            digest.update(other2);
            assertArrayEquals(digest.digest(), util.digest(input, other1, other2));
            digest.reset();

            var buffer = ByteBuffer.wrap(input);
            var ob1 = ByteBuffer.wrap(other1);
            var ob2 = ByteBuffer.wrap(other2);
            digest.update(buffer);
            digest.update(ob1);
            digest.update(ob2);
            buffer.position(0);
            ob1.position(0);
            ob2.position(0);
            assertArrayEquals(digest.digest(), util.digest(buffer, ob1, ob2));
            digest.reset();

        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testSha1() {
        DigestUtil util = DigestAlgorithm.SHA1.createUtil();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update("This is a test text string!!!".getBytes());
            assertArrayEquals(digest.digest(), util.digest("This is a test text string!!!"));
            digest.reset();

            byte[] input = "This is a test text string!!!".getBytes();
            digest.update(input, 0, 11);
            assertArrayEquals(digest.digest(), util.digest(input, 0, 11));
            digest.reset();

            byte[] other1 = "Other string 1".getBytes();
            byte[] other2 = "Other string 2".getBytes();
            digest.update(input);
            digest.update(other1);
            digest.update(other2);
            assertArrayEquals(digest.digest(), util.digest(input, other1, other2));
            digest.reset();

            var buffer = ByteBuffer.wrap(input);
            var ob1 = ByteBuffer.wrap(other1);
            var ob2 = ByteBuffer.wrap(other2);
            digest.update(buffer);
            digest.update(ob1);
            digest.update(ob2);
            buffer.position(0);
            ob1.position(0);
            ob2.position(0);
            assertArrayEquals(digest.digest(), util.digest(buffer, ob1, ob2));
            digest.reset();

        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testSha256() {
        DigestUtil util = DigestAlgorithm.SHA256.createUtil();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update("This is a test text string!!!".getBytes());
            assertArrayEquals(digest.digest(), util.digest("This is a test text string!!!"));
            digest.reset();

            byte[] input = "This is a test text string!!!".getBytes();
            digest.update(input, 0, 11);
            assertArrayEquals(digest.digest(), util.digest(input, 0, 11));
            digest.reset();

            byte[] other1 = "Other string 1".getBytes();
            byte[] other2 = "Other string 2".getBytes();
            digest.update(input);
            digest.update(other1);
            digest.update(other2);
            assertArrayEquals(digest.digest(), util.digest(input, other1, other2));
            digest.reset();

            var buffer = ByteBuffer.wrap(input);
            var ob1 = ByteBuffer.wrap(other1);
            var ob2 = ByteBuffer.wrap(other2);
            digest.update(buffer);
            digest.update(ob1);
            digest.update(ob2);
            buffer.position(0);
            ob1.position(0);
            ob2.position(0);
            assertArrayEquals(digest.digest(), util.digest(buffer, ob1, ob2));
            digest.reset();

        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testSha512() {
        DigestUtil util = DigestAlgorithm.SHA512.createUtil();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.update("This is a test text string!!!".getBytes());
            assertArrayEquals(digest.digest(), util.digest("This is a test text string!!!"));
            digest.reset();

            byte[] input = "This is a test text string!!!".getBytes();
            digest.update(input, 0, 11);
            assertArrayEquals(digest.digest(), util.digest(input, 0, 11));
            digest.reset();

            byte[] other1 = "Other string 1".getBytes();
            byte[] other2 = "Other string 2".getBytes();
            digest.update(input);
            digest.update(other1);
            digest.update(other2);
            assertArrayEquals(digest.digest(), util.digest(input, other1, other2));
            digest.reset();

            var buffer = ByteBuffer.wrap(input);
            var ob1 = ByteBuffer.wrap(other1);
            var ob2 = ByteBuffer.wrap(other2);
            digest.update(buffer);
            digest.update(ob1);
            digest.update(ob2);
            buffer.position(0);
            ob1.position(0);
            ob2.position(0);
            assertArrayEquals(digest.digest(), util.digest(buffer, ob1, ob2));
            digest.reset();

        } catch (Exception e) {
            fail(e);
        }
    }

}
