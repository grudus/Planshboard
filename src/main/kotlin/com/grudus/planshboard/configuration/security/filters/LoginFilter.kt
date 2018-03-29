package com.grudus.planshboard.configuration.security.filters

import com.grudus.planshboard.configuration.security.AuthenticatedUser
import com.grudus.planshboard.configuration.security.UserAuthenticationProvider
import com.grudus.planshboard.configuration.security.token.TokenAuthenticationService
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class StatelessLoginFilter

constructor(defaultFilterProcessesUrl: String,
            private val tokenAuthenticationService: TokenAuthenticationService,
            private val userAuthenticationProvider: UserAuthenticationProvider,
            private val usernameParameter: String = DEFAULT_USERNAME_PARAMETER,
            private val passwordParameter: String = DEFAULT_PASSWORD_PARAMETER) : AbstractAuthenticationProcessingFilter(defaultFilterProcessesUrl) {

    private val log = LoggerFactory.getLogger(javaClass.simpleName)


    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val username = request.getParameter(usernameParameter)
        val password = request.getParameter(passwordParameter)

        return userAuthenticationProvider.authenticate(UsernamePasswordAuthenticationToken(username, password))
    }

    override fun successfulAuthentication(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain?, authResult: Authentication) {
        val auth = authResult as AuthenticatedUser

        tokenAuthenticationService.addAuthentication(response, auth)
        SecurityContextHolder.getContext().authentication = auth
        log.info("User ${auth.user.name} successfully logged")
    }

    override fun unsuccessfulAuthentication(request: HttpServletRequest, response: HttpServletResponse, failed: AuthenticationException) {
        log.warn("Cannot authenticate user", failed)
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, failed.message)
    }

    companion object {
        private const val DEFAULT_USERNAME_PARAMETER = "username"
        private const val DEFAULT_PASSWORD_PARAMETER = "password"
    }
}