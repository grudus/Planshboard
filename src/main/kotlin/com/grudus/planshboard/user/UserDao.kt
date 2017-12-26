package com.grudus.planshboard.user

import com.grudus.planshboard.Tables.USERS
import com.grudus.planshboard.user.auth.InsertedUserResult
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

    fun registerNewUser(username: String, password: String): InsertedUserResult =
        dsl.insertInto(USERS, USERS.NAME, USERS.PASSWORD, USERS.ROLE)
                .values(username, password, User.Role.USER.name)
                .returning(USERS.ID, USERS.REGISTER_DATE)
                .fetchOne()
                .let {record ->
                    InsertedUserResult(record.id, record.registerDate)
                }
}