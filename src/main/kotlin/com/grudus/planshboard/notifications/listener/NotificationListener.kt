package com.grudus.planshboard.notifications.listener

import com.grudus.planshboard.notifications.NotificationService
import com.grudus.planshboard.notifications.events.PlayWithUserMarkedAsOpponentEvent
import com.grudus.planshboard.plays.opponent.OpponentService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class NotificationListener
@Autowired
constructor(private val notificationService: NotificationService,
            private val opponentService: OpponentService) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @EventListener
    fun handleOpponent(event: PlayWithUserMarkedAsOpponentEvent) {
        logger.info("Receive event about user being marked as opponent {}", event)
        if (event.isFreshlyCreatedOpponent()) {
            return
        }
        val pointingToUserId = opponentService.getPointingToUserId(event.result.opponentId!!) ?: return
        if (pointingToUserId == event.playCreatorId)
            return

        logger.info("Notify user [$pointingToUserId] about added play")
        notificationService.saveUserMarkedAsOpponent(event.playCreatorId, pointingToUserId, event.result)
    }
}
