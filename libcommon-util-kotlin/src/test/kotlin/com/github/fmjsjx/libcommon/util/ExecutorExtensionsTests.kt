package com.github.fmjsjx.libcommon.util

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference

class ExecutorExtensionsTests {

    @Test
    fun testFuture() {
        val name: AtomicReference<String> = AtomicReference("")
        val executor = Executors.newSingleThreadExecutor()
        try {
            executor.execute { name.set(Thread.currentThread().name) }
            val f = executor.future {
                delay(1)
                Thread.currentThread().name.substringBefore(" @")
            }
            assertNotEquals("", name.get())
            assertEquals(name.get(), f.get())
        } finally {
            executor.shutdown()
        }
    }

    @Test
    fun testLaunch() {
        val name: AtomicReference<String> = AtomicReference("")
        val executor = Executors.newSingleThreadExecutor()
        try {
            executor.execute { name.set(Thread.currentThread().name) }
            var threadName = ""
            val job = executor.launch {
                delay(1)
                threadName = Thread.currentThread().name.substringBefore(" @")
            }
            runBlocking {
                job.join()
            }
            assertNotEquals("", name.get())
            assertEquals(name.get(), threadName)
        } finally {
            executor.shutdown()
        }
    }

    @Test
    fun testAsync() {
        val name: AtomicReference<String> = AtomicReference("")
        val executor = Executors.newSingleThreadExecutor()
        try {
            executor.execute { name.set(Thread.currentThread().name) }
            val deferred = executor.async {
                delay(1)
                Thread.currentThread().name.substringBefore(" @")
            }
            var threadName: String
            runBlocking {
                threadName = deferred.await()
            }
            assertNotEquals("", name.get())
            assertEquals(name.get(), threadName)
        } finally {
            executor.shutdown()
        }
    }

}