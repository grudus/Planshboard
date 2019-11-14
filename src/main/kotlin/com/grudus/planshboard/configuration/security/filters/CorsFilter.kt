package com.grudus.planshboard.configuration.security.filters

import com.grudus.planshboard.configuration.security.token.TokenAuthenticationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class CorsFilter
    constructor(private val allowedOrigin: String): GenericFilterBean() {


    override fun doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) {
        val response = res as HttpServletResponse
        val request = req as HttpServletRequest

        response.setHeader("Access-Control-Allow-Origin", allowedOrigin)
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS")
        response.setHeader("Access-Control-Allow-Headers", "withCredentials, content-type, " + TokenAuthenticationService.AUTH_HEADER_NAME)
        response.setHeader("Access-Control-Allow-Credentials", "true")
        response.setHeader("Access-Control-Expose-Headers", TokenAuthenticationService.AUTH_HEADER_NAME)
        response.setHeader("withCredentials", "true")

        if (request.method != "OPTIONS")
            chain.doFilter(request, response)
    }
}
