package com.grudus.planshboard.user.auth

import com.grudus.planshboard.commons.Keys.EMPTY_PASSWORD
import com.grudus.planshboard.commons.Keys.EMPTY_USERNAME
import org.apache.commons.lang3.StringUtils.isBlank
import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import org.springframework.validation.Validator

@Component
class AddUserRequestValidator : Validator {


    override fun validate(target: Any?, errors: Errors?) {
        val request = target as AddUserRequest

        if (isBlank(request.username))
            errors?.reject(EMPTY_USERNAME)
        if (isBlank(request.password))
            errors?.reject(EMPTY_PASSWORD)
    }

    override fun supports(clazz: Class<*>?): Boolean =
            AddUserRequest::class.java.isAssignableFrom(clazz)
}