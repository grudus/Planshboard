package com.grudus.planshboard.plays.model

import com.grudus.planshboard.commons.Id

data class PlayResult(val id: Id?, val playId: Id, val opponentId: Id, val points: Int?, val position: Int?) {
    constructor(playId: Id, opponentId: Id, points: Int?, position: Int?): this(null, playId, opponentId, points, position)
}