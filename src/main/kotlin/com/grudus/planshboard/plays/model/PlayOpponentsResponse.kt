package com.grudus.planshboard.plays.model

import com.grudus.planshboard.commons.Id

class PlayOpponentsResponse(val opponentId: Id, val opponentName: String, val position: Int?, val points: Int?) {
    constructor(dto: PlayOpponentsDto) : this(dto.opponentId, dto.opponentName, dto.position, dto.points)
}