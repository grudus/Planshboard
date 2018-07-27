package com.grudus.planshboard.stats

import com.grudus.planshboard.AbstractDatabaseTest
import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.utils.BoardGameUtil
import org.apache.commons.lang3.RandomStringUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class GamesStatsDaoTest
@Autowired
constructor(private val dao: GamesStatsDao,
            private val boardGameUtil: BoardGameUtil) : AbstractDatabaseTest() {

    private val userId: Id by lazy { addUser(RandomStringUtils.randomAlphabetic(11)).id!! }


    @Test
    fun `should count all user games`() {
        val gamesCount = 5
        boardGameUtil.addRandomBoardGames(this.userId, gamesCount)

        val allGames = dao.countAllGames(this.userId)
        assertEquals(gamesCount, allGames)
    }


    @Test
    fun `should count only given user games`() {
        val gamesCount = 3
        val newUserId = addUser().id!!

        boardGameUtil.addRandomBoardGames(this.userId, gamesCount + 1)
        boardGameUtil.addRandomBoardGames(newUserId, gamesCount)

        val allGames = dao.countAllGames(newUserId)
        assertEquals(gamesCount, allGames)
    }

    @Test
    fun `shouldn't count any game when user has no games`() {
        val allGames = dao.countAllGames(this.userId)
        assertEquals(0, allGames)
    }
}