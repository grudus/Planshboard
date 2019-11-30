package com.grudus.planshboard.notifications

import com.fasterxml.jackson.databind.ObjectMapper
import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.notifications.model.NotificationDto.Companion.fromNotification
import com.grudus.planshboard.notifications.model.Notification
import com.grudus.planshboard.notifications.model.NotificationDto
import com.grudus.planshboard.plays.model.AddPlayResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class NotificationService
@Autowired
constructor(private val notificationDao: NotificationDao,
            private val objectMapper: ObjectMapper) {

    fun saveUserMarkedAsOpponent(createdBy: Id, availableForUser: Id, playCreatorName: String, result: AddPlayResult): Id {
        val notification = Notification(
                availableFor = availableForUser,
                createdAt = LocalDateTime.now(),
                visited = false,
                type = Notification.Type.MARKED_AS_OPPONENT,
                createdBy = createdBy,
                additionalData = objectMapper.createObjectNode()
                        .put("points", result.points)
                        .put("position", result.position)
                        .put("playCreatorName", playCreatorName)
        )

        return save(notification)
    }

    fun findAll(forUserId: Id): List<NotificationDto> =
            notificationDao.findAllForUser(forUserId)
                    .map(::fromNotification)

    private fun save(notification: Notification): Id =
            notificationDao.save(notification)

    fun acceptNotification(id: Id) {
        notificationDao.markAsVisited(id)
    }

    fun rejectNotification(id: Id) {
        notificationDao.markAsVisited(id)
    }

    fun delete(id: Id) {
        notificationDao.delete(id)
    }

    fun isAvailableForUser(userId: Id, notificationId: Id): Boolean =
            notificationDao.findById(notificationId)?.availableFor == userId

}
