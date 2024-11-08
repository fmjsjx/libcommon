package com.github.fmjsjx.libcommon.util

import java.util.Base64

fun ByteArray.encodeToBase64String(urlSafe: Boolean = false, withoutPadding: Boolean = false): String =
    Base64Util.encoder(urlSafe, withoutPadding).encodeToString(this)

fun ByteArray.encodeToBase64(urlSafe: Boolean = false, withoutPadding: Boolean = false): ByteArray =
    Base64Util.encoder(urlSafe, withoutPadding).encode(this)

fun String.decodeBase64(urlSafe: Boolean = false): ByteArray =
    if (urlSafe) {
        Base64.getUrlDecoder()
    } else {
        Base64.getDecoder()
    }.decode(this)

fun ByteArray.decodeBase64(urlSafe: Boolean = false): ByteArray = if (urlSafe) {
    Base64.getUrlDecoder()
} else {
    Base64.getDecoder()
}.decode(this)
