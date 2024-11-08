package com.github.fmjsjx.libcommon.util

import java.util.Base64

/**
 * Encodes the specified byte array into a string using the `Base64` encoding scheme.
 *
 * @param urlSafe URL and Filename safe if `true`, the default value is `false`.
 * @param withoutPadding without adding any padding character at the end of the encoded byte data if `true`, the default value is `false`.
 * @return A string containing the resulting Base64 encoded characters
 *
 * @author MJ Fang
 * @since 3.10
 */
fun ByteArray.encodeToBase64String(urlSafe: Boolean = false, withoutPadding: Boolean = false): String =
    Base64Util.encoder(urlSafe, withoutPadding).encodeToString(this)

/**
 * Encodes all bytes from the specified byte array into a newly-allocated byte array using the `Base64` encoding scheme.
 *
 * @param urlSafe URL and Filename safe if `true`, the default value is `false`.
 * @param withoutPadding without adding any padding character at the end of the encoded byte data if `true`, the default value is `false`.
 * @return A newly-allocated byte array containing the resulting encoded bytes.
 *
 * @author MJ Fang
 * @since 3.10
 */
fun ByteArray.encodeToBase64(urlSafe: Boolean = false, withoutPadding: Boolean = false): ByteArray =
    Base64Util.encoder(urlSafe, withoutPadding).encode(this)

/**
 * Decodes a Base64 encoded String into a newly-allocated byte array using the `Base64` encoding scheme.
 *
 * @param urlSafe URL and Filename safe if `true`, the default value is `false`.
 * @return A newly-allocated byte array containing the decoded bytes.
 *
 * @author MJ Fang
 * @since 3.10
 */
fun String.decodeBase64(urlSafe: Boolean = false): ByteArray =
    if (urlSafe) {
        Base64.getUrlDecoder()
    } else {
        Base64.getDecoder()
    }.decode(this)

/**
 * Decodes all bytes from the input byte array using the `Base64` encoding scheme,
 * writing the results into a newly-allocated output byte array.
 *
 * @param urlSafe URL and Filename safe if `true`, the default value is `false`.
 * @return A newly-allocated byte array containing the decoded bytes.
 *
 * @author MJ Fang
 * @since 3.10
 */
fun ByteArray.decodeBase64(urlSafe: Boolean = false): ByteArray = if (urlSafe) {
    Base64.getUrlDecoder()
} else {
    Base64.getDecoder()
}.decode(this)
