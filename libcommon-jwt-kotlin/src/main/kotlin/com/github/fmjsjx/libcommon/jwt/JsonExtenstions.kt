package com.github.fmjsjx.libcommon.jwt

/**
 * Parse this JSON byte array to a [JoseHeader] instance.
 *
 * @param factory the [JsonRepresentedFactory]
 * @return a `JoseHeader` instance
 * @author MJ Fang
 * @since 3.10
 */
fun ByteArray.parseToJoseHeader(
    factory: JsonRepresentedFactory<*> = Fastjson2JsonRepresented.getFactory(),
): JoseHeader = JoseHeader.parse(this, factory)

/**
 * Parse this JSON byte array to a [JwtClaimsSet] instance.
 *
 * @param factory the [JsonRepresentedFactory]
 * @return a `JwtClaimsSet` instance
 * @author MJ Fang
 * @since 3.10
 */
fun ByteArray.parseToJwtClaimsSet(
    factory: JsonRepresentedFactory<*> = Fastjson2JsonRepresented.getFactory()
): JwtClaimsSet = JwtClaimsSet.parse(this, factory)
