package com.grudus.planshboard.plays.opponent

import com.grudus.planshboard.commons.RestKeys
import com.grudus.planshboard.user.auth.AuthenticationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import org.springframework.validation.Validator

@Component
class AddOpponentValidator
@Autowired
constructor(private val opponentsService: OpponentService, private val authService: AuthenticationService) : Validator {

    override fun validate(`object`: Any?, errors: Errors?) {
        val request = `object` as AddOpponentRequest

        if (request.name.isBlank())
            errors?.reject(RestKeys.EMPTY_NAME)
        else if (exists(request))
            errors?.reject(RestKeys.NAME_EXISTS)
    }


    override fun supports(clazz: Class<*>?): Boolean =
            AddOpponentRequest::class.java.isAssignableFrom(clazz)

    private fun exists(request: AddOpponentRequest): Boolean =
            opponentsService.exists(authService.currentUserId(), request.name)
}