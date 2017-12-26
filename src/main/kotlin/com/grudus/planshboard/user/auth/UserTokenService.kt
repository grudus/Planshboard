package com.grudus.planshboard.user.auth

import com.grudus.planshboard.user.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserTokenService
@Autowired
constructor(private val userTokenDao: UserTokenDao) {

    fun findByToken(token: String): User? =
            userTokenDao.findByToken(token)

    fun addToken(id: Long, token: String) =
            userTokenDao.addToken(id, token)
}