package com.grudus.planshboard.plays

import com.grudus.planshboard.AbstractSpringServiceTest
import com.grudus.planshboard.boardgame.BoardGameService
import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.plays.model.AddPlayResult
import com.grudus.planshboard.plays.model.SavePlayRequest
import com.grudus.planshboard.plays.opponent.OpponentNameId
import com.grudus.planshboard.plays.opponent.OpponentService
import com.grudus.planshboard.utils.randomStrings
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

class PlayServiceSpringTest
@Autowired
constructor(private val boardGameService: BoardGameService,
            private val opponentService: OpponentService,
            private val playService: PlayService) : AbstractSpringServiceTest() {


    private val userId: Id by lazy {
        addUser().id!!
    }
    private val boardGameId by lazy {
        boardGameService.createNew(userId, randomAlphabetic(11))
    }


    @Test
    fun `should insert play when all opponents exists`() {
        val playOpponents = randomPlayOpponents(5)

        val id = playService.savePlay(userId, boardGameId, SavePlayRequest(playOpponents))

        assertNotNull(id)
    }

    @Test
    fun `should insert new opponents and play when no opponent exists`() {
        val playOpponents = opponentsWithoutDb(3)
                .mapIndexed { i, (name, _) -> AddPlayResult(name, i) }

        val id = playService.savePlay(userId, boardGameId, SavePlayRequest(playOpponents))

        assertNotNull(id)

        playOpponents.forEach { (name) ->
            assertTrue(opponentService.exists(userId, name))
        }
    }

    @Test
    fun `should insert play when mixed existing opponents and new`() {
        val playOpponents = (addOpponentsToDb(3) + opponentsWithoutDb(5))
                .mapIndexed { i, (name, id) -> AddPlayResult(name, i, opponentId = id) }

        val id = playService.savePlay(userId, boardGameId, SavePlayRequest(playOpponents))

        assertNotNull(id)

        playOpponents.forEach { (name) ->
            assertTrue(opponentService.exists(userId, name))
        }
    }


    @Test
    fun `should save play results`() {
        val opponentsCount = 5
        val playOpponents = randomPlayOpponents(opponentsCount)

        playService.savePlay(userId, boardGameId, SavePlayRequest(playOpponents))

        val opponentsCountForPlay = playService.getPlayResults(userId, boardGameId)[0].results.size
        assertEquals(opponentsCount, opponentsCountForPlay)
    }

    @Test
    fun `should update existing play`() {
        val opponents = randomPlayOpponents(1)
        val playId = playService.savePlay(userId, boardGameId, SavePlayRequest(opponents))
        val note = randomAlphabetic(11)
        val date = LocalDateTime.now().minusDays(55)

        playService.updatePlay(userId, playId, SavePlayRequest(opponents, date, note))

        val updatedPlay = playService.getPlayResults(userId, boardGameId)[0]

        assertEquals(note, updatedPlay.note)
        assertEquals(date, updatedPlay.date)
        assertEquals(playId, updatedPlay.id)
    }

    @Test
    fun `should completely replace opponents when update play with new opponents`() {
        val oldOpponents = randomPlayOpponents(1)
        val playId = playService.savePlay(userId, boardGameId, SavePlayRequest(oldOpponents))

        playService.updatePlay(userId, playId, SavePlayRequest(randomPlayOpponents(3)))

        val updatedPlay = playService.getPlayResults(userId, boardGameId)[0]

        assertEquals(3, updatedPlay.results.size)
        assertTrue(updatedPlay.results.none { it.opponentName === oldOpponents[0].opponentName })
    }

    @Test
    fun `should find plays`() {
        val count = 4
        val playOpponents = randomPlayOpponents(count)
        val playOpponents2 = randomPlayOpponents(count+1)

        playService.savePlay(userId, boardGameId, SavePlayRequest(playOpponents))
        playService.savePlay(userId, boardGameId, SavePlayRequest(playOpponents))
        playService.savePlay(userId, boardGameService.createNew(userId, "a"), SavePlayRequest(playOpponents2))

        val playResults = playService.getPlayResults(userId, boardGameId)

        assertEquals(2, playResults.size)
        assertEquals(count, playResults[0].results.size)
    }

    @Test
    fun `should find specific play results`() {
        val points = listOf(32, 22)
        val position = listOf(1, 2)
        val names = listOf(randomAlphabetic(11), randomAlphabetic(11))
        val playOpponent1 = AddPlayResult(names[0], position[0], points[0])
        val playOpponent2 = AddPlayResult(names[1], position[1], points[1])

        playService.savePlay(userId, boardGameId, SavePlayRequest(listOf(playOpponent1, playOpponent2)))

        val results = playService.getPlayResults(userId, boardGameId)[0].results
                .sortedBy { it.position }

        assertEquals(points[0], results[0].points)
        assertEquals(position[0], results[0].position)
        assertEquals(names[0], results[0].opponentName)
        assertNotNull(results[0].opponentId)
        assertEquals(points[1], results[1].points)
        assertEquals(position[1], results[1].position)
        assertEquals(names[1], results[1].opponentName)
        assertNotNull(results[1].opponentId)
    }

    private fun randomPlayOpponents(count: Int): List<AddPlayResult> = addOpponentsToDb(count)
            .mapIndexed { i, (name, id) -> AddPlayResult(name, i, opponentId = id) }


    private fun addOpponentsToDb(count: Int): List<OpponentNameId> =
            randomStrings(count)
                    .map { name ->
                        val id = opponentService.addOpponent(userId, name)
                        OpponentNameId(name, id)
                    }

    private fun opponentsWithoutDb(count: Int): List<OpponentNameId> =
            randomStrings(count)
                    .map { name -> OpponentNameId(name) }
}
