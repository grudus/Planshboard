package com.grudus.planshboard.configuration.security.token

import com.grudus.planshboard.configuration.security.AuthenticatedUser
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class TokenAuthenticationService
@Autowired
constructor(@Value("\${token.secret}") private val tokenSecret: String) {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val tokenHandler: TokenHandler = TokenHandler(tokenSecret.toByteArray())

    fun addAuthentication(response: HttpServletResponse, authentication: Authentication) {
        val user = (authentication as AuthenticatedUser)

        val token = user.token ?: tokenHandler.createTokenForUser(user)

        response.setHeader(AUTH_HEADER_NAME, "$TOKEN_PREFIX$token")
    }

    fun getAuthentication(request: HttpServletRequest): Authentication? =
            request.getHeader(AUTH_HEADER_NAME)
                    ?.let { token -> tokenHandler.parseToken(token.removePrefix(TOKEN_PREFIX)) }


    companion object {
        const val AUTH_HEADER_NAME = "Authorization"
        const val TOKEN_PREFIX = "Bearer "
    }
}