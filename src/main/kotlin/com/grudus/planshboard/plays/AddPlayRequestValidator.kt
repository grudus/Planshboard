package com.grudus.planshboard.plays

import com.grudus.planshboard.boardgame.BoardGameService
import com.grudus.planshboard.commons.RestKeys
import com.grudus.planshboard.plays.opponent.OpponentService
import com.grudus.planshboard.user.auth.AuthenticationService
import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import org.springframework.validation.Validator

@Component
class AddPlayRequestValidator
    constructor(private val authService: AuthenticationService,
                private val boardGameService: BoardGameService,
                private val opponentService: OpponentService): Validator {

    override fun validate(target: Any?, errors: Errors?) {
        val request = target as AddPlayRequest

        if (request.opponents.isEmpty())
            errors?.reject(RestKeys.NO_OPPONENTS)

        else if (!allOpponentsExist(request))
            errors?.reject(RestKeys.OPPONENTS_NOT_EXISTS)

        if (boardGameDoesNotExist(request))
            errors?.reject(RestKeys.BOARD_GAME_NOT_EXISTS)
    }


    override fun supports(clazz: Class<*>?): Boolean =
            AddPlayRequest::class.java.isAssignableFrom(clazz)


    private fun allOpponentsExist(request: AddPlayRequest) =
            opponentService.allExists(authService.currentUserId(), request.opponents)

    private fun boardGameDoesNotExist(request: AddPlayRequest) =
        !boardGameService.existsForUser(authService.currentUserId(), request.boardGameId)
}