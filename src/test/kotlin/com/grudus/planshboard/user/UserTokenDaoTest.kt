package com.grudus.planshboard.user

import com.grudus.planshboard.AbstractDatabaseTest
import com.grudus.planshboard.user.auth.UserTokenDao
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class UserTokenDaoTest
@Autowired
constructor(private val userTokenDao: UserTokenDao, private val userDao: UserDao): AbstractDatabaseTest() {

    private lateinit var user: User

    @BeforeEach
    fun init() {
        user = addUser()
    }

    @Test
    fun `should add token for existing user`() {
        addUser()
        val token = randomAlphabetic(32)

        userTokenDao.addToken(user.id!!, token)

        val dbUser = userDao.findByUsername(user.name)!!
        assertEquals(token, dbUser.token)
    }

    @Test
    fun `should not add token if username not exists`() {
        val token = randomAlphabetic(32)

        userTokenDao.addToken(-1L, token)

        val dbUser = userDao.findByUsername(user.name)!!
        assertNotEquals(token, dbUser.token)
    }

    @Test
    fun `should find by token`() {
        addUser()
        val token = randomAlphabetic(32)

        userTokenDao.addToken(user.id!!, token)

        val dbUser = userTokenDao.findByToken(token)!!

        assertEquals(user.name, dbUser.name)
        assertEquals(user.password, dbUser.password)
    }

    @Test
    fun `should not find by token`() {
        addUser()
        val token = randomAlphabetic(32)

        userTokenDao.addToken(user.id!!, token)

        val dbUser = userTokenDao.findByToken(randomAlphabetic(11))
        assertNull(dbUser )
    }


}