package com.grudus.planshboard.notifications

import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.commons.Json
import com.grudus.planshboard.plays.opponent.model.Opponent
import com.grudus.planshboard.plays.opponent.model.OpponentDto
import java.time.LocalDateTime

/* val id: Id? = null,
        val availableFor: Id,
        val createdAt: LocalDateTime,
        val visited: Boolean,
        val type: Type,
        val createdBy: Id? = null,
        val additionalData: Json? = null
*/

data class NotificationDto(
        val id: Id,
        val createdAt: LocalDateTime,
        val visited: Boolean,
        val type: Notification.Type,
        val createdBy: Id? = null,
        val additionalData: Json? = null
) {
    companion object {
        fun fromNotification(n: Notification) = NotificationDto(
                n.id!!,
                n.createdAt,
                n.visited,
                n.type,
                n.createdBy,
                n.additionalData
        )
    }
}
