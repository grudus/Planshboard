package com.grudus.planshboard.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService
@Autowired
constructor(private val userDao: UserDao) {

    fun findByUsername(username: String): User? =
            userDao.findByUsername(username)
}

