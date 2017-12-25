package com.grudus.planshboard.user

import com.grudus.planshboard.Tables.USERS
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class UserDao
@Autowired
constructor(private val dsl: DSLContext) {

    fun findByUsername(username: String): User? =
            dsl.selectFrom(USERS)
                    .where(USERS.NAME.eq(username))
                    .fetchOneInto(User::class.java)


}