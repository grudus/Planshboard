package com.grudus.planshboard.notifications

import com.grudus.planshboard.configuration.security.AuthenticatedUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/notifications")
class NotificationController
@Autowired
constructor(private val notificationService: NotificationService) {

    fun getAll(user: AuthenticatedUser): List<NotificationDto> =
            notificationService.findAll(user.id)
}
