package com.grudus.planshboard.stats.models

data class StatsDto(
        val boardGamesCount: Int,
        val allPlaysCount: Int,
        val playPositionsPerOpponentCount: List<OpponentCount>,
        val playsPerBoardGameCount: List<PlaysCount>
)