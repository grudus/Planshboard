package com.grudus.planshboard.configuration.security

import com.grudus.planshboard.user.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component


@Component
class UserAuthenticationProvider

@Autowired
constructor(private val userService: UserService, private val passwordEncoder: PasswordEncoder) : AuthenticationProvider {


    override fun authenticate(authentication: Authentication): Authentication {
        val username = authentication.principal as String?
        val credentials = authentication.credentials

        if (username == null || credentials == null)
            throw BadCredentialsException("Bad credentials")

        val password = credentials.toString().trim({ it <= ' ' })

        val user = userService.findByUsername(username.trim { it <= ' ' })
                ?: throw UsernameNotFoundException("Cannot find user: $username")


        if (!passwordEncoder.matches(password, user.password))
            throw BadCredentialsException("Bad credentials")

        return AuthenticatedUser(user)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return true
    }
}