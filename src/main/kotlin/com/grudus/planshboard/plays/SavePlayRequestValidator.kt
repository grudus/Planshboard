package com.grudus.planshboard.plays

import com.grudus.planshboard.commons.RestKeys
import com.grudus.planshboard.plays.model.SavePlayRequest
import com.grudus.planshboard.plays.model.AddPlayResult
import com.grudus.planshboard.plays.opponent.OpponentService
import com.grudus.planshboard.user.auth.AuthenticationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import org.springframework.validation.Validator

@Component
class SavePlayRequestValidator
@Autowired
constructor(private val authService: AuthenticationService,
            private val opponentService: OpponentService) : Validator {

    override fun validate(target: Any?, errors: Errors?) {
        val request = target as SavePlayRequest
        val (opponentsWithId, opponentsWithoutId) = request.results.partition { it.opponentId != null }

        if (request.results.isEmpty()) {
            errors?.reject(RestKeys.NO_RESULTS)
            return
        }
        if (containsDuplicatedOpponents(request)) {
            errors?.reject(RestKeys.DUPLICATED_OPPONENTS)
            return
        }
        if (!allOpponentsWithIdExist(opponentsWithId))
            errors?.reject(RestKeys.OPPONENTS_NOT_EXISTS)
        if (!allOpponentsWithoutIdDoNotExist(opponentsWithoutId))
            errors?.reject(RestKeys.OPPONENTS_EXISTS)
    }

    private fun containsDuplicatedOpponents(request: SavePlayRequest): Boolean =
            request.results.distinctBy { it.opponentName }.size != request.results.size


    override fun supports(clazz: Class<*>): Boolean =
            SavePlayRequest::class.java.isAssignableFrom(clazz)


    private fun allOpponentsWithoutIdDoNotExist(results: List<AddPlayResult>) =
            results.map { it.opponentName }
                    .let { names ->
                        opponentService.allDoNotExist(authService.currentUserId(), names)
                    }


    private fun allOpponentsWithIdExist(results: List<AddPlayResult>) =
            results.map { it.opponentId!! }
                    .let { ids ->
                        opponentService.allExists(authService.currentUserId(), ids)
                    }
}
