package com.grudus.planshboard.stats

import com.grudus.planshboard.plays.opponent.OpponentDto

data class OpponentCount(val opponent: OpponentDto, val count: Int)