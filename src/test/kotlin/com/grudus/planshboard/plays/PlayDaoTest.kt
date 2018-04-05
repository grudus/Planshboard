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
import java.time.LocalDateTime

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

        val dbPlayResultCount = countPlayResults(playsIds)
        assertEquals(playResultCount, dbPlayResultCount)
    }

    @Test
    fun `should find plays for board game`() {
        val boardGameId2 = boardGameDao.create(randomAlphabetic(11), userId)
        val playsCount = 4
        repeat(playsCount) { playDao.insertPlayAlone(boardGameId) }
        repeat(3) { playDao.insertPlayAlone(boardGameId2) }

        val plays = playDao.findPlaysForBoardGame(boardGameId)

        assertEquals(playsCount, plays.size)
    }

    @Test
    fun `should return empty list when no plays for board game`() {
        val boardGameId2 = boardGameDao.create(randomAlphabetic(11), userId)
        repeat(3) { playDao.insertPlayAlone(boardGameId2) }

        val plays = playDao.findPlaysForBoardGame(boardGameId)

        assertTrue(plays.isEmpty())
    }

    @Test
    fun `should find play results for single play`() {
        addPlay(boardGameDao.create(randomAlphabetic(11), userId), addOpponents(2))
        val opponentsCount = 3
        val id = addPlay(boardGameId, addOpponents(opponentsCount))

        val playResults = playDao.findPlayResultsForPlays(listOf(id))

        assertEquals(opponentsCount, playResults.size)
    }

    @Test
    fun `should find play results for multiple plays`() {
        val opponentsCount = 3
        val id1 = addPlay(boardGameDao.create(randomAlphabetic(11), userId), addOpponents(opponentsCount))
        val id2 = addPlay(boardGameId, addOpponents(opponentsCount))

        val playResults = playDao.findPlayResultsForPlays(listOf(id1, id2))

        assertEquals(opponentsCount * 2, playResults.size)
    }

    @Test
    fun `should insert with note`() {
        val note = randomAlphabetic(11)
        playDao.insertPlayAlone(boardGameId, LocalDateTime.now(), note)
        playDao.insertPlayAlone(boardGameId, LocalDateTime.now())

        val plays = playDao.findPlaysForBoardGame(boardGameId)
                .sortedBy { it.id }

        assertEquals(note, plays[0].note)
        assertNull(plays[1].note)
    }

    @Test
    fun `should insert with date in past`() {
        val date = LocalDateTime.now().minusDays(5)
        playDao.insertPlayAlone(boardGameId, date)

        val play = playDao.findPlaysForBoardGame(boardGameId)[0]

        assertEquals(date, play.date)
    }

    @Test
    fun `should insert with today's date when no date specified`() {
        val timeBeforeInsert = LocalDateTime.now().minusSeconds(10)
        playDao.insertPlayAlone(boardGameId)

        val play = playDao.findPlaysForBoardGame(boardGameId)[0]

        assertTrue(play.date.isAfter(timeBeforeInsert))
    }

    @Test
    fun `should find specific play results`() {
        val playId = playDao.insertPlayAlone(boardGameId)
        val points = listOf(32, 22)
        val position = listOf(1, 2)
        val opponents = addOpponents(2)
        val playResults = (0 until 2).map {
            PlayResult(playId, opponents[it], points[it], position[it])
        }
        playDao.savePlayResults(playResults)

        val dbResults = playDao.findPlayResultsForPlays(listOf(playId))
                .sortedBy { it.position }

        assertEquals(position[0], dbResults[0].position)
        assertEquals(points[0], dbResults[0].points)
        assertEquals(opponents[0], dbResults[0].opponentId)
        assertEquals(position[1], dbResults[1].position)
        assertEquals(points[1], dbResults[1].points)
        assertEquals(opponents[1], dbResults[1].opponentId)
    }


    @Test
    fun `should delete play`() {
        val playId = playDao.insertPlayAlone(boardGameId)
        val playId2 = playDao.insertPlayAlone(boardGameId)

        playDao.delete(playId)

        val dbPlays = playDao.findPlaysForBoardGame(boardGameId)

        assertEquals(1, dbPlays.size)
        assertEquals(playId2, dbPlays[0].id)
    }


    @Test
    fun `should delete play with results`() {
        val playId = addPlay(boardGameId, addOpponents(3))

        playDao.delete(playId)

        val dbPlays = playDao.findPlaysForBoardGame(boardGameId)
        val dbResults = playDao.findPlayResultsForPlays(listOf(playId))

        assertTrue(dbPlays.isEmpty())
        assertTrue(dbResults.isEmpty())
    }

    @Test
    fun `shouldn't delete opponents when deleting play with results`() {
        val opponentIds = addOpponents(1)
        val playId = addPlay(boardGameId, opponentIds)

        playDao.delete(playId)

        val opponents = opponentDao.findAllOpponentsWithoutReal(userId)
        assertEquals(1, opponents.size)
        assertEquals(opponentIds[0], opponents[0].id)
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

    private fun countPlayResults(playsIds: List<Id>): Int =
            playDao.findPlayResultsForPlays(playsIds).size

}