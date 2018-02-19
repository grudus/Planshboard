package com.grudus.planshboard.games

import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.games.opponent.Opponent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GameService
@Autowired
constructor(private val gameDao: GameDao) {

    fun findOpponentsForGame(gameId: Id): List<Opponent> =
            gameDao.findOpponentsForGame(gameId)

    fun saveGame(boardGameId: Id, opponents: List<Id>): Id {
        require(opponents.isNotEmpty()) { "Cannot save game without any opponent" }

        val gameId = gameDao.insertGameAlone(boardGameId)
        gameDao.insertGameOpponents(gameId, opponents)

        return gameId
    }

}