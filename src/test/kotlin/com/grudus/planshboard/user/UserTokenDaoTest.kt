package com.grudus.planshboard.user

import com.grudus.planshboard.SpringBasedTest
import com.grudus.planshboard.user.auth.UserTokenDao
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class UserTokenDaoTest
@Autowired
constructor(private val userTokenDao: UserTokenDao, private val userDao: UserDao): SpringBasedTest() {

    private lateinit var user: User

    @BeforeEach
    fun init() {
        user = addUser()
    }

    @Test
    fun shouldAddTokenForExistingUser() {
        addUser()
        val token = randomAlphabetic(32)

        userTokenDao.addToken(user.id!!, token)

        val dbUser = userDao.findByUsername(user.name)!!
        assertEquals(token, dbUser.token)
    }

    @Test
    fun shouldNotAddTokenIfUsernameNotExists() {
        val token = randomAlphabetic(32)

        userTokenDao.addToken(-1L, token)

        val dbUser = userDao.findByUsername(user.name)!!
        assertNotEquals(token, dbUser.token)
    }

    @Test
    fun shouldFindByToken() {
        addUser()
        val token = randomAlphabetic(32)

        userTokenDao.addToken(user.id!!, token)

        val dbUser = userTokenDao.findByToken(token)!!

        assertEquals(user.name, dbUser.name)
        assertEquals(user.password, dbUser.password)
    }

    @Test
    fun shouldNotFindByToken() {
        addUser()
        val token = randomAlphabetic(32)

        userTokenDao.addToken(user.id!!, token)

        val dbUser = userTokenDao.findByToken(randomAlphabetic(11))
        assertNull(dbUser )
    }


}