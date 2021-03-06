package com.grudus.planshboard.plays

import com.grudus.planshboard.AbstractControllerTest
import com.grudus.planshboard.OPPONENTS_URL
import com.grudus.planshboard.boardgame.BoardGameService
import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.commons.IdResponse
import com.grudus.planshboard.commons.RestKeys.NO_RESULTS
import com.grudus.planshboard.plays.model.SavePlayRequest
import com.grudus.planshboard.plays.model.AddPlayResult
import com.grudus.planshboard.plays.opponent.OpponentNameId
import com.grudus.planshboard.plays.opponent.OpponentService
import com.grudus.planshboard.playsUrlPattern
import com.grudus.planshboard.utils.randomStrings
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.apache.commons.lang3.RandomUtils.nextLong
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime.now
import java.time.LocalDateTime.of
import java.time.format.DateTimeFormatter

class PlayControllerTest
@Autowired
constructor(private val boardGameService: BoardGameService,
            private val opponentService: OpponentService) : AbstractControllerTest() {

    private val boardGameId: Id by lazy { newBoardGame() }
    private val opponentsUrl = OPPONENTS_URL

    private fun baseUrl(boardGameId: Id) = playsUrlPattern(boardGameId)

    @BeforeEach
    fun init() {
        login()
    }

    @Test
    fun `should save play`() {
        val request = SavePlayRequest(addOpponents(3))

        post(baseUrl(boardGameId), request)
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.id", notNullValue()))
    }

    @Test
    fun `should not be able to save play without results`() {
        val request = SavePlayRequest(emptyList())

        post(baseUrl(boardGameId), request)
                .andExpect(status().isBadRequest)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("codes", hasItem(NO_RESULTS)))
    }

    @Test
    fun `should be able to create play with date in past`() {
        val date = now().minusDays(5)
        val dateString: String = date.format(DateTimeFormatter.ISO_DATE_TIME)
        val request = SavePlayRequest(addOpponents(3), date)

        post(baseUrl(boardGameId), request)
                .andExpect(status().isCreated)

        get("${baseUrl(boardGameId)}/results")
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.[0].date").value(dateString))
                .andExpect(jsonPath("$.[0].note", nullValue()))
    }

    @Test
    fun `should be able to create play with note`() {
        val note = randomAlphabetic(11)
        val request = SavePlayRequest(addOpponents(2), now(), note)

        post(baseUrl(boardGameId), request)
                .andExpect(status().isCreated)

        get("${baseUrl(boardGameId)}/results")
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.[0].note").value(note))
    }

    @Test
    fun `should create opponent if saving play with opponent without id`() {
        val opponentName = randomAlphabetic(11)
        val request = SavePlayRequest(listOf(AddPlayResult(opponentName, 1)))

        post(baseUrl(boardGameId), request)
                .andExpect(status().isCreated)

        get(opponentsUrl)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.[*]", hasSize<Int>(2)))
                .andExpect(jsonPath("$.[*].name", hasItem(opponentName)))
    }

    @Test
    fun `should update play`() {
        val playId = addPlay(boardGameId, addOpponents(3))

        val updateRequest = SavePlayRequest(addOpponents(2), now().minusDays(1), randomAlphabetic(11))

        put("${baseUrl(boardGameId)}/$playId", updateRequest)
                .andExpect(status().isOk)

        get("${baseUrl(boardGameId)}/results")
                .andExpect(status().isOk)
                .andPrint()
                .andExpect(jsonPath("$.[0].note").value(updateRequest.note!!))
    }

    @Test
    fun `validation should work when updating play`() {
        val playId = addPlay(boardGameId, addOpponents(3))

        val updateRequest = SavePlayRequest(emptyList())

        put("${baseUrl(boardGameId)}/$playId", updateRequest)
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.codes", hasItem(NO_RESULTS)))
    }

    @Test
    fun `should not be able to update someone else's play`() {
        val playId = addPlay(boardGameId, addOpponents(3))

        login()

        put("${baseUrl(boardGameId)}/$playId", SavePlayRequest(addOpponents(2)))
                .andExpect(status().isForbidden)
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
    fun `should find play results and sort them by date`() {
        post(baseUrl(boardGameId), SavePlayRequest(addOpponents(3), now().minusDays(4)))
        post(baseUrl(boardGameId), SavePlayRequest(addOpponents(2), now()))
        post(baseUrl(boardGameId), SavePlayRequest(addOpponents(1), now().minusYears(1)))

        get("${baseUrl(boardGameId)}/results")
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.[*]", hasSize<Int>(3)))
                .andExpect(jsonPath("$.[*].date", notNullValue()))
                .andExpect(jsonPath("$.[*].id", notNullValue()))
                .andExpect(jsonPath("$.[0].results", hasSize<Int>(2)))
                .andExpect(jsonPath("$.[1].results", hasSize<Int>(3)))
                .andExpect(jsonPath("$.[2].results", hasSize<Int>(1)))
    }

    @Test
    fun `should find play result for game`() {
        post(baseUrl(boardGameId), SavePlayRequest(addOpponents(3)))
        post(baseUrl(newBoardGame()), SavePlayRequest(addOpponents(2)))

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
        post(baseUrl(boardGameId), SavePlayRequest(addOpponents(3)))
        login()

        get("${baseUrl(boardGameId)}/results")
                .andExpect(status().isForbidden)
    }

    @Test
    fun `should return 404 when finding play results for non existing board game`() {
        post(baseUrl(boardGameId), SavePlayRequest(addOpponents(3)))

        get("${baseUrl(nextLong())}/results")
                .andExpect(status().isNotFound)
    }

    @Test
    fun `should be able to delete play`() {
        val playId = addPlay(boardGameId, addOpponents(3))
        val playId2 = addPlay(boardGameId, addOpponents(6))

        delete("${baseUrl(boardGameId)}/$playId")
                .andExpect(status().isNoContent)

        get("${baseUrl(boardGameId)}/results")
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.[*]", hasSize<Int>(1)))
                .andExpect(jsonPath("$.[0].id").value(playId2))
    }

    @Test
    fun `should not be able to delete someone else's play`() {
        val playId = addPlay(boardGameId, addOpponents(3))
        login()

        delete("${baseUrl(boardGameId)}/$playId")
                .andExpect(status().isForbidden)
    }

    @Test
    fun `should do nothing when deleting no existing play`() {
        addPlay(boardGameId, addOpponents(3))

        delete("${baseUrl(boardGameId)}/${nextLong()}")
                .andExpect(status().isNoContent)

        get("${baseUrl(boardGameId)}/results")
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.[*]", hasSize<Int>(1)))
    }

    private fun newBoardGame() =
            boardGameService.createNew(authentication.userId, randomAlphabetic(11))


    private fun addPlay(boardGameId: Id, results: List<AddPlayResult>): Id =
            post(baseUrl(boardGameId), SavePlayRequest(results), IdResponse::class.java).id


    private fun addOpponents(count: Int): List<AddPlayResult> =
            randomStrings(count).map { name ->
                OpponentNameId(name, opponentService.addOpponent(authentication.userId, name))
            }.mapIndexed { index, (name, id) -> AddPlayResult(name, index, opponentId = id) }
}
