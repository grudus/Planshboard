package com.grudus.planshboard.user

import com.grudus.planshboard.AbstractControllerTest
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

internal class UserControllerTest : AbstractControllerTest() {

    private val BASE_URL = "/api/users"

    @Test
    fun `should get currently logged user`() {
        login()
        get("$BASE_URL/current")
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.name").value(authentication.user.name))
    }
}