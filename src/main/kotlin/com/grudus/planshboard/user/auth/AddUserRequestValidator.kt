package com.grudus.planshboard.user.auth

import com.grudus.planshboard.commons.RestKeys.EMPTY_PASSWORD
import com.grudus.planshboard.commons.RestKeys.EMPTY_USERNAME
import com.grudus.planshboard.commons.RestKeys.USERNAME_EXISTS
import com.grudus.planshboard.user.UserService
import org.apache.commons.lang3.StringUtils.isBlank
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import org.springframework.validation.Validator

@Component
class AddUserRequestValidator
@Autowired
constructor(private val userService: UserService) : Validator {


    override fun validate(target: Any?, errors: Errors?) {
        val (username, password) = target as AddUserRequest

        if (isBlank(username))
            errors?.reject(EMPTY_USERNAME)
        else if (userService.usernameExists(username))
            errors?.reject(USERNAME_EXISTS)
        if (isBlank(password))
            errors?.reject(EMPTY_PASSWORD)
    }

    override fun supports(clazz: Class<*>?): Boolean =
            AddUserRequest::class.java.isAssignableFrom(clazz)
}