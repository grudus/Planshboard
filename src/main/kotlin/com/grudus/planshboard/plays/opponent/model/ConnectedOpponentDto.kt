package com.grudus.planshboard.plays.opponent.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.user.UserDto

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ConnectedOpponentDto(val id: Id, val name: String, val connectedUser: UserDto? = null)
