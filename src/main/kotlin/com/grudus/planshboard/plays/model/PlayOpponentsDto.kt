package com.grudus.planshboard.plays.model

import com.grudus.planshboard.commons.Id

class PlayOpponentsDto(val playId: Id, val opponentId: Id, val opponentName: String, val position: Int?, val points: Int?) {
    constructor(playId: Id, opponentId: Id, opponentName: String): this(playId, opponentId, opponentName, null, null)
}