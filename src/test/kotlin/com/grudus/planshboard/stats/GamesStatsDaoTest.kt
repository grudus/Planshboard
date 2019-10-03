package com.grudus.planshboard.stats

import com.grudus.planshboard.AbstractDatabaseTest
import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.plays.opponent.OpponentService
import com.grudus.planshboard.utils.BoardGameUtil
import com.grudus.planshboard.utils.OpponentsUtil
import com.grudus.planshboard.utils.PlayUtil
import org.apache.commons.lang3.RandomStringUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.math.sign

class GamesStatsDaoTest
@Autowired
constructor(private val dao: GamesStatsDao,
            private val boardGameUtil: BoardGameUtil,
            private val opponentsUtil: OpponentsUtil,
            private val playUtil: PlayUtil) : AbstractDatabaseTest() {

    private val userId: Id by lazy { addUser(RandomStringUtils.randomAlphabetic(11)).id!! }

    @Test
    fun `should count all user games`() {
        val gamesCount = 5
        boardGameUtil.addRandomBoardGamesWithPlay(this.userId, gamesCount)

        val allGames = dao.countAllGames(opponentsUtil.asOpponent(userId))
        assertEquals(gamesCount, allGames)
    }


    @Test
    fun `should count only given user games`() {
        val gamesCount = 3
        val newUserId = addUser().id!!

        boardGameUtil.addRandomBoardGamesWithPlay(this.userId, gamesCount + 1)
        boardGameUtil.addRandomBoardGamesWithPlay(newUserId, gamesCount)

        val allGames = dao.countAllGames(opponentsUtil.asOpponent(newUserId))
        assertEquals(gamesCount, allGames)
    }

    @Test
    fun `shouldn't count any game when user has no games`() {
        val allGames = dao.countAllGames(this.userId)
        assertEquals(0, allGames)
    }

    @Test
    fun `should count only board games with plays`() {
        boardGameUtil.addRandomBoardGames(userId, 2)
        boardGameUtil.addRandomBoardGamesWithPlay(userId, 3)

        val allGames = dao.countAllGames(opponentsUtil.asOpponent(userId))
        assertEquals(3, allGames)
    }

    @Test
    fun `number of plays should not affect counting board games`() {
        boardGameUtil.addRandomBoardGamesWithPlay(userId, 4)
        val boardGameId = boardGameUtil.addRandomBoardGamesWithPlay(userId, 1)[0]
        playUtil.addPlays(boardGameId, listOf(opponentsUtil.asOpponent(userId)), 12)

        val allGames = dao.countAllGames(opponentsUtil.asOpponent(userId))
        assertEquals(5, allGames)
    }

    @Test
    fun `should count only board games for specific opponent`() {
        val boardGameIds = boardGameUtil.addRandomBoardGames(userId, 5)
        val opponent1 = opponentsUtil.asOpponent(userId)
        val opponent2 = opponentsUtil.addOpponents(userId)[0]

        playUtil.addPlays(boardGameIds[0], listOf(opponent1), 1)
        playUtil.addPlays(boardGameIds[1], listOf(opponent1, opponent2), 1)
        playUtil.addPlays(boardGameIds[2], listOf(opponent2), 1)
        playUtil.addPlays(boardGameIds[3], listOf(opponent2), 1)
        playUtil.addPlays(boardGameIds[4], listOf(opponent2), 1)

        val allGames = dao.countAllGames(opponentsUtil.asOpponent(userId))

        assertEquals(2, allGames)
    }
}
