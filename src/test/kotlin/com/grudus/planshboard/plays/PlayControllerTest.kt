package com.grudus.planshboard.plays

import com.grudus.planshboard.AbstractControllerTest
import com.grudus.planshboard.boardgame.BoardGameService
import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.commons.IdResponse
import com.grudus.planshboard.plays.model.AddPlayRequest
import com.grudus.planshboard.plays.model.AddPlayResult
import com.grudus.planshboard.plays.opponent.OpponentNameId
import com.grudus.planshboard.plays.opponent.OpponentService
import com.grudus.planshboard.utils.randomStrings
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.apache.commons.lang3.RandomUtils.nextLong
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class PlayControllerTest
@Autowired
constructor(private val boardGameService: BoardGameService,
            private val opponentService: OpponentService) : AbstractControllerTest() {

    private val boardGameId: Id by lazy { newBoardGame() }

    private fun baseUrl(boardGameId: Id) = "/api/board-games/$boardGameId/plays"

    @BeforeEach
    fun init() {
        login()
    }

    @Test
    fun `should save play`() {
        val request = AddPlayRequest(addOpponents(3))

        post(baseUrl(boardGameId), request)
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.id", notNullValue()))
    }

    @Test
    fun `should find all opponents for play`() {
        val opponents = addOpponents(3)
        val playId = addPlay(boardGameId, opponents)

        get("${baseUrl(boardGameId)}/$playId/opponents")
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.[*]", hasSize<Int>(opponents.size)))
                .andExpect(jsonPath("$.[0].id", notNullValue()))
                .andExpect(jsonPath("$.[0].name", notNullValue()))
    }

    @Test
    fun `should return 404 when finding opponents for play which not exists`() {
        get("${baseUrl(boardGameId)}/${nextLong()}/opponents")
                .andExpect(status().isNotFound)
    }

    @Test
    fun `should not be able to get someone else's opponents`() {
        val opponents = addOpponents(3)
        val playId = addPlay(boardGameId, opponents)

        login()

        get("${baseUrl(boardGameId)}/$playId/opponents")
                .andExpect(status().isForbidden)
    }


    @Test
    fun `should find play results`() {
        post(baseUrl(boardGameId), AddPlayRequest(addOpponents(3)))
        post(baseUrl(boardGameId), AddPlayRequest(addOpponents(2)))
        post(baseUrl(boardGameId), AddPlayRequest(addOpponents(1)))

        get("${baseUrl(boardGameId)}/results")
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.[*]", hasSize<Int>(3)))
                .andExpect(jsonPath("$.[*].date", notNullValue()))
                .andExpect(jsonPath("$.[*].id", notNullValue()))
                .andExpect(jsonPath("$.[0].results", hasSize<Int>(3)))
                .andExpect(jsonPath("$.[1].results", hasSize<Int>(2)))
                .andExpect(jsonPath("$.[2].results", hasSize<Int>(1)))
    }

    @Test
    fun `should find play result for game`() {
        post(baseUrl(boardGameId), AddPlayRequest(addOpponents(3)))
        post(baseUrl(newBoardGame()), AddPlayRequest(addOpponents(2)))

        get("${baseUrl(boardGameId)}/results")
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.[*]", hasSize<Int>(1)))
                .andExpect(jsonPath("$.[0].results", hasSize<Int>(3)))
    }

    @Test
    fun `should return empty list when no plays for board game`() {
        get("${baseUrl(boardGameId)}/results")
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.[*]", hasSize<Int>(0)))
    }

    @Test
    fun `should not be able to see someone else's play results`() {
        post(baseUrl(boardGameId), AddPlayRequest(addOpponents(3)))
        login()

        get("${baseUrl(boardGameId)}/results")
                .andExpect(status().isForbidden)
    }

    @Test
    fun `should return 404 when finding play results for non existing board game`() {
        post(baseUrl(boardGameId), AddPlayRequest(addOpponents(3)))

        get("${baseUrl(nextLong())}/results")
                .andExpect(status().isNotFound)
    }

    private fun newBoardGame() =
            boardGameService.createNew(authentication.userId, randomAlphabetic(11))


    private fun addPlay(boardGameId: Id, results: List<AddPlayResult>): Id =
            post(baseUrl(boardGameId), AddPlayRequest(results), IdResponse::class.java).id


    private fun addOpponents(count: Int): List<AddPlayResult> =
            randomStrings(count).map { name ->
                OpponentNameId(name, opponentService.addOpponent(authentication.userId, name))
            }.mapIndexed { index, (name, id) -> AddPlayResult(name, index, opponentId = id) }
}