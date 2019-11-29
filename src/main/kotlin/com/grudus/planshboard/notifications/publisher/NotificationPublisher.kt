package com.grudus.planshboard.notifications.publisher

import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.notifications.events.NotificationEvent
import com.grudus.planshboard.notifications.events.PlayWithUserMarkedAsOpponentEvent
import com.grudus.planshboard.plays.model.AddPlayResult
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class NotificationPublisher
@Autowired
constructor(private val eventPublisher: ApplicationEventPublisher) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Async
    fun notifyUsedIsMarkedAsOpponent(userId: Id, playId: Id, results: List<AddPlayResult>) {
        results.forEach { result ->
            publish(PlayWithUserMarkedAsOpponentEvent(userId, playId, result))
        }
    }

    private fun publish(event: NotificationEvent) {
        logger.info("Publishing event {}", event)
        eventPublisher.publishEvent(event)
    }
}
