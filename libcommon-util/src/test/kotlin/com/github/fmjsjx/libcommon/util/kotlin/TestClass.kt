package com.github.fmjsjx.libcommon.util.kotlin

import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Suppress("MemberVisibilityCanBePrivate", "unused")
class TestClass(
    val name: String = "",
) {

    fun showName(): String = "name: '$name'"

    suspend fun suspendingName(millis: Long): String {
        delay(millis.milliseconds)
        return showName()
    }

}