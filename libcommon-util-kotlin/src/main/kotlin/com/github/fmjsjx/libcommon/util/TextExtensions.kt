package com.github.fmjsjx.libcommon.util

import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.Charset

val <T : CharSequence> T.isNumberic: Boolean get() = StringUtil.isNumberic(toString())

fun <T : CharSequence> T.urlEncode(charset: Charset = Charsets.UTF_8): String = URLEncoder.encode(toString(), charset)

fun <T : CharSequence> T.urlDecode(charset: Charset = Charsets.UTF_8): String = URLDecoder.decode(toString(), charset)
