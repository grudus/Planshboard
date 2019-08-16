package com.grudus.planshboard.configuration.security.token

import com.grudus.planshboard.configuration.security.AuthenticatedUser
import com.grudus.planshboard.environment.EnvironmentKeys.TOKEN_SECRET
import com.grudus.planshboard.environment.EnvironmentService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class TokenAuthenticationService
@Autowired
constructor(environmentService: EnvironmentService) {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val tokenHandler: TokenHandler = TokenHandler(environmentService.getText(TOKEN_SECRET).toByteArray())

    fun addAuthentication(response: HttpServletResponse, authentication: Authentication) {
        val user = (authentication as AuthenticatedUser)
        logger.info("Adding authentication for user [{}]", user.userId)

        val token = user.token ?: tokenHandler.createTokenForUser(user)
        response.addCookie(createAuthCookie(token))
    }


    fun getAuthentication(request: HttpServletRequest): Authentication? =
            request.cookies.find { cookie -> cookie.name == AUTH_COOKIE_NAME }
                    ?.let { cookie -> tokenHandler.parseToken(cookie.value) }

    fun removeAuthentication(response: HttpServletResponse) {
        val cookie = createAuthCookie("")
        cookie.maxAge = 0
        response.addCookie(cookie)
    }

    private fun createAuthCookie(token: String): Cookie {
        val cookie = Cookie(AUTH_COOKIE_NAME, token)
        cookie.isHttpOnly = true
        cookie.maxAge = 3600
        cookie.path = "/"
        return cookie
    }


    companion object {
        const val AUTH_COOKIE_NAME = "Authorization"
    }
}
