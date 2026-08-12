package com.github.fmjsjx.libcommon.util.kotlin;

import static org.junit.jupiter.api.Assertions.*;

import kotlin.Unit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class KotlinReflectionUtilTests {

    @Test
    public void testIsSupportedKotlinClass() throws Exception {
        Assertions.assertTrue(KotlinReflectionUtil.isSupportedKotlinClass(TestClass.class));
        Assertions.assertTrue(KotlinReflectionUtil.isSupportedKotlinClass(TestData.class));
        Assertions.assertTrue(KotlinReflectionUtil.isSupportedKotlinClass(TestClass2.class));
        Assertions.assertTrue(KotlinReflectionUtil.isSupportedKotlinClass(TestData2.class));
        Assertions.assertFalse(KotlinReflectionUtil.isSupportedKotlinClass(KotlinReflectionUtilTests.class));
        var testKt = Class.forName("com.github.fmjsjx.libcommon.util.kotlin.TestKt");
        Assertions.assertFalse(KotlinReflectionUtil.isSupportedKotlinClass(testKt));
    }

    @Test
    public void testIsDataClass() {
        Assertions.assertTrue(KotlinReflectionUtil.isDataClass(TestData.class));
        Assertions.assertTrue(KotlinReflectionUtil.isDataClass(TestData2.class));
        Assertions.assertFalse(KotlinReflectionUtil.isDataClass(TestClass.class));
        Assertions.assertFalse(KotlinReflectionUtil.isDataClass(TestClass2.class));
    }

    @Test
    public void testFindKotlinFunction() throws Exception {
        var showName = Arrays.stream(TestClass.class.getMethods())
                .filter(it -> it.getName().equals("showName")).findFirst().orElseThrow();
        var kShowName = KotlinReflectionUtil.findKotlinFunction(showName);
        assertTrue(kShowName.isPresent());

        var suspendingName = Arrays.stream(TestClass.class.getMethods())
                .filter(it -> it.getName().equals("suspendingName")).findFirst().orElseThrow();
        var kSuspendingName = KotlinReflectionUtil.findKotlinFunction(suspendingName);
        assertTrue(kSuspendingName.isPresent());

        var testKt = Class.forName("com.github.fmjsjx.libcommon.util.kotlin.TestKt");
        Assertions.assertTrue(KotlinReflectionUtil.findKotlinFunction(Arrays.stream(testKt.getMethods())
                .filter(it -> it.getName().equals("helloWorld")).findFirst().orElseThrow()).isPresent());
    }

    @Test
    public void testIsSuspend() throws Exception {
        var showName = Arrays.stream(TestClass.class.getMethods())
                .filter(it -> it.getName().equals("showName")).findFirst().orElseThrow();
        Assertions.assertFalse(KotlinReflectionUtil.isSuspend(showName));
        var suspendingName = Arrays.stream(TestClass.class.getMethods())
                .filter(it -> it.getName().equals("suspendingName")).findFirst().orElseThrow();
        Assertions.assertTrue(KotlinReflectionUtil.isSuspend(suspendingName));

        Assertions.assertFalse(KotlinReflectionUtil.isSuspend(getClass().getMethod("testIsDataClass")));
    }

    @Test
    public void testGetReturnType() throws Exception {
        var showName = Arrays.stream(TestClass.class.getMethods())
                .filter(it -> it.getName().equals("showName")).findFirst().orElseThrow();
        Assertions.assertEquals(String.class, KotlinReflectionUtil.getReturnType(showName));

        var suspendingName = Arrays.stream(TestClass.class.getMethods())
                .filter(it -> it.getName().equals("suspendingName")).findFirst().orElseThrow();
        Assertions.assertEquals(String.class, KotlinReflectionUtil.getReturnType(suspendingName));

        var testKt = Class.forName("com.github.fmjsjx.libcommon.util.kotlin.TestKt");
        var helloWorld = Arrays.stream(testKt.getMethods())
                .filter(it -> it.getName().equals("helloWorld")).findFirst().orElseThrow();
        Assertions.assertEquals(Unit.class, KotlinReflectionUtil.getReturnType(helloWorld));
    }

}
