package com.grudus.planshboard.stats

import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.stats.models.StatsDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class StatsService
@Autowired
constructor(private val gamesStatsDao: GamesStatsDao,
            private val playsStatsDao: PlaysStatsDao) {


    fun generateStats(userId: Id, opponentId: Id): StatsDto = StatsDto(
            boardGamesCount = gamesStatsDao.countAllGames(opponentId),
            allPlaysCount = playsStatsDao.countAllPlays(opponentId),
            playPositionsPerOpponentCount = playsStatsDao.countPlayPositionPerOpponent(userId),
            playsPerBoardGameCount = playsStatsDao.countPlaysPerBoardGames(opponentId)
    )
}
