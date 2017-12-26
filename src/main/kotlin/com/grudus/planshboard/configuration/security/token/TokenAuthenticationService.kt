package com.grudus.planshboard.configuration.security.token

import com.grudus.planshboard.configuration.security.AuthenticatedUser
import com.grudus.planshboard.user.auth.UserTokenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.xml.bind.DatatypeConverter

@Service
class TokenAuthenticationService
@Autowired
constructor(@Value("\${token.secret}") secretToken: String, private val userTokenService: UserTokenService) {

    private val tokenHandler: TokenHandler = TokenHandler(DatatypeConverter.parseBase64Binary(secretToken))


    fun addAuthentication(response: HttpServletResponse, authentication: Authentication) {
        val user = (authentication as AuthenticatedUser).user

        val token = user.token ?: tokenHandler.createTokenForUser(user)
                .let { userTokenService.addToken(user.id!!, it); it }

        response.setHeader(AUTH_HEADER_NAME, token)
    }

    fun getAuthentication(request: HttpServletRequest): Authentication? =
            request.getHeader(AUTH_HEADER_NAME)
                    ?.let { token -> userTokenService.findByToken(token) }
                    ?.let { user -> AuthenticatedUser(user) }


    companion object {
        val AUTH_HEADER_NAME = "X-AUTH-TOKEN"
    }
}