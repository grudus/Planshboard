package com.grudus.planshboard.boardgame

import com.grudus.planshboard.AbstractControllerTest
import com.grudus.planshboard.BOARD_GAMES_URL
import com.grudus.planshboard.commons.IdResponse
import com.grudus.planshboard.commons.RestKeys
import com.grudus.planshboard.utils.RequestParam
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.apache.commons.lang3.RandomUtils.nextLong
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class BoardGameControllerTest : AbstractControllerTest() {

    private val baseUrl = BOARD_GAMES_URL

    @BeforeEach
    fun init() {
        login()
    }

    @Test
    fun `should save new game`() {
        val request = AddBoardGameRequest(randomAlphabetic(11))

        post(baseUrl, request)
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.id", notNullValue()))
    }


    @Test
    fun `should not save game when already exists for user`() {
        val name = randomAlphabetic(11)

        post(baseUrl, AddBoardGameRequest(name))
                .andExpect(status().isCreated)

        post(baseUrl, AddBoardGameRequest(name))
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.codes", contains(RestKeys.NAME_EXISTS)))
    }

    @Test
    fun `should save game when exists for another user`() {
        val name = randomAlphabetic(11)

        post(baseUrl, AddBoardGameRequest(name))
                .andExpect(status().isCreated)

        login()

        post(baseUrl, AddBoardGameRequest(name))
                .andExpect(status().isCreated)
    }

    @Test
    fun `should find all board games for user`() {
        val count = 3
        (0 until count).map { randomAlphabetic(5 + it) }
                .forEach { post(baseUrl, AddBoardGameRequest(it)) }

        get(baseUrl)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.[*]", hasSize<Any>(count)))
    }

    @Test
    fun `should return empty list when no games for user`() {
        get(baseUrl)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.[*]", hasSize<Any>(0)))
    }

    @Test
    fun `should find all only for given user`() {
        (0 until 5).map { randomAlphabetic(5 + it) }
                .forEach { post(baseUrl, AddBoardGameRequest(it)) }

        login()
        post(baseUrl, AddBoardGameRequest(randomAlphabetic(11)))

        get(baseUrl)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.[*]", hasSize<Any>(1)))
    }

    @Test
    fun `should check if board game exists`() {
        val name = randomAlphabetic(13)
        post(baseUrl, AddBoardGameRequest(name))

        get("$baseUrl/exists", RequestParam("name", name))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.exists").value(true))
    }

    @Test
    fun `should detect board game not exist`() {
        val name = randomAlphabetic(13)
        post(baseUrl, AddBoardGameRequest(name))

        get("$baseUrl/exists", RequestParam("name", randomAlphabetic(11)))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.exists").value(false))
    }

    @Test
    fun `should detect board game not exist if exists for different user`() {
        val name = randomAlphabetic(13)
        post(baseUrl, AddBoardGameRequest(name))

        login()

        get("$baseUrl/exists", RequestParam("name", name))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.exists").value(false))
    }


    @Test
    fun `should delete board game`() {
        val name = randomAlphabetic(11)
        val id = post(baseUrl, AddBoardGameRequest(name), IdResponse::class.java).id

        delete("$baseUrl/$id")
                .andExpect(status().isNoContent)

        get("$baseUrl/exists", RequestParam("name", name))
                .andExpect(jsonPath("$.exists").value(false))
    }


    @Test
    fun `should do nothing when delete non existing game `() {
        post(baseUrl, AddBoardGameRequest(randomAlphabetic(11)), IdResponse::class.java).id

        delete("$baseUrl/${nextLong()}")
                .andExpect(status().isNoContent)

        get(baseUrl)
                .andExpect(jsonPath("$.[*]", hasSize<Any>(1)))
    }

    @Test
    fun `should not be able to delete someone else's board game`() {
        val id = post(baseUrl, AddBoardGameRequest(randomAlphabetic(11)), IdResponse::class.java).id

        login()

        delete("$baseUrl/$id")
                .andExpect(status().isForbidden)
    }

    @Test
    fun `should update game's name`() {
        val id = post(baseUrl, AddBoardGameRequest(randomAlphabetic(11)), IdResponse::class.java).id
        val newName = randomAlphabetic(14)

        put("$baseUrl/$id", EditBoardGameRequest(newName))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.name").value(newName))
    }


    @Test
    fun `should not be able to update not existing game`() {
        post(baseUrl, AddBoardGameRequest(randomAlphabetic(11)), IdResponse::class.java).id
        val newName = randomAlphabetic(14)

        put("$baseUrl/${nextLong()}", EditBoardGameRequest(newName))
                .andExpect(status().isNotFound)
    }

    @Test
    fun `should not be able to update someone else's game`() {
        val id = post(baseUrl, AddBoardGameRequest(randomAlphabetic(11)), IdResponse::class.java).id
        val newName = randomAlphabetic(14)

        login()

        put("$baseUrl/$id", EditBoardGameRequest(newName))
                .andExpect(status().isForbidden)
    }

    @Test
    fun `should find by id`() {
        val gameRequest = AddBoardGameRequest(randomAlphabetic(11))
        val id = post(baseUrl, gameRequest, IdResponse::class.java).id

        get("$baseUrl/$id")
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(gameRequest.name))
    }

    @Test
    fun `should return 404 when accessing not existing board game`() {
        val gameRequest = AddBoardGameRequest(randomAlphabetic(11))
        post(baseUrl, gameRequest, IdResponse::class.java).id

        get("$baseUrl/${nextLong()}")
                .andExpect(status().isNotFound)
    }


    @Test
    fun `should not be able to get someone else's board game `() {
        val gameRequest = AddBoardGameRequest(randomAlphabetic(11))
        val id = post(baseUrl, gameRequest, IdResponse::class.java).id

        login()

        get("$baseUrl/$id")
                .andExpect(status().isForbidden)
    }

}