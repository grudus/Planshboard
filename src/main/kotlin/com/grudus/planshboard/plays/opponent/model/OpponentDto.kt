package com.grudus.planshboard.plays.opponent.model

import com.grudus.planshboard.commons.Id

data class OpponentDto(val id: Id, val name: String, val pointingToUser: Id? = null) {
    companion object {
        fun fromOpponent(opponent: Opponent) = OpponentDto(opponent.id!!, opponent.name, opponent.pointingToUser)
    }
}
