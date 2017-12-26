package com.grudus.planshboard.user.auth

import com.grudus.planshboard.Tables.USERS
import com.grudus.planshboard.user.User
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class UserTokenDao

@Autowired
constructor(private val dsl: DSLContext) {

    fun findByToken(token: String): User? =
            dsl.selectFrom(USERS)
                    .where(USERS.TOKEN.eq(token))
                    .fetchOneInto(User::class.java)

    fun addToken(userId: Long, token: String) =
            dsl.update(USERS)
                    .set(USERS.TOKEN, token)
                    .where(USERS.ID.eq(userId))
                    .execute()
}