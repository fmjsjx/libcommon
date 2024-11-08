package com.github.fmjsjx.libcommon.util

import kotlinx.coroutines.*
import kotlinx.coroutines.future.future
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

@OptIn(DelicateCoroutinesApi::class)
fun <E : Executor, T> E.future(
    scope: CoroutineScope = GlobalScope,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T,
): CompletableFuture<T> = scope.future(asCoroutineDispatcher(), start, block)

@OptIn(DelicateCoroutinesApi::class)
fun <E : Executor> E.launch(
    scope: CoroutineScope = GlobalScope,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit,
): Job = scope.launch(asCoroutineDispatcher(), start, block)

@OptIn(DelicateCoroutinesApi::class)
fun <E: Executor, T> E.async(
    scope: CoroutineScope = GlobalScope,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T,
): Deferred<T> = scope.async(asCoroutineDispatcher(), start, block)
