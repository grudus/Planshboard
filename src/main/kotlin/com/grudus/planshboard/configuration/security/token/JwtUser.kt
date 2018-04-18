package com.grudus.planshboard.configuration.security.token

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.configuration.security.AuthenticatedUser


data class JwtUser
@JsonCreator
constructor(@JsonProperty("id") val id: Id,
            @JsonProperty("name") val name: String,
            @JsonProperty("authorities") val authorities: Collection<String>) {


    companion object {
        fun fromAuthenticatedUser(user: AuthenticatedUser): JwtUser =
                JwtUser(user.id, user.name, user.authorities.map { it.authority })
    }
}