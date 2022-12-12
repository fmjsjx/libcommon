package com.github.fmjsjx.libcommon.util.kotlin

import kotlinx.coroutines.delay

@Suppress("MemberVisibilityCanBePrivate", "unused")
class TestClass(
    val name: String = "",
) {

    fun showName(): String = "name: '$name'"

    suspend fun suspendingName(millis: Long): String {
        delay(millis)
        return showName()
    }

}