package com.grudus.planshboard.user

import com.grudus.planshboard.configuration.security.AuthenticatedUser

class UserDto(val name: String) {

    companion object {
        fun fromUser(user: AuthenticatedUser): UserDto = UserDto(user.name)
    }

}