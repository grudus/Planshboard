package com.grudus.planshboard.notifications

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.commons.Json
import com.grudus.planshboard.plays.model.AddPlayResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class NotificationService
@Autowired
constructor(private val notificationDao: NotificationDao,
            private val objectMapper: ObjectMapper) {

    fun saveUserMarkedAsOpponent(createdBy: Id, availableForUser: Id, result: AddPlayResult): Id {
        val notification = Notification(
                availableFor = availableForUser,
                createdAt = LocalDateTime.now(),
                visited = false,
                type = Notification.Type.MARKED_AS_OPPONENT,
                createdBy = createdBy,
                additionalData = objectMapper.createObjectNode()
                        .put("points", result.points)
                        .put("position", result.position)
        )

        return save(notification)
    }


    private fun save(notification: Notification): Id =
            notificationDao.save(notification)
}
