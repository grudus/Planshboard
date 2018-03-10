package com.grudus.planshboard.plays

import com.grudus.planshboard.AbstractDatabaseTest
import com.grudus.planshboard.Tables.PLAYS_RESULTS
import com.grudus.planshboard.boardgame.BoardGameDao
import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.plays.model.PlayResult
import com.grudus.planshboard.plays.opponent.OpponentDao
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class PlayDaoTest
@Autowired
constructor(private val playDao: PlayDao,
            private val opponentDao: OpponentDao,
            private val boardGameDao: BoardGameDao) : AbstractDatabaseTest() {

    private val userId: Id by lazy {
        addUser().id!!
    }
    private val boardGameId by lazy {
        boardGameDao.create(randomAlphabetic(11), userId)
    }

    @Test
    fun `should find all opponents for play`() {
        val opponentIds = addOpponents(3)
        val playId = addPlay(boardGameId, opponentIds)

        val opponents = playDao.findOpponentsForPlay(playId)
                .map { it.id!! }

        assertEquals(3, opponents.size)
        assertThat(opponentIds, containsInAnyOrder(*opponents.toTypedArray()))
    }

    @Test
    fun `should find opponents for specific play when multiple exists`() {
        val play1Count = 5
        val play2Count = 3
        val play1Id = addPlay(boardGameId, addOpponents(play1Count))
        val play2Id = addPlay(boardGameId, addOpponents(play2Count))

        val opponents1 = playDao.findOpponentsForPlay(play1Id)
        val opponents2 = playDao.findOpponentsForPlay(play2Id)

        assertEquals(play1Count, opponents1.size)
        assertEquals(play2Count, opponents2.size)
    }

    @Test
    fun `should save play and only play`() {
        val id = playDao.insertPlayAlone(boardGameId)

        assertNotNull(id)
    }

    @Test
    fun `should save multiple plays alone for one board play`() {
        val id1 = playDao.insertPlayAlone(boardGameId)
        val id2 = playDao.findOpponentsForPlay(boardGameDao.create(randomAlphabetic(11), userId))

        assertNotEquals(id1, id2)
    }

    @Test
    fun `should insert many play results for multiple plays`() {
        val playResultCount = 4
        val playsIds = listOf(playDao.insertPlayAlone(boardGameId), playDao.insertPlayAlone(boardGameId))
        val playResults = addOpponents(playResultCount)
                .mapIndexed { index, id -> PlayResult(playsIds[index % 2], id, null, null) }

        playDao.savePlayResults(playResults)

        val dbPlayResultCount = dsl.fetchCount(PLAYS_RESULTS)
        assertEquals(playResultCount, dbPlayResultCount)
    }


    private fun addOpponents(count: Int = 5): List<Id> =
            (0 until count).map { randomAlphabetic(11 + it) }
                    .map { name -> opponentDao.addOpponent(userId, name) }



    private fun addPlay(boardGameId: Id, opponents: List<Id>): Id {
        val playId = playDao.insertPlayAlone(boardGameId)
        val playResults = opponents.map { PlayResult(playId, it, null, null) }
        playDao.savePlayResults(playResults)
        return playId
    }
}