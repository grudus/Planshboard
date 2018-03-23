package com.grudus.planshboard.plays.opponent

import com.grudus.planshboard.AbstractControllerTest
import com.grudus.planshboard.OPPONENTS_URL
import com.grudus.planshboard.commons.RestKeys
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class OpponentControllerTest : AbstractControllerTest() {

    val baseUrl = OPPONENTS_URL

    @BeforeEach
    fun init() {
        login()
    }


    @Test
    fun `should save new opponent and return it's id`() {
        post(baseUrl, AddOpponentRequest(randomAlphabetic(11)))
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.id", notNullValue()))
    }

    @Test
    fun `should not be able to save the same opponent twice`() {
        val name = randomAlphabetic(11)

        post(baseUrl, AddOpponentRequest(name))
                .andExpect(status().isCreated)

        post(baseUrl, AddOpponentRequest(name))
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.codes", hasItem(RestKeys.NAME_EXISTS)))
    }

    @Test
    fun `should not be able to save without name`() {
        post(baseUrl, AddOpponentRequest(""))
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.codes", hasItem(RestKeys.EMPTY_NAME)))
    }

    @Test
    fun `should be able to save with the same name for different user`() {
        val name = randomAlphabetic(11)

        post(baseUrl, AddOpponentRequest(name))
                .andExpect(status().isCreated)

        login()

        post(baseUrl, AddOpponentRequest(name))
                .andExpect(status().isCreated)
    }

    @Test
    fun `should find only current user as opponent if no other opponents`() {
        get(baseUrl)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.[*]", hasSize<Int>(1)))
                .andExpect(jsonPath("$.[0].name").value(authentication.user.name))
    }

    @Test
    fun `should find all opponents with current user`() {
        val opponentsWithoutCurrentUserCount = 3
        repeat(opponentsWithoutCurrentUserCount) {
            post(baseUrl, AddOpponentRequest(randomAlphabetic(11)))
        }

        get(baseUrl)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.[*]", hasSize<Int>(opponentsWithoutCurrentUserCount + 1)))
    }

}