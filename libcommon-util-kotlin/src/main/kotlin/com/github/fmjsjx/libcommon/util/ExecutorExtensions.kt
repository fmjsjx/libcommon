package com.github.fmjsjx.libcommon.util

import kotlinx.coroutines.*
import kotlinx.coroutines.future.future
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

/**
 * Starts a new coroutine and returns its result as an implementation of [CompletableFuture].
 * The running coroutine is cancelled when the resulting future is cancelled or otherwise completed.
 *
 * @param scope the scope for new coroutines, The default value is [GlobalScope].
 * @param start coroutine start option. The default value is [CoroutineStart.DEFAULT].
 * @param block the coroutine code.
 * @return a [CompletableFuture]
 *
 * @author MJ Fang
 * @since 3.10
 */
@OptIn(DelicateCoroutinesApi::class)
fun <E : Executor, T> E.future(
    scope: CoroutineScope = GlobalScope,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T,
): CompletableFuture<T> = scope.future(asCoroutineDispatcher(), start, block)

/**
 * Launches a new coroutine without blocking the current thread and returns a reference to the coroutine as a [Job].
 * The coroutine is cancelled when the resulting job is [cancelled][Job.cancel].
 *
 * @param scope the scope for new coroutines, The default value is [GlobalScope].
 * @param start coroutine start option. The default value is [CoroutineStart.DEFAULT].
 * @param block the coroutine code which will be invoked in the context of the provided scope.
 * @return a [Job]
 *
 * @author MJ Fang
 * @since 3.10
 */
@OptIn(DelicateCoroutinesApi::class)
fun <E : Executor> E.launch(
    scope: CoroutineScope = GlobalScope,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit,
): Job = scope.launch(asCoroutineDispatcher(), start, block)

/**
 * Creates a coroutine and returns its future result as an implementation of [Deferred].
 * The running coroutine is cancelled when the resulting deferred is [cancelled][Job.cancel].
 *
 * @param scope the scope for new coroutines, The default value is [GlobalScope].
 * @param start coroutine start option. The default value is [CoroutineStart.DEFAULT].
 * @param block the coroutine code which will be invoked in the context of the provided scope.
 * @return a [Deferred]
 *
 * @author MJ Fang
 * @since 3.10
 */
@OptIn(DelicateCoroutinesApi::class)
fun <E : Executor, T> E.async(
    scope: CoroutineScope = GlobalScope,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T,
): Deferred<T> = scope.async(asCoroutineDispatcher(), start, block)
