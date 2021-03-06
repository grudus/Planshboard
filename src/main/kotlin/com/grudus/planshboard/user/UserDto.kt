package com.grudus.planshboard.user

import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.configuration.security.AuthenticatedUser

data class UserDto(val id: Id, val name: String, val opponentEntityId: Id? = null) {

    companion object {
        fun fromUser(user: AuthenticatedUser): UserDto = UserDto(user.id, user.name)
    }

}
