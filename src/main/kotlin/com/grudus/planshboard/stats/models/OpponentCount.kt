package com.grudus.planshboard.stats.models

import com.grudus.planshboard.plays.opponent.model.OpponentDto

data class OpponentCount(val opponent: OpponentDto, val count: Int)
