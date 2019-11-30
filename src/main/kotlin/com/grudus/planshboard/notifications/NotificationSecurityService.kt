package com.grudus.planshboard.notifications

import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.configuration.security.AuthenticatedUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class NotificationSecurityService
@Autowired
constructor(private val notificationService: NotificationService) {

    fun hasAccessToNotification(user: AuthenticatedUser, id: Id): Boolean =
            notificationService.isAvailableForUser(user.userId, id)
}
