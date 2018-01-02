package com.grudus.planshboard.boardgame

import com.grudus.planshboard.AbstractControllerTest
import com.grudus.planshboard.commons.RestKeys
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.hamcrest.Matchers
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

}