package com.grudus.planshboard.user.auth

import com.grudus.planshboard.commons.IdResponse
import com.grudus.planshboard.user.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import javax.validation.Valid
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/auth/register")
class UserRegistrationController
@Autowired
constructor(private val userService: UserService, private val addUserRequestValidator: AddUserRequestValidator) {


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(@Valid @RequestBody addUserRequest: AddUserRequest): IdResponse =
            userService.registerNewUser(addUserRequest)
                    .let { user -> IdResponse(user.id!!) }


    @InitBinder("addUserRequest")
    protected fun initBinder(binder: WebDataBinder) {
        binder.validator = addUserRequestValidator
    }
}