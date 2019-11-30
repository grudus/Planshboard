package com.grudus.planshboard.ranking

import com.grudus.planshboard.AbstractControllerTest
import com.grudus.planshboard.RANKING_URL
import com.grudus.planshboard.plays.model.PlayResult
import com.grudus.planshboard.utils.BoardGameUtil
import com.grudus.planshboard.utils.OpponentsUtil
import com.grudus.planshboard.utils.PlayUtil
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class RankingControllerTest
@Autowired
constructor(private val opponentsUtil: OpponentsUtil,
            private val playUtil: PlayUtil,
            private val boardGameUtil: BoardGameUtil) : AbstractControllerTest() {

    private val baseUrl = RANKING_URL
    private val boardGameId by lazy { boardGameUtil.addRandomBoardGame(authentication.userId) }

    @BeforeEach
    fun init() {
        login()
    }

    @Test
    fun `should return most frequent first position ranking`() {
        val opponent1 = opponentsUtil.addOpponents(authentication.userId, 1)[0]
        playUtil.addPlay(boardGameId, listOf(opponent1), { id, playId ->
            PlayResult(playId, id, 1, 1)
        })

        get("$baseUrl/most-frequent-first-position")
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.[*]", hasSize<Int>(2)))  // user-opponent always exists in db
                .andExpect(jsonPath("$.[0].opponentId", `is`(opponent1.toInt()))) // just junit things (1 != 1L)
                .andExpect(jsonPath("$.[0].numberOfFirstPositions", `is`(1)))
                .andExpect(jsonPath("$.[1].numberOfFirstPositions", `is`(0)))

    }
}
