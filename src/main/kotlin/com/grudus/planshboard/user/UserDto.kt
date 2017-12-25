package com.grudus.planshboard.user

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.grudus.planshboard.commons.date.JsonLocalDateTimeDeserializer
import com.grudus.planshboard.commons.date.JsonLocalDateTimeSerializer
import java.time.LocalDateTime

class UserDto(val name: String,
              @JsonSerialize(using = JsonLocalDateTimeSerializer::class)
              @JsonDeserialize(using = JsonLocalDateTimeDeserializer::class)
              val registerDate: LocalDateTime) {

    companion object {
        fun fromUser(user: User): UserDto
            = UserDto(user.name, user.registerDate)
    }

}