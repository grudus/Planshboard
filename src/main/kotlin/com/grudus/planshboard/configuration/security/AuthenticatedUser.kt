package com.grudus.planshboard.configuration.security

import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.user.User
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken


class AuthenticatedUser(val user: User) : UsernamePasswordAuthenticationToken(user.name, user.password, user.getAuthorities()) {

    val userId: Id
        get() = user.id ?: throw AuthenticationCredentialsNotFoundException("Cannot find user's id")
}