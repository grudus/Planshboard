package com.grudus.planshboard.plays.opponent

import com.grudus.planshboard.commons.RestKeys
import com.grudus.planshboard.user.User
import com.grudus.planshboard.user.UserService
import com.grudus.planshboard.user.auth.AuthenticationService
import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import org.springframework.validation.Validator

@Component
class SaveConnectedOpponentValidator(
        private val opponentService: OpponentService,
        private val authService: AuthenticationService,
        private val userService: UserService
): Validator {

    override fun supports(clazz: Class<*>): Boolean =
            SaveConnectedOpponentRequest::class.java.isAssignableFrom(clazz)

    override fun validate(`object`: Any?, errors: Errors) {
        val request = `object` as SaveConnectedOpponentRequest

        if (request.opponentName.isBlank())
            errors.reject(RestKeys.EMPTY_NAME)
        else if (exists(request))
            errors.reject(RestKeys.NAME_EXISTS)
        validateConnectedUser(request, errors)
    }

    private fun exists(request: SaveConnectedOpponentRequest): Boolean {
        val existingOpponent: OpponentDto? = request.existingOpponentId ?. let { opponentService.findById(it) }
        val newName = request.opponentName
        return existingOpponent?.name != newName && opponentService.exists(authService.currentUserId(), newName)
    }


    private fun validateConnectedUser(request: SaveConnectedOpponentRequest, errors: Errors) {
        if (request.connectedUserName == null || request.connectedUserName.isBlank())
            return
        val user: User? = userService.findByUsername(request.connectedUserName)
        if (user == null) {
            errors.reject(RestKeys.USER_NOT_EXIST)
            return
        }
        val opponent: OpponentDto? = opponentService.findOpponentByConnectedUser(authService.currentUserId(), user.id)
        if (opponent != null && opponent.id != request.existingOpponentId)
            errors.reject(RestKeys.USER_ASSIGNED_TO_ANOTHER_OPPONENT, opponent.name)
    }

}
