package com.github.fmjsjx.libcommon.jwt

/**
 * Parse this JSON byte array to a [JoseHeader] instance.
 *
 * @return a `JoseHeader` instance
 * @author MJ Fang
 * @since 3.10
 */
fun ByteArray.parseToJoseHeader(): JoseHeader = JoseHeader.parse(this)

/**
 * Parse this JSON byte array to a [JwtClaimsSet] instance.
 *
 * @return a `JwtClaimsSet` instance
 * @author MJ Fang
 * @since 3.10
 */
fun ByteArray.parseToJwtClaimsSet(): JwtClaimsSet = JwtClaimsSet.parse(this)
