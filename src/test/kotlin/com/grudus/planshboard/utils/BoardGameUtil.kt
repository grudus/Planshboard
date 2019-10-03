package com.grudus.planshboard.utils

import com.grudus.planshboard.boardgame.BoardGameService
import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.plays.PlayService
import com.grudus.planshboard.plays.model.AddPlayResult
import com.grudus.planshboard.plays.model.SavePlayRequest
import com.grudus.planshboard.plays.opponent.OpponentService
import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.RandomStringUtils.random
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class BoardGameUtil
@Autowired
constructor(private val boardGameService: BoardGameService,
            private val opponentService: OpponentService,
            private val playService: PlayService) {

    fun addRandomBoardGame(userId: Id, name: String = RandomStringUtils.randomAlphabetic(11)): Id =
            boardGameService.createNew(userId, name)

    fun addRandomBoardGames(userId: Id, numberOfGames: Int): List<Id> =
            (0 until numberOfGames).map {
                addRandomBoardGame(userId)
            }

    fun addRandomBoardGamesWithPlay(userId: Id, numberOfGames: Int): List<Id> {
        val opponentId = opponentService.findOpponentPointingToCurrentUser(userId).id
        return (0 until numberOfGames).map {
            val boardGameId = addRandomBoardGame(userId)
            val savePlayRequest = SavePlayRequest(listOf(
                    AddPlayResult(random(11), 1, null, opponentId)
            ))
            playService.savePlay(userId, boardGameId, savePlayRequest)
            return@map boardGameId
        }
    }
}
