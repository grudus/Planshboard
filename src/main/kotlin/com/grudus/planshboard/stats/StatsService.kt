package com.grudus.planshboard.stats

import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.plays.model.PlayOpponentsDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class StatsService
@Autowired
constructor(private val gamesStatsDao: GamesStatsDao,
            private val playsStatsDao: PlaysStatsDao) {

    fun countAllBoardGames(userId: Id): Int =
            gamesStatsDao.countAllGames(userId)

    fun countAllPlays(userId: Id): Int =
            playsStatsDao.countAllPlays(userId)

    fun countPlayPositionPerOpponent(userId: Id): List<OpponentCount> =
            playsStatsDao.countPlayPositionPerOpponent(userId)

    fun countPlaysPerBoardGame(userId: Id): List<PlaysCount> =
            playsStatsDao.countPlaysPerBoardGames(userId)
}