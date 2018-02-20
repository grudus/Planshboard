package com.grudus.planshboard.games

import com.grudus.planshboard.AbstractControllerTest
import com.grudus.planshboard.boardgame.BoardGameService
import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.commons.IdResponse
import com.grudus.planshboard.games.opponent.AddOpponentRequest
import com.grudus.planshboard.games.opponent.OpponentService
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.apache.commons.lang3.RandomUtils.nextLong
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class GameControllerTest
@Autowired
constructor(private val boardGameService: BoardGameService,
            private val opponentService: OpponentService) : AbstractControllerTest() {

    private val BASE_URL = "/api/games"

    private val boardGameId: Id by lazy {
        boardGameService.createNew(authentication.userId, randomAlphabetic(11))
    }

    @BeforeEach
    fun init() {
        login()
    }

    @Test
    fun `should save game`() {
        val request = AddGameRequest(boardGameId, addOpponents(3))

        post(BASE_URL, request)
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.id", notNullValue()))
    }

    @Test
    fun `should find all opponents for game`() {
        val opponents = addOpponents(3)
        val gameId = addGame(boardGameId, opponents)

        get("$BASE_URL/$gameId/opponents")
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.[*]", hasSize<Int>(opponents.size)))
                .andExpect(jsonPath("$.[0].id", notNullValue()))
                .andExpect(jsonPath("$.[0].name", notNullValue()))
    }

    @Test
    fun `should return 404 when finding opponents for game which not exists`() {
        get("$BASE_URL/${nextLong()}/opponents")
                .andExpect(status().isNotFound)
    }

    @Test
    fun `should not be able to get someone else's opponents`() {
        val opponents = addOpponents(3)
        val gameId = addGame(boardGameId, opponents)

        login()

        get("$BASE_URL/$gameId/opponents")
                .andExpect(status().isForbidden)
    }

    private fun addGame(boardGameId: Id, opponents: List<Id>): Id =
            post(BASE_URL, AddGameRequest(boardGameId, opponents), IdResponse::class.java).id

    private fun addOpponents(count: Int): List<Id> =
            (0 until count).map {
                opponentService.addOpponent(
                        authentication.userId,
                        AddOpponentRequest(randomAlphabetic(23))
                )
            }
}