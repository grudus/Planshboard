package com.grudus.planshboard.configuration.security.token

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.grudus.planshboard.configuration.security.AuthenticatedUser
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureException
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.apache.commons.lang3.RandomUtils.nextLong
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.*

class TokenHandlerTest {

    private lateinit var key: ByteArray
    private lateinit var tokenHandler: TokenHandler

    @BeforeEach
    fun init() {
        key = randomAlphabetic(32).toByteArray()
        tokenHandler = TokenHandler(key)
    }

    @Test
    fun `should create valid JWT token`() {
        val token = tokenHandler.createTokenForUser(randomUser())

        val jwt = Jwts.parser().setSigningKey(key).parse(token)
        assertNotNull(jwt)
    }

    @Test
    fun `should parse token`() {
        val token = tokenHandler.createTokenForUser(randomUser())

        val user = tokenHandler.parseToken(token)
        assertNotNull(user)
    }

    @Test
    fun `should retrieve all user info from token`() {
        val user = randomUser()
        val token = tokenHandler.createTokenForUser(user)

        val tokenUser = tokenHandler.parseToken(token)

        assertEquals(user.id, tokenUser.id)
        assertEquals(user.userId, tokenUser.userId)
        assertEquals(user.name, tokenUser.name)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `should not be able to parse invalid token`() {
        val token = tokenHandler.createTokenForUser(randomUser())
        val tokenParts = token.split(".")

        val parsedUserString = String(Base64.getDecoder().decode(tokenParts[1]))
        val parsedUser: MutableMap<String, Any> = ObjectMapper().readValue(parsedUserString, object : TypeReference<MutableMap<String, Any>>() {})

        (parsedUser["user"] as MutableMap<String, Any>)["id"] = nextLong()

        val malformedUserString = Base64.getEncoder().encodeToString(ObjectMapper().writeValueAsBytes(parsedUser))

        assertThrows(SignatureException::class.java) {
            tokenHandler.parseToken("${tokenParts[0]}.$malformedUserString.${tokenParts[1]}")
        }
    }

    @Test
    fun `should not be able to parse expired tokens`() {
        val tokenValidityInSeconds = 1L
        val tokenHandler = TokenHandler(randomAlphabetic(11).toByteArray(), tokenValidityInSeconds)
        val token = tokenHandler.createTokenForUser(randomUser())

        Thread.sleep(tokenValidityInSeconds * 2 * 1000)

        assertThrows(ExpiredJwtException::class.java) {
            tokenHandler.parseToken(token)
        }
    }


    private fun randomUser(): AuthenticatedUser =
            AuthenticatedUser(1L, randomAlphabetic(11), listOf(SimpleGrantedAuthority(randomAlphabetic(5))))

}