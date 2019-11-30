package com.grudus.planshboard.notifications

import com.fasterxml.jackson.databind.ObjectMapper
import com.grudus.planshboard.MockitoExtension
import com.grudus.planshboard.notifications.model.Notification
import com.grudus.planshboard.plays.model.AddPlayResult
import com.nhaarman.mockitokotlin2.argumentCaptor
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import kotlin.random.Random.Default.nextInt
import kotlin.random.Random.Default.nextLong

@ExtendWith(MockitoExtension::class)
class NotificationServiceTest {

    @Mock
    private lateinit var notificationDao: NotificationDao
    private lateinit var objectMapper: ObjectMapper

    private lateinit var notificationService: NotificationService

    @BeforeEach
    fun init() {
        objectMapper = ObjectMapper()
        notificationService = NotificationService(notificationDao, objectMapper)
    }

    @Test
    fun `should correctly map to notification when saving marked as opponent`() {
        val notificationCaptor = argumentCaptor<Notification>()
        val creatorId = nextLong()
        val availableForId = nextLong()
        val playCreatorName = randomAlphabetic(11)
        val result = AddPlayResult(randomAlphabetic(11), nextInt(), nextInt(), nextLong())

        notificationService.saveUserMarkedAsOpponent(creatorId, availableForId, playCreatorName, result)
        verify(notificationDao).save(notificationCaptor.capture())

        val notification = notificationCaptor.firstValue

        assertEquals(creatorId, notification.createdBy)
        assertEquals(availableForId, notification.availableFor)
        assertEquals(false, notification.visited)
        assertEquals(Notification.Type.MARKED_AS_OPPONENT, notification.type)
        assertEquals(playCreatorName, notification.additionalData!!["playCreatorName"].textValue())
        assertEquals(result.points, notification.additionalData!!["points"].intValue())
        assertEquals(result.position, notification.additionalData!!["position"].intValue())
    }
}
