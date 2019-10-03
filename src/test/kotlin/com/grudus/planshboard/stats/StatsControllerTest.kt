package com.grudus.planshboard.stats

import com.grudus.planshboard.AbstractControllerTest
import com.grudus.planshboard.STATS_URL
import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.plays.model.PlayResult
import com.grudus.planshboard.stats.models.StatsDto
import com.grudus.planshboard.utils.BoardGameUtil
import com.grudus.planshboard.utils.OpponentsUtil
import com.grudus.planshboard.utils.PlayUtil
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class StatsControllerTest
@Autowired
constructor(
        private val opponentsUtil: OpponentsUtil,
        private val boardGameUtil: BoardGameUtil,
        private val playsUtil: PlayUtil
) : AbstractControllerTest() {

    private val baseUrl = STATS_URL
    private val userId by lazy { authentication.userId }

    @BeforeEach
    fun init() {
        login()
    }

    @Test
    fun `should return valid result when no games`() {
        get(baseUrl)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.boardGamesCount").value(0))
                .andExpect(jsonPath("$.allPlaysCount").value(0))
                .andExpect(jsonPath("$.playPositionsPerOpponentCount", empty<Any>()))
                .andExpect(jsonPath("$.playsPerBoardGameCount", empty<Any>()))
    }


    @Test
    fun `should count board games`() {
        val boardGamesCount = 5
        boardGamesWithPlays(boardGamesCount)

        get(baseUrl)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.boardGamesCount").value(boardGamesCount))
                .andExpect(jsonPath("$.allPlaysCount").value(boardGamesCount))
                .andExpect(jsonPath("$.playPositionsPerOpponentCount", hasSize<Int>(1)))
                .andExpect(jsonPath("$.playsPerBoardGameCount", hasSize<Int>(boardGamesCount)))
    }


    @Test
    fun `should count plays for different games`() {
        val gamesCount = 3
        val playsCount = 5
        val boardGames = boardGames(gamesCount)

        repeat(gamesCount) { i ->
            playsUtil.addPlays(boardGames[i], opponentsWithUser(), playsCount)
        }

        get(baseUrl)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.boardGamesCount").value(gamesCount))
                .andExpect(jsonPath("$.allPlaysCount").value(gamesCount * playsCount))
                .andExpect(jsonPath("$.playsPerBoardGameCount", hasSize<Int>(gamesCount)))
                .andExpect(jsonPath("$.playsPerBoardGameCount.[0].count", `is`(playsCount)))
                .andExpect(jsonPath("$.playsPerBoardGameCount.[1].count", `is`(playsCount)))
                .andExpect(jsonPath("$.playsPerBoardGameCount.[2].count", `is`(playsCount)))
    }

    @Test
    fun `should count opponent's positions`() {
        val numberOfOpponents = 2
        val opponents = opponentsUtil.addOpponents(userId, numberOfOpponents)

        listOf(
                mapOf(opponents[0] to 1, opponents[1] to 2),
                mapOf(opponents[0] to 1, opponents[1] to 2),
                mapOf(opponents[0] to 2, opponents[1] to 1),
                mapOf(opponents[0] to 1, opponents[1] to 2)
        ).forEach { result: Map<Id, Int> ->
            playsUtil.addPlay(boardGames()[0], opponents, { id, playId ->
                PlayResult(playId, id, null, result.getValue(id))
            })
        }

        val stats = getAndReturn(baseUrl, StatsDto::class.java)
        val count = {id: Id -> stats.playPositionsPerOpponentCount.find { it.opponent.id == id }?.count}

        assertEquals(2, stats.playPositionsPerOpponentCount.size )
        assertEquals(3, count(opponents[0]))
        assertEquals(1, count(opponents[1]))
    }


    private fun opponentsWithUser(count: Int = 5): List<Id> =
            opponentsUtil.addOpponents(userId, count - 1) + opponentsUtil.asOpponent(userId)

    private fun boardGamesWithPlays(count: Int = 1): List<Id> =
            boardGameUtil.addRandomBoardGamesWithPlay(userId, count)

    private fun boardGames(count: Int = 1): List<Id> =
            boardGameUtil.addRandomBoardGames(userId, count)
}
