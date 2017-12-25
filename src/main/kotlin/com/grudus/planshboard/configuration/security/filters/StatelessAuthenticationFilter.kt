package com.grudus.planshboard.configuration.security.filters

import com.grudus.planshboard.configuration.security.token.TokenAuthenticationService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class StatelessAuthenticationFilter(private val authenticationService: TokenAuthenticationService) : OncePerRequestFilter() {


    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val auth = authenticationService.getAuthentication(request)
        SecurityContextHolder.getContext().authentication = auth

        filterChain.doFilter(request, response)
    }
}