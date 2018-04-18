package com.grudus.planshboard.configuration.security

import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.configuration.security.token.JwtUser
import com.grudus.planshboard.user.User
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority


class AuthenticatedUser(
        val id: Id, name: String, authorities: Collection<GrantedAuthority>, val token: String? = null) : UsernamePasswordAuthenticationToken(name, token, authorities) {
    constructor(user: User) : this(user.id!!, user.name, user.getAuthorities())

    constructor(user: JwtUser) : this(user.id, user.name, user.authorities.map { SimpleGrantedAuthority(it) })

    val userId: Id
        get() = id
}