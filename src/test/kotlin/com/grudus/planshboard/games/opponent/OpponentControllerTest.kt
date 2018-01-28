package com.grudus.planshboard.games.opponent

import com.grudus.planshboard.AbstractControllerTest
import com.grudus.planshboard.commons.RestKeys
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class OpponentControllerTest : AbstractControllerTest() {


    private val BASE_URL = "/api/opponents"

    @BeforeEach
    fun init() {
        login()
    }

    @Test
    fun `should save new opponent and return it's id`() {
        post(BASE_URL, AddOpponentRequest(randomAlphabetic(11)))
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.id", notNullValue()))
    }

    @Test
    fun `should not be able to save the same opponent twice`() {
        val name = randomAlphabetic(11)

        post(BASE_URL, AddOpponentRequest(name))
                .andExpect(status().isCreated)

        post(BASE_URL, AddOpponentRequest(name))
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.codes", hasItem(RestKeys.NAME_EXISTS)))
    }

    @Test
    fun `should not be able to save without name`() {
        post(BASE_URL, AddOpponentRequest(""))
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.codes", hasItem(RestKeys.EMPTY_NAME)))
    }

    @Test
    fun `should be able to save with the same name for different user`() {
        val name = randomAlphabetic(11)

        post(BASE_URL, AddOpponentRequest(name))
                .andExpect(status().isCreated)

        login()

        post(BASE_URL, AddOpponentRequest(name))
                .andExpect(status().isCreated)
    }
}