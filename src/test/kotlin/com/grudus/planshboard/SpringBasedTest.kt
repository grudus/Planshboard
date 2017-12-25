package com.grudus.planshboard

import com.grudus.planshboard.Tables.USERS
import com.grudus.planshboard.tables.records.UsersRecord
import com.grudus.planshboard.user.User
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.jooq.DSLContext
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.transaction.annotation.Transactional
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.time.LocalDateTime.now

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestContext::class])
@WebAppConfiguration
@Transactional
@Rollback
abstract class SpringBasedTest {

    @Autowired
    protected lateinit var dsl: DSLContext


    protected fun addUser(name: String = randomAlphabetic(11)): User {
        val user = User(name = name, password = randomAlphabetic(11), registerDate = now())

        val userRecord = dsl.newRecord(USERS, user)
        userRecord.insert()

        return user.copy(id = userRecord.id)
    }

}