package com.grudus.planshboard.stats

import com.grudus.planshboard.boardgame.BoardGameDto

data class PlaysCount(val boardGame: BoardGameDto, val count: Int)