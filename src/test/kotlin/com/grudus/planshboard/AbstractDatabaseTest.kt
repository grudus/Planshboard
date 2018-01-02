package com.grudus.planshboard

import com.grudus.planshboard.user.User
import com.grudus.planshboard.user.UserService
import com.grudus.planshboard.user.auth.AddUserRequest
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractDatabaseTest : SpringBasedTest() {

    @Autowired
    protected lateinit var dsl: DSLContext

    @Autowired
    private lateinit var userService: UserService

    protected final fun addUser(username: String = randomAlphabetic(11)): User =
            userService.registerNewUser(AddUserRequest(username, randomAlphabetic(11)))

}