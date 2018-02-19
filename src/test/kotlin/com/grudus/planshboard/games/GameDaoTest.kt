package com.grudus.planshboard.games

import com.grudus.planshboard.AbstractDatabaseTest
import com.grudus.planshboard.Tables.GAME_RESULTS
import com.grudus.planshboard.boardgame.BoardGameDao
import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.games.opponent.OpponentDao
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class GameDaoTest
@Autowired
constructor(private val gameDao: GameDao,
            private val opponentDao: OpponentDao,
            private val boardGameDao: BoardGameDao) : AbstractDatabaseTest() {

    private val userId: Id by lazy {
        addUser().id!!
    }
    private val boardGameId by lazy {
        boardGameDao.create(randomAlphabetic(11), userId)
    }

    @Test
    fun `should find all opponents for game`() {
        val opponentIds = addOpponents(3)
        val gameId = addGame(boardGameId, opponentIds)

        val opponents = gameDao.findOpponentsForGame(gameId)
                .map { it.id!! }

        assertEquals(3, opponents.size)
        assertThat(opponentIds, containsInAnyOrder(*opponents.toTypedArray()))
    }

    @Test
    fun `should find opponents for specific game when multiple exists`() {
        val game1Count = 5
        val game2Count = 3
        val game1Id = addGame(boardGameId, addOpponents(game1Count))
        val game2Id = addGame(boardGameId, addOpponents(game2Count))

        val opponents1 = gameDao.findOpponentsForGame(game1Id)
        val opponents2 = gameDao.findOpponentsForGame(game2Id)

        assertEquals(game1Count, opponents1.size)
        assertEquals(game2Count, opponents2.size)
    }

    @Test
    fun `should save game and only game`() {
        val id = gameDao.insertGameAlone(boardGameId)

        assertNotNull(id)
    }

    @Test
    fun `should save multiple games alone for one board game`() {
        val id1 = gameDao.insertGameAlone(boardGameId)
        val id2 = gameDao.findOpponentsForGame(boardGameDao.create(randomAlphabetic(11), userId))

        assertNotEquals(id1, id2)
    }

    @Test
    fun `should insert multiple game opponents`() {
        val gameResultsCount = 3
        val gameId = gameDao.insertGameAlone(boardGameId)
        val opponentIds = addOpponents(gameResultsCount)

        gameDao.insertGameOpponents(gameId, opponentIds)

        val dbGameResultsCount = dsl.fetchCount(GAME_RESULTS)
        assertEquals(gameResultsCount, dbGameResultsCount)
    }


    private fun addOpponents(count: Int = 5): List<Id> =
            (0 until count).map { randomAlphabetic(11 + it) }
                    .map { name -> opponentDao.addOpponent(userId, name) }

    
    
    private fun addGame(boardGameId: Id, opponents: List<Id>): Id {
        val gameId = gameDao.insertGameAlone(boardGameId)
        gameDao.insertGameOpponents(gameId, opponents)
        return gameId
    }
}