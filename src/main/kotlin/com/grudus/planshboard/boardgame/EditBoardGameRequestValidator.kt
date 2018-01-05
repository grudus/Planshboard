package com.grudus.planshboard.boardgame

import com.grudus.planshboard.commons.RestKeys
import com.grudus.planshboard.user.auth.AuthenticationService
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import org.springframework.validation.Validator

@Component
class EditBoardGameRequestValidator
@Autowired
constructor(private val authenticationService: AuthenticationService, private val boardGameService: BoardGameService) : Validator {


    override fun validate(target: Any?, errors: Errors?) {
        val request = target as EditBoardGameRequest

        if (StringUtils.isBlank(request.name))
            errors?.reject(RestKeys.EMPTY_NAME)
        else if (exists(request.name)) {
            errors?.reject(RestKeys.NAME_EXISTS)
        }
    }

    override fun supports(clazz: Class<*>?): Boolean =
            EditBoardGameRequest::class.java.isAssignableFrom(clazz)

    private fun exists(name: String): Boolean =
            boardGameService.exists(authenticationService.currentUserId(), name)
}