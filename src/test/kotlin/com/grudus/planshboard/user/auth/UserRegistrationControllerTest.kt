package com.grudus.planshboard.user.auth

import com.grudus.planshboard.AbstractControllerTest
import com.grudus.planshboard.user.UserService
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
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

    private val BASE_URL = "/api/auth/register"

    @Test
    fun `should create user without authentication`() {
        val user = AddUserRequest(randomAlphabetic(11), randomAlphabetic(11))
        postWithoutAuth(BASE_URL, user)
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.id", notNullValue()))

        assertTrue(userService.usernameExists(user.username))
    }

    @Test
    fun `should not create user with empty username`() {
        val user = AddUserRequest("", randomAlphabetic(11))
        postWithoutAuth(BASE_URL, user)
                .andExpect(status().isBadRequest)

        assertFalse(userService.usernameExists(user.username))
    }

    @Test
    fun `should not create user with empty password`() {
        val user = AddUserRequest(randomAlphabetic(11), "\t")
        postWithoutAuth(BASE_URL, user)
                .andExpect(status().isBadRequest)

        assertFalse(userService.usernameExists(user.username))
    }

    @Test
    fun `should not create user when already exists`() {
        login()
        val user = AddUserRequest(authentication.user.name, randomAlphabetic(11))
        postWithoutAuth(BASE_URL, user)
                .andExpect(status().isBadRequest)
    }
}