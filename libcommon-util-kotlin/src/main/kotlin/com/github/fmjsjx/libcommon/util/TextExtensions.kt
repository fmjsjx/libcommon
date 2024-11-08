package com.github.fmjsjx.libcommon.util

import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.Charset

/**
 * Returns `true` if the [CharSequence] contains only Unicode digits, otherwise `false`.
 *
 * A decimal point is not a Unicode digit and returns `false`.
 *
 * `null` will return `false`. An empty CharSequence (length()=0) will return `false`.
 *
 * @author MJ Fang
 * @since 3.10
 */
val <T : CharSequence> T.isNumberic: Boolean get() = StringUtil.isNumberic(toString())

/**
 * Translates a [CharSequence] into `application/x-www-form-urlencoded` format using a specific [Charset].
 *
 * @param charset the given charset, the default value is [Charsets.UTF_8].
 * @return the translated [String].
 *
 * @author MJ Fang
 * @since 3.10
 */
fun <T : CharSequence> T.urlEncode(charset: Charset = Charsets.UTF_8): String = URLEncoder.encode(toString(), charset)

/**
 * Decodes an `application/x-www-form-urlencoded` [CharSequence] using a specific [Charset].
 *
 * @param charset the given charset, the default value is [Charsets.UTF_8].
 * @return the newly decoded [String].
 *
 * @author MJ Fang
 * @since 3.10
 */
fun <T : CharSequence> T.urlDecode(charset: Charset = Charsets.UTF_8): String = URLDecoder.decode(toString(), charset)
