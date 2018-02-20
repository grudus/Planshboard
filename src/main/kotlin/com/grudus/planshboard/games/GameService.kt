package com.grudus.planshboard.games

import com.grudus.planshboard.boardgame.BoardGameService
import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.commons.exceptions.ResourceNotFoundException
import com.grudus.planshboard.games.opponent.Opponent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GameService
@Autowired
constructor(private val gameDao: GameDao,
            private val boardGameService: BoardGameService) {

    fun findOpponentsForGame(gameId: Id): List<Opponent> =
            if (gameDao.findById(gameId) != null)
                gameDao.findOpponentsForGame(gameId)
            else throw ResourceNotFoundException("Cannot find game with id [${gameId}]")

    fun saveGame(boardGameId: Id, opponents: List<Id>): Id {
        require(opponents.isNotEmpty()) { "Cannot save game without any opponent" }

        val gameId = gameDao.insertGameAlone(boardGameId)
        gameDao.insertGameOpponents(gameId, opponents)

        return gameId
    }

    fun belongsToAnotherUser(userId: Id, gameId: Id): Boolean =
            gameDao.findById(gameId)
                    ?.let { game -> boardGameService.belongsToAnotherUser(userId, game.boardGameId) }
                    ?: false
}