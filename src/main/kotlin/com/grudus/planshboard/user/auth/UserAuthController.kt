package com.grudus.planshboard.user.auth

import com.grudus.planshboard.configuration.security.AuthenticatedUser
import com.grudus.planshboard.configuration.security.token.TokenAuthenticationService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/api/auth")
class UserAuthController
@Autowired
constructor(private val tokenAuthenticationService: TokenAuthenticationService) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping("/logout")
    fun logout(request: HttpServletRequest,
               response: HttpServletResponse) {
        logger.info("Logout user [{}]", tokenAuthenticationService.getAuthentication(request)?.name)
        tokenAuthenticationService.removeAuthentication(response)
    }

}
