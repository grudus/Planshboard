package com.grudus.planshboard.user

import com.grudus.planshboard.plays.opponent.OpponentService
import com.grudus.planshboard.user.auth.AddUserRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService
@Autowired
constructor(private val userDao: UserDao,
            private val passwordEncoder: PasswordEncoder,
            private val opponentService: OpponentService) {

    fun findByUsername(username: String): User? =
            userDao.findByUsername(username)

    fun registerNewUser(addUserRequest: AddUserRequest): User {
        val encodedPassword = passwordEncoder.encode(addUserRequest.password)
        val (id, registerDate) = userDao.registerNewUser(addUserRequest.username, encodedPassword)
        opponentService.addCurrentUserAsOpponent(id, addUserRequest.username)
        return User(id, addUserRequest.username, encodedPassword, registerDate = registerDate)
    }

    fun usernameExists(username: String): Boolean =
            userDao.findByUsername(username) != null
}

