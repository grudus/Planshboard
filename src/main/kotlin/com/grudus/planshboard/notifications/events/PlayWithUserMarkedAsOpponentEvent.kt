package com.grudus.planshboard.notifications.events

import com.grudus.planshboard.plays.model.AddPlayResult

data class PlayWithUserMarkedAsOpponentEvent(
        val playCreatorId: Long,
        val playId: Long,
        val result: AddPlayResult
) : NotificationEvent {

    fun isFreshlyCreatedOpponent(): Boolean =
            result.opponentId == null

}
