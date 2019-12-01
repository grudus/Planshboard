package com.grudus.planshboard.stats.models

import com.grudus.planshboard.boardgame.BoardGameDto

data class WinsCount(val boardGame: BoardGameDto, val count: Int)
