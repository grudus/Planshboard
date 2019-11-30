package com.grudus.planshboard.notifications

import com.grudus.planshboard.Tables.NOTIFICATIONS
import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.notifications.model.Notification
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class NotificationDao
@Autowired
constructor(private val dsl: DSLContext) {

    fun save(notification: Notification): Id =
            dsl.insertInto(NOTIFICATIONS, NOTIFICATIONS.AVAILABLE_FOR, NOTIFICATIONS.CREATED_AT, NOTIFICATIONS.CREATED_BY, NOTIFICATIONS.TYPE, NOTIFICATIONS.VISITED, NOTIFICATIONS.ADDITIONAL_DATA)
                    .values(notification.availableFor, notification.createdAt, notification.createdBy, notification.type.name, notification.visited, notification.additionalData)
                    .returning(NOTIFICATIONS.ID)
                    .fetchOne().id

    fun findAllForUser(forUserId: Id): List<Notification> =
            dsl.selectFrom(NOTIFICATIONS)
                    .where(NOTIFICATIONS.AVAILABLE_FOR.eq(forUserId))
                    .fetchInto(Notification::class.java)

    fun delete(id: Id) =
            dsl.deleteFrom(NOTIFICATIONS)
                    .where(NOTIFICATIONS.ID.eq(id))
                    .execute()

    fun markAsVisited(id: Id) =
            dsl.update(NOTIFICATIONS)
                    .set(NOTIFICATIONS.VISITED, true)
                    .where(NOTIFICATIONS.ID.eq(id))
                    .execute()

    fun findById(id: Id): Notification? =
            dsl.selectFrom(NOTIFICATIONS)
                    .where(NOTIFICATIONS.ID.eq(id))
                    .fetchOneInto(Notification::class.java)
}
