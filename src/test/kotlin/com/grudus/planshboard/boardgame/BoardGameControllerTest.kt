package com.grudus.planshboard.boardgame

import com.grudus.planshboard.AbstractControllerTest
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

    private val BASE_URL = "/api/board-games"

    @BeforeEach
    fun init() {
        login()
    }

    @Test
    fun `should save new game`() {
        val request = AddBoardGameRequest(randomAlphabetic(11))

        post(BASE_URL, request)
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.id", notNullValue()))
    }


    @Test
    fun `should not save game when already exists for user`() {
        val name = randomAlphabetic(11)

        post(BASE_URL, AddBoardGameRequest(name))
                .andExpect(status().isCreated)

        post(BASE_URL, AddBoardGameRequest(name))
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.codes", contains(RestKeys.NAME_EXISTS)))
    }

    @Test
    fun `should save game when exists for another user`() {
        val name = randomAlphabetic(11)

        post(BASE_URL, AddBoardGameRequest(name))
                .andExpect(status().isCreated)

        login()

        post(BASE_URL, AddBoardGameRequest(name))
                .andExpect(status().isCreated)
    }

    @Test
    fun `should find all board games for user`() {
        val count = 3
        (0 until count).map { randomAlphabetic(5 + it) }
                .forEach { post(BASE_URL, AddBoardGameRequest(it)) }

        get(BASE_URL)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.[*]", hasSize<Any>(count)))
    }

    @Test
    fun `should return empty list when no games for user`() {
        get(BASE_URL)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.[*]", hasSize<Any>(0)))
    }

    @Test
    fun `should find all only for given user`() {
        (0 until 5).map { randomAlphabetic(5 + it) }
                .forEach { post(BASE_URL, AddBoardGameRequest(it)) }

        login()
        post(BASE_URL, AddBoardGameRequest(randomAlphabetic(11)))

        get(BASE_URL)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.[*]", hasSize<Any>(1)))
    }

    @Test
    fun `should check if board game exists`() {
        val name = randomAlphabetic(13)
        post(BASE_URL, AddBoardGameRequest(name))

        get("$BASE_URL/exists", RequestParam("name", name))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.exists").value(true))
    }

    @Test
    fun `should detect board game not exist`() {
        val name = randomAlphabetic(13)
        post(BASE_URL, AddBoardGameRequest(name))

        get("$BASE_URL/exists", RequestParam("name", randomAlphabetic(11)))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.exists").value(false))
    }

    @Test
    fun `should detect board game not exist if exists for different user`() {
        val name = randomAlphabetic(13)
        post(BASE_URL, AddBoardGameRequest(name))

        login()

        get("$BASE_URL/exists", RequestParam("name", name))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.exists").value(false))
    }


    @Test
    fun `should delete board game`() {
        val name = randomAlphabetic(11)
        val id = post(BASE_URL, AddBoardGameRequest(name), IdResponse::class.java).id

        delete("$BASE_URL/$id")
                .andExpect(status().isNoContent)

        get("$BASE_URL/exists", RequestParam("name", name))
                .andExpect(jsonPath("$.exists").value(false))
    }


    @Test
    fun `should do nothing when delete non existing game `() {
        post(BASE_URL, AddBoardGameRequest(randomAlphabetic(11)), IdResponse::class.java).id

        delete("$BASE_URL/${nextLong()}")
                .andExpect(status().isNoContent)

        get(BASE_URL)
                .andExpect(jsonPath("$.[*]", hasSize<Any>(1)))
    }

    @Test
    fun `should not be able to delete someone else's board game`() {
        val id = post(BASE_URL, AddBoardGameRequest(randomAlphabetic(11)), IdResponse::class.java).id

        login()

        delete("$BASE_URL/$id")
                .andExpect(status().isForbidden)
    }

    @Test
    fun `should update game's name`() {
        val id = post(BASE_URL, AddBoardGameRequest(randomAlphabetic(11)), IdResponse::class.java).id
        val newName = randomAlphabetic(14)

        put("$BASE_URL/$id", EditBoardGameRequest(newName))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.name").value(newName))
    }


    @Test
    fun `should not be able to update not existing game`() {
        post(BASE_URL, AddBoardGameRequest(randomAlphabetic(11)), IdResponse::class.java).id
        val newName = randomAlphabetic(14)

        put("$BASE_URL/${nextLong()}", EditBoardGameRequest(newName))
                .andExpect(status().isNotFound)
    }

    @Test
    fun `should not be able to update someone else's game`() {
        val id = post(BASE_URL, AddBoardGameRequest(randomAlphabetic(11)), IdResponse::class.java).id
        val newName = randomAlphabetic(14)

        login()

        put("$BASE_URL/$id", EditBoardGameRequest(newName))
                .andExpect(status().isForbidden)
    }

}