package com.grudus.planshboard.user

import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.user.User.Role.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.time.LocalDateTime

data class User(val id: Id? = null,
                val name: String,
                val password: String,
                val role: Role = USER,
                val token: String? = null,
                val registerDate: LocalDateTime = LocalDateTime.now()) {

    fun getAuthorities(): List<GrantedAuthority> = listOf(SimpleGrantedAuthority("ROLE_${role.name}"))


    enum class Role {
        USER,
        ADMIN
    }

}