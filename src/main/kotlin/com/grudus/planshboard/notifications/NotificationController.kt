package com.grudus.planshboard.notifications

import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.configuration.security.AuthenticatedUser
import com.grudus.planshboard.notifications.model.NotificationDto
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/notifications")
class NotificationController
@Autowired
constructor(private val notificationService: NotificationService) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @GetMapping
    fun getAll(user: AuthenticatedUser): List<NotificationDto> =
            notificationService.findAll(user.id)

    @PostMapping("/{id}/accept")
    @PreAuthorize("@notificationSecurityService.hasAccessToNotification(#user, #id)")
    fun acceptMarkedAsOpponentNotification(
            user: AuthenticatedUser,
            @PathVariable("id") id: Id
    ) {
        logger.info("User [{}] accepts notification {}", user.id, id)
        notificationService.acceptNotification(id)
    }


    @PostMapping("/{id}/reject")
    @PreAuthorize("@notificationSecurityService.hasAccessToNotification(#user, #id)")
    fun rejectMarkedAsOpponentNotification(
            user: AuthenticatedUser,
            @PathVariable("id") id: Id
    ) {
        logger.info("User [{}] rejects notification {}", user.id, id)
        notificationService.rejectNotification(id)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@notificationSecurityService.hasAccessToNotification(#user, #id)")
    fun deleteNotification(
            user: AuthenticatedUser,
            @PathVariable("id") id: Id
    ) {
        logger.info("User [{}] deletes notification {}", user.id, id)
        notificationService.delete(id)
    }
}
