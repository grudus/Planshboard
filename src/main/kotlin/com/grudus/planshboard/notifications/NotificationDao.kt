package com.grudus.planshboard.notifications

import com.grudus.planshboard.Tables.NOTIFICATIONS
import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.commons.Json
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class NotificationDao
@Autowired
constructor(private val dsl: DSLContext) {

    fun save(notification: Notification, additionalDataMapper: (Json?) -> String): Id =
            dsl.insertInto(NOTIFICATIONS, NOTIFICATIONS.AVAILABLE_FOR, NOTIFICATIONS.CREATED_AT, NOTIFICATIONS.CREATED_BY, NOTIFICATIONS.TYPE, NOTIFICATIONS.VISITED, NOTIFICATIONS.ADDITIONAL_DATA)
                    .values(notification.availableFor, notification.createdAt, notification.createdBy, notification.type.name, notification.visited, additionalDataMapper(notification.additionalData))
                    .returning(NOTIFICATIONS.ID)
                    .fetchOne().id

}
