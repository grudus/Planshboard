package com.grudus.planshboard.configuration.security.token

import com.fasterxml.jackson.databind.ObjectMapper
import com.grudus.planshboard.configuration.security.AuthenticatedUser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.SignatureAlgorithm.HS512
import io.jsonwebtoken.impl.DefaultClaims
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.collections.LinkedHashMap

class TokenHandler(private val key: ByteArray,
                   private val tokenExpireSeconds: Long = ONE_DAY_IN_SECONDS,
                   private val algorithm: SignatureAlgorithm = HS512) {

    private val jsonReader = ObjectMapper()

    fun createTokenForUser(user: AuthenticatedUser): String {
        val claims = DefaultClaims()
                .setExpiration(expirationDate())
                .apply { set(USER_KEY, JwtUser.fromAuthenticatedUser(user)) }

        return Jwts.builder()
                .signWith(algorithm, key)
                .setClaims(claims)
                .compact()
    }

    fun parseToken(token: String): AuthenticatedUser {
        val user: LinkedHashMap<*, *> = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .body[USER_KEY, LinkedHashMap::class.java]

        return jsonReader.convertValue(user, JwtUser::class.java)
                .let { jwtUser -> AuthenticatedUser(jwtUser) }
    }


    private fun TokenHandler.expirationDate(): Date =
            LocalDateTime.now()
                    .plusSeconds(tokenExpireSeconds)
                    .atZone(ZoneOffset.systemDefault()).toInstant()
                    .let { instant -> Date.from(instant) }


    companion object {
        private const val USER_KEY = "user"
        private const val ONE_DAY_IN_SECONDS: Long = 60 * 60 * 24
    }
}