package com.grudus.planshboard.configuration.security

import com.grudus.planshboard.user.User
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken


class AuthenticatedUser(val user: User) : UsernamePasswordAuthenticationToken(user.name, user.password, user.getAuthorities()) {

    val userId: Long?
        get() = user.id
}