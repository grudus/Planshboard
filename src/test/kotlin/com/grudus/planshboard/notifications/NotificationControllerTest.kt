package com.grudus.planshboard.notifications

import com.grudus.planshboard.AbstractControllerTest
import com.grudus.planshboard.NOTIFICATIONS_URL
import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.plays.model.AddPlayResult
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.random.Random.Default.nextInt
import kotlin.random.Random.Default.nextLong

class NotificationControllerTest
@Autowired
constructor(private val notificationService: NotificationService) : AbstractControllerTest() {
    private val baseUrl = NOTIFICATIONS_URL

    private val userId by lazy { addUser().id!! }
    private val userId2 by lazy { addUser().id!! }

    @BeforeEach
    fun init() {
        login()
    }

    @Test
    fun `should get all notifications for user`() {
        saveNotification(availableFor = authentication.userId)
        saveNotification(availableFor = authentication.userId)
        saveNotification(availableFor = userId2)

        get(baseUrl)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.[*]", hasSize<Int>(2)))
    }

    @Test
    fun `should return empty list when no notifications`() {
        get(baseUrl)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.[*]", hasSize<Int>(0)))
    }

    @Test
    fun `should delete notification`() {
        val id = saveNotification(availableFor = authentication.userId)
        saveNotification(availableFor = authentication.userId)
        saveNotification(availableFor = authentication.userId)

        delete("$baseUrl/$id")
                .andExpect(status().isOk)

        get(baseUrl)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.[*]", hasSize<Int>(2)))
    }

    @Test
    fun `should not be able to delete someone else's notification`() {
        val id = saveNotification(availableFor = authentication.userId)
        login()

        delete("$baseUrl/$id")
                .andExpect(status().isForbidden)
    }

    @Test
    fun `should accept marked as opponent notification`() {
        val id = saveNotification(availableFor = authentication.userId)

        post("$baseUrl/$id/accept")
                .andExpect(status().isOk)

        get(baseUrl)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.[*]", hasSize<Int>(1)))
                .andExpect(jsonPath("$.[0].visited", `is`(true)))
    }

    @Test
    fun `should not be able to accept someone else's notification`() {
        val id = saveNotification(availableFor = authentication.userId)
        login()

        post("$baseUrl/$id/accept")
                .andExpect(status().isForbidden)
    }

    @Test
    fun `should reject marked as opponent notification`() {
        val id = saveNotification(availableFor = authentication.userId)

        post("$baseUrl/$id/reject")
                .andExpect(status().isOk)

        get(baseUrl)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.[*]", hasSize<Int>(1)))
                .andExpect(jsonPath("$.[0].visited", `is`(true)))
    }

    @Test
    fun `should not be able to reject someone else's notification`() {
        val id = saveNotification(availableFor = authentication.userId)
        login()

        post("$baseUrl/$id/reject")
                .andExpect(status().isForbidden)
    }

    private fun saveNotification(createdBy: Id = userId, availableFor: Id = userId2): Id {
        val result = AddPlayResult(randomAlphabetic(11), nextInt(), nextInt(), nextLong())
        return notificationService.saveUserMarkedAsOpponent(createdBy, availableFor, randomAlphabetic(11), result)
    }
}
