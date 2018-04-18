package com.grudus.planshboard.user

import com.grudus.planshboard.AbstractControllerTest
import com.grudus.planshboard.USERS_URL
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

internal class UserControllerTest : AbstractControllerTest() {

    private val baseUrl = USERS_URL

    @Test
    fun `should get currently logged user`() {
        login()
        get("$baseUrl/current")
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.name").value(authentication.name))
    }
}