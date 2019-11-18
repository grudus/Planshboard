package com.grudus.planshboard.plays.opponent

import com.grudus.planshboard.AbstractControllerTest
import com.grudus.planshboard.OPPONENTS_URL
import com.grudus.planshboard.commons.IdResponse
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
                .andExpect(jsonPath("$.[0].name").value(authentication.name))
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

    @Test
    fun `should be able to save new opponent without pointing to user`() {
        val name = randomAlphabetic(11)

        post("${baseUrl}?withUser=true", SaveConnectedOpponentRequest(name))
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.id", notNullValue()))
    }

    @Test
    fun `should be able to save new opponent pointing to user`() {
        val name = randomAlphabetic(11)
        val newUserName = addUser().name

        post("${baseUrl}?withUser=true", SaveConnectedOpponentRequest(name, newUserName))
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.id", notNullValue()))
    }

    @Test
    fun `should not be able to save new opponent pointing to already pointed user`() {
        val name = randomAlphabetic(11)
        val newUserName = addUser().name

        post("${baseUrl}?withUser=true", SaveConnectedOpponentRequest(name, newUserName))
                .andExpect(status().isCreated)

        post("${baseUrl}?withUser=true", SaveConnectedOpponentRequest(randomAlphabetic(11), newUserName))
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.codes", hasItem(RestKeys.USER_ASSIGNED_TO_ANOTHER_OPPONENT)))
    }

    @Test
    fun `should be able to save new opponent pointing to user pointed by another user`() {
        val name = randomAlphabetic(11)
        val newUserName = addUser().name

        post("${baseUrl}?withUser=true", SaveConnectedOpponentRequest(name, newUserName))
                .andExpect(status().isCreated)

        login()

        post("${baseUrl}?withUser=true", SaveConnectedOpponentRequest(name, newUserName))
                .andExpect(status().isCreated)
    }

    @Test
    fun `should be able to edit opponent name`() {
        val name = randomAlphabetic(11)
        val opponentId = addOpponent()

        put("${baseUrl}?withUser=true", SaveConnectedOpponentRequest(name, existingOpponentId = opponentId))
                .andExpect(status().isOk)
    }


    @Test
    fun `should be able to assign user to existing opponent`() {
        val name = randomAlphabetic(11)
        val opponentId = addOpponent(name)
        val newUserName = addUser().name

        put("${baseUrl}?withUser=true", SaveConnectedOpponentRequest(name, connectedUserName = newUserName, existingOpponentId = opponentId))
                .andExpect(status().isOk)
    }

    @Test
    fun `should be able to set non unique opponent name`() {
        val name = randomAlphabetic(11)
        val name2 = randomAlphabetic(11)
        addOpponent(name)
        val opponentId = addOpponent(name2)

        put("${baseUrl}?withUser=true", SaveConnectedOpponentRequest(name, existingOpponentId = opponentId))
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.codes", hasItem(RestKeys.NAME_EXISTS)))
    }

    @Test
    fun `should not be able to edit opponent pointing to user pointed by another user`() {
        val name = randomAlphabetic(11)
        val name2 = randomAlphabetic(11)
        val opponentId = addOpponent(name)
        val opponentId2 = addOpponent(name2)
        val newUserName = addUser().name

        put("${baseUrl}?withUser=true", SaveConnectedOpponentRequest(name, connectedUserName = newUserName, existingOpponentId = opponentId))
                .andExpect(status().isOk)

        put("${baseUrl}?withUser=true", SaveConnectedOpponentRequest(name2, connectedUserName = newUserName, existingOpponentId = opponentId2))
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.codes", hasItem(RestKeys.USER_ASSIGNED_TO_ANOTHER_OPPONENT)))
    }

    @Test
    fun `should not be able to edit someone else's opponent`() {
        val opponentId = addOpponent()

        login()

        put("${baseUrl}?withUser=true", SaveConnectedOpponentRequest(randomAlphabetic(2), existingOpponentId = opponentId))
                .andExpect(status().isForbidden)
    }

    private fun addOpponent(name: String = randomAlphabetic(11)): Long =
            post("${baseUrl}?withUser=true", SaveConnectedOpponentRequest(name), IdResponse::class.java)
                    .id

}
