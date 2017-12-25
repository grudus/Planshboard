package com.grudus.planshboard.configuration.security.token

import com.grudus.planshboard.user.User
import java.math.BigInteger
import java.security.SecureRandom

// TODO change to jws token
class TokenHandler(key: ByteArray) {

    private val secureRandom: SecureRandom = SecureRandom(key)

    fun createTokenForUser(user: User): String =
            BigInteger(130, secureRandom).toString(32)


}