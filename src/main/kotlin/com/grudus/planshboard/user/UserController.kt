package com.grudus.planshboard.user

import com.grudus.planshboard.configuration.security.AuthenticatedUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController
@Autowired
constructor(private val userService: UserService) {

    @GetMapping("/current")
    fun getCurrentUser(authenticatedUser: AuthenticatedUser): UserDto =
            userService.getCurrentUser(authenticatedUser)

}
