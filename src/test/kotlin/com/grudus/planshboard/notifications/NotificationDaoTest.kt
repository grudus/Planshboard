package com.grudus.planshboard.notifications

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.grudus.planshboard.AbstractDatabaseTest
import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.notifications.model.Notification
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.jooq.exception.DataAccessException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

class NotificationDaoTest
@Autowired
constructor(private val notificationDao: NotificationDao) : AbstractDatabaseTest() {
    private val userId by lazy { addUser().id!! }
    private val userId2 by lazy { addUser().id!! }


    @Test
    fun `should save notification and returns id`() {
        val notification = randomNotification()

        val id = notificationDao.save(notification)

        assertNotNull(id)
    }

    @Test
    fun `should not be able to save notification with invalid user`() {
        val notification = randomNotification(availableFor = -1L)

        assertThrows(DataAccessException::class.java)
        { notificationDao.save(notification) }
    }

    @Test
    fun `should save notification with additional json data`() {
        val json = jacksonObjectMapper().createObjectNode()
                .put(randomAlphabetic(11), randomAlphabetic(11))
        val notification = randomNotification()
                .copy(additionalData = json)

        val id = notificationDao.save(notification)

        assertNotNull(id)
    }

    @Test
    fun `should find all notifications for user`() {
        notificationDao.save(randomNotification(availableFor = userId))
        notificationDao.save(randomNotification(availableFor = userId))
        notificationDao.save(randomNotification(availableFor = userId2))

        val allForUser = notificationDao.findAllForUser(userId)

        assertEquals(2, allForUser.size)
    }

    @Test
    fun `should return empty list when no notifications available for user`() {
        notificationDao.save(randomNotification(availableFor = userId))
        notificationDao.save(randomNotification(availableFor = userId))

        val allForUser = notificationDao.findAllForUser(userId2)

        assertTrue(allForUser.isEmpty())
    }

    @Test
    fun `should find notification by id`() {
        val notification = randomNotification(availableFor = userId)
        notificationDao.save(randomNotification())
        val id = notificationDao.save(notification)
        notificationDao.save(randomNotification())

        val dbNotification = notificationDao.findById(id)

        assertNotNull(dbNotification)
        assertEquals(notification.type, dbNotification!!.type)
        assertEquals(notification.createdAt, dbNotification.createdAt)
        assertEquals(notification.createdBy, dbNotification.createdBy)
        assertEquals(notification.availableFor, dbNotification.availableFor)
    }

    @Test
    fun `should not find notification by id when does not exist`() {
        notificationDao.save(randomNotification())
        notificationDao.save(randomNotification())

        val dbNotification = notificationDao.findById(-1L)

        assertNull(dbNotification)
    }

    @Test
    fun `should delete notification`() {
        val id = notificationDao.save(randomNotification(availableFor = userId))
        val id2 = notificationDao.save(randomNotification(availableFor = userId))

        notificationDao.delete(id)

        val allForUser = notificationDao.findAllForUser(userId)

        assertEquals(1, allForUser.size)
        assertEquals(id2, allForUser[0].id)
    }

    @Test
    fun `should not delete anything when passing non existing id`() {
        val id = notificationDao.save(randomNotification())
        notificationDao.delete(-1L)

        val notification = notificationDao.findById(id)
        assertNotNull(notification)
    }

    @Test
    fun `should mark as visited`() {
        val id = notificationDao.save(randomNotification())
        notificationDao.markAsVisited(id)

        val notification = notificationDao.findById(id)

        assertTrue(notification!!.visited)
    }


    private fun randomNotification(availableFor: Id = userId2, createdBy: Id = userId): Notification =
            Notification(
                    availableFor = availableFor,
                    createdAt = LocalDateTime.now(),
                    visited = false,
                    type = Notification.Type.MARKED_AS_OPPONENT,
                    createdBy = createdBy)
}
