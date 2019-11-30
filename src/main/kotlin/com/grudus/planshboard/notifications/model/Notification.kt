package com.grudus.planshboard.notifications.model

import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.commons.Json
import java.time.LocalDateTime

data class Notification(
        val id: Id? = null,
        val availableFor: Id,
        val createdAt: LocalDateTime,
        val visited: Boolean,
        val type: Type,
        val createdBy: Id? = null,
        val additionalData: Json? = null
) {

    enum class Type {
        MARKED_AS_OPPONENT
    }
}
