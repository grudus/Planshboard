package com.grudus.planshboard.plays

import com.grudus.planshboard.AbstractDatabaseTest
import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.plays.model.PlayResponse
import com.grudus.planshboard.plays.model.PlayResult
import com.grudus.planshboard.plays.opponent.OpponentDao
import com.grudus.planshboard.utils.BoardGameUtil
import com.grudus.planshboard.utils.OpponentsUtil
import com.grudus.planshboard.utils.PlayUtil
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime.*

class PlayDaoTest
@Autowired
constructor(private val playDao: PlayDao,
            private val opponentsUtil: OpponentsUtil,
            private val opponentDao: OpponentDao,
            private val boardGameUtil: BoardGameUtil,
            private val playUtil: PlayUtil) : AbstractDatabaseTest() {

    private val userId: Id by lazy {
        addUser().id!!
    }
    private val boardGameId by lazy {
        boardGameUtil.addRandomBoardGame(userId)
    }

    @Test
    fun `should find all opponents for play`() {
        val opponentIds = addOpponents(3)
        val playId = playUtil.addPlay(boardGameId, opponentIds)

        val opponents = playDao.findOpponentsForPlay(playId)
                .map { it.id!! }

        assertEquals(3, opponents.size)
        assertThat(opponentIds, containsInAnyOrder(*opponents.toTypedArray()))
    }

    @Test
    fun `should find opponents for specific play when multiple exists`() {
        val play1Count = 5
        val play2Count = 3
        val play1Id = playUtil.addPlay(boardGameId, addOpponents(play1Count))
        val play2Id = playUtil.addPlay(boardGameId, addOpponents(play2Count))

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
        val id2 = playDao.findOpponentsForPlay(boardGameUtil.addRandomBoardGame(userId))

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
        val boardGameId2 = boardGameUtil.addRandomBoardGame(userId)
        val playsCount = 4
        repeat(playsCount) { playUtil.addPlay(boardGameId, addOpponents(2)) }
        repeat(3) { playUtil.addPlay(boardGameId2, addOpponents(2)) }

        val plays = playDao.findPlaysForBoardGame(boardGameId)

        assertEquals(playsCount, plays.size)
    }

    @Test
    fun `should find plays for board game and sort them properly`() {
        val (opponent1, opponent2) = addOpponents(2)
        val playId1 = playUtil.addPlay(boardGameId, listOf(opponent1, opponent2), {opId, playId ->
            if (opId == opponent1) PlayResult(playId, opId, 12, 1)
            else PlayResult(playId, opId, 3, 2)
        })
        val playId2 = playUtil.addPlay(boardGameId, listOf(opponent1, opponent2), {opId, playId ->
            if (opId == opponent2) PlayResult(playId, opId, 12, 1)
            else PlayResult(playId, opId, 3, 2)
        })

        val plays: List<PlayResponse> = playDao.findPlaysForBoardGame(boardGameId)

        assertEquals(playId2, plays[0].id)
        assertEquals(playId1, plays[1].id)
        assertEquals(opponent2, plays[0].results[0].opponentId)
        assertEquals(opponent1, plays[1].results[0].opponentId)
    }

    @Test
    fun `should return empty list when no plays for board game`() {
        val boardGameId2 = boardGameUtil.addRandomBoardGame(userId)
        repeat(3) { playDao.insertPlayAlone(boardGameId2) }

        val plays = playDao.findPlaysForBoardGame(boardGameId)

        assertTrue(plays.isEmpty())
    }

    @Test
    fun `should find play results for single play`() {
        playUtil.addPlay(boardGameUtil.addRandomBoardGame(userId), addOpponents(2))
        val opponentsCount = 3
        val id = playUtil.addPlay(boardGameId, addOpponents(opponentsCount))

        val playResults = playDao.findPlayResultsForPlays(listOf(id))

        assertEquals(opponentsCount, playResults.size)
    }

    @Test
    fun `should find play results for multiple plays`() {
        val opponentsCount = 3
        val id1 = playUtil.addPlay(boardGameUtil.addRandomBoardGame(userId), addOpponents(opponentsCount))
        val id2 = playUtil.addPlay(boardGameId, addOpponents(opponentsCount))

        val playResults = playDao.findPlayResultsForPlays(listOf(id1, id2))

        assertEquals(opponentsCount * 2, playResults.size)
    }

    @Test
    fun `should insert with note`() {
        val note = randomAlphabetic(11)
        playUtil.addPlay(boardGameId, addOpponents(1), note = note)
        playUtil.addPlay(boardGameId, addOpponents(1))

        val plays = playDao.findPlaysForBoardGame(boardGameId)
                .sortedBy { it.id }

        assertEquals(note, plays[0].note)
        assertNull(plays[1].note)
    }

    @Test
    fun `should insert with date in past`() {
        val date = now().minusDays(5)
        playUtil.addPlay(boardGameId, addOpponents(2), date = date)

        val play = playDao.findPlaysForBoardGame(boardGameId)[0]

        assertEquals(date, play.date)
    }

    @Test
    fun `should insert with today's date when no date specified`() {
        val timeBeforeInsert = now().minusSeconds(10)
        playUtil.addPlay(boardGameId, addOpponents(2))

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
        val playId = playUtil.addPlay(boardGameId, addOpponents(1))
        val playId2 = playUtil.addPlay(boardGameId, addOpponents(1))

        playDao.delete(playId)

        val dbPlays = playDao.findPlaysForBoardGame(boardGameId)

        assertEquals(1, dbPlays.size)
        assertEquals(playId2, dbPlays[0].id)
    }


    @Test
    fun `should delete play with results`() {
        val playId = playUtil.addPlay(boardGameId, addOpponents(3))

        playDao.delete(playId)

        val dbPlays = playDao.findPlaysForBoardGame(boardGameId)
        val dbResults = playDao.findPlayResultsForPlays(listOf(playId))

        assertTrue(dbPlays.isEmpty())
        assertTrue(dbResults.isEmpty())
    }

    @Test
    fun `shouldn't delete opponents when deleting play with results`() {
        val opponentIds = addOpponents(1)
        val playId = playUtil.addPlay(boardGameId, opponentIds)

        playDao.delete(playId)

        val opponentsWithoutCreator = opponentDao.findAllOpponentsCreatedBy(userId)
                .filter { it.pointingToUser != userId }

        assertEquals(1, opponentsWithoutCreator.size)
        assertEquals(opponentIds[0], opponentsWithoutCreator[0].id)
    }

    private fun addOpponents(count: Int) =
        opponentsUtil.addOpponents(userId, count)


    private fun countPlayResults(playsIds: List<Id>): Int =
            playDao.findPlayResultsForPlays(playsIds).size

}
