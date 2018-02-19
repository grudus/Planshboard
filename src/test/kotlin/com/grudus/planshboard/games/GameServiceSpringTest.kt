package com.grudus.planshboard.games

import com.grudus.planshboard.AbstractSpringServiceTest
import com.grudus.planshboard.boardgame.BoardGameService
import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.games.opponent.AddOpponentRequest
import com.grudus.planshboard.games.opponent.OpponentService
import org.apache.commons.lang3.RandomStringUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class GameServiceSpringTest
@Autowired
constructor(private val boardGameService: BoardGameService,
            private val opponentService: OpponentService,
            private val gameService: GameService) : AbstractSpringServiceTest() {


    private val userId: Id by lazy {
        addUser().id!!
    }
    private val boardGameId by lazy {
        boardGameService.createNew(userId, RandomStringUtils.randomAlphabetic(11))
    }

    @Test
    fun `should save game`() {
        val opponentIds = addOpponents(5)

        val id = gameService.saveGame(boardGameId, opponentIds)

        Assertions.assertNotNull(id)
    }

    @Test
    fun `should not be able to save game without opponents`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            gameService.saveGame(boardGameId, emptyList())
        }
    }

    @Test
    fun `should be able to save game for the same opponents`() {
        val opponentIds = addOpponents(4)

        val id1 = gameService.saveGame(boardGameId, opponentIds)
        val id2 = gameService.saveGame(boardGameId, opponentIds)

        Assertions.assertNotEquals(id1, id2)
    }


    private fun addOpponents(count: Int = 5): List<Id> =
            (0 until count).map { RandomStringUtils.randomAlphabetic(11 + it) }
                    .map { name -> opponentService.addOpponent(userId, AddOpponentRequest(name)) }


}