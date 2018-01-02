package com.grudus.planshboard.user.auth

import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.configuration.security.AuthenticatedUser
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class AuthenticationService {

    fun currentUserId(): Id =
            getAuthentication().let { auth ->
                when (auth) {
                    is AuthenticatedUser -> auth.userId
                    else -> throw AuthenticationCredentialsNotFoundException("Cannot obtain current user id")
                }
            }

    private fun getAuthentication(): Authentication =
            SecurityContextHolder.getContext().authentication

}