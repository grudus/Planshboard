package com.grudus.planshboard.user.auth

import com.grudus.planshboard.AbstractControllerTest
import com.grudus.planshboard.USERS_AUTH_REGISTRATION_URL
import com.grudus.planshboard.commons.RestKeys.EMPTY_PASSWORD
import com.grudus.planshboard.commons.RestKeys.EMPTY_USERNAME
import com.grudus.planshboard.commons.RestKeys.USERNAME_EXISTS
import com.grudus.planshboard.user.UserService
import com.grudus.planshboard.utils.RequestParam
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

internal class UserRegistrationControllerTest
@Autowired
constructor(private val userService: UserService) : AbstractControllerTest() {

    private val baseUrl = USERS_AUTH_REGISTRATION_URL

    @Test
    fun `should create user without authentication`() {
        val user = AddUserRequest(randomAlphabetic(11), randomAlphabetic(11))
        postWithoutAuth(baseUrl, user)
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.id", notNullValue()))

        assertTrue(userService.usernameExists(user.username))
    }

    @Test
    fun `should not create user with empty username`() {
        val user = AddUserRequest("", randomAlphabetic(11))
        postWithoutAuth(baseUrl, user)
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.codes", contains(EMPTY_USERNAME)))

        assertFalse(userService.usernameExists(user.username))
    }

    @Test
    fun `should not create user with empty password`() {
        val user = AddUserRequest(randomAlphabetic(11), "\t")
        postWithoutAuth(baseUrl, user)
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.codes", contains(EMPTY_PASSWORD)))

        assertFalse(userService.usernameExists(user.username))
    }

    @Test
    fun `should not create user when already exists`() {
        login()
        val user = AddUserRequest(authentication.name, randomAlphabetic(11))
        postWithoutAuth(baseUrl, user)
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.codes", contains(USERNAME_EXISTS)))
    }

    @Test
    fun `should check if username already exists`() {
        val user = AddUserRequest(randomAlphabetic(11), randomAlphabetic(11))
        postWithoutAuth(baseUrl, user)

        getWithoutAuth("$baseUrl/exists", RequestParam("username", user.username))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.exists").value(true))
    }


    @Test
    fun `should detect username doesn't exists`() {
        val user = AddUserRequest(randomAlphabetic(11), randomAlphabetic(11))
        postWithoutAuth(baseUrl, user)

        getWithoutAuth("$baseUrl/exists", RequestParam("username", randomAlphabetic(12)))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.exists").value(false))
    }

    @Test
    fun `should return 400 when checking if user exists without username`() {
        val user = AddUserRequest(randomAlphabetic(11), randomAlphabetic(11))
        postWithoutAuth(baseUrl, user)

        getWithoutAuth("$baseUrl/exists")
                .andExpect(status().isBadRequest)
    }
}