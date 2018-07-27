package com.grudus.planshboard.stats

import com.grudus.planshboard.AbstractDatabaseTest
import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.utils.BoardGameUtil
import com.grudus.planshboard.utils.OpponentsUtil
import com.grudus.planshboard.utils.PlayUtil
import org.apache.commons.lang3.RandomStringUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class PlaysStatsDaoTest
@Autowired
constructor(private val dao: PlaysStatsDao,
            private val boardGameUtil: BoardGameUtil,
            private val opponentsUtil: OpponentsUtil,
            private val playUtil: PlayUtil) : AbstractDatabaseTest() {

    private val userId: Id by lazy { addUser(RandomStringUtils.randomAlphabetic(11)).id!! }

    @Test
    fun `should count all plays`() {
        val gameId = boardGameUtil.addRandomBoardGame(userId)
        val numberOfPlays = 5

        playUtil.addPlays(gameId, addOpponents(), numberOfPlays)

        val playsCount = dao.countAllPlays(userId)

        assertEquals(numberOfPlays, playsCount)
    }


    @Test
    fun `should count plays from all board games`() {
        val gameId = boardGameUtil.addRandomBoardGame(userId)
        val gameId2 = boardGameUtil.addRandomBoardGame(userId)
        val numberOfPlays = 5

        playUtil.addPlays(gameId, addOpponents(), numberOfPlays)
        playUtil.addPlays(gameId2, addOpponents(), numberOfPlays)

        val playsCount = dao.countAllPlays(userId)

        assertEquals(numberOfPlays * 2, playsCount)
    }




    @Test
    fun `should count plays only from one user`() {
        val gameId = boardGameUtil.addRandomBoardGame(userId)
        val gameId2 = boardGameUtil.addRandomBoardGame(addUser().id!!)
        val numberOfPlays = 5

        playUtil.addPlays(gameId, addOpponents(), numberOfPlays)
        playUtil.addPlays(gameId2, addOpponents(), numberOfPlays)

        val playsCount = dao.countAllPlays(userId)

        assertEquals(numberOfPlays, playsCount)
    }


    private fun addOpponents() =
            opponentsUtil.addOpponents(userId)
}