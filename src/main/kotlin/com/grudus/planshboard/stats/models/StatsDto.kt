package com.grudus.planshboard.stats.models

data class StatsDto(
        val boardGamesCount: Int,
        val allPlaysCount: Int,
        val playsPerBoardGameCount: List<PlaysCount>,
        val opponentWins: Int,
        val winsPerBoardGame: List<WinsCount>
)
