package com.grudus.planshboard.user

import com.grudus.planshboard.SpringBasedTest
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.jooq.exception.DataAccessException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

class UserDaoTest
@Autowired
constructor(private val userDao: UserDao) : SpringBasedTest() {

    @Test
    fun `should find by username`() {
        val name = randomAlphabetic(11)
        addUser(name)
        addUser(randomAlphabetic(11))

        val user = userDao.findByUsername(name)

        assertNotNull(user)
        assertEquals(name, user!!.name)
    }

    @Test
    fun `should not find by username`() {
        addUser(randomAlphabetic(11))

        val user = userDao.findByUsername(randomAlphabetic(11))

        assertNull(user)
    }

    @Test
    fun `should register new user`() {
        val username = randomAlphabetic(11)
        userDao.registerNewUser(username, randomAlphabetic(11))

        val user = userDao.findByUsername(username)
        assertNotNull(user)
        assertEquals(username, user?.name)
    }

    @Test
    fun `should register new user and return id and date`() {
        val (_, date) = userDao.registerNewUser(randomAlphabetic(11), randomAlphabetic(11))

        assertFalse(LocalDateTime.now().isBefore(date))
    }

    @Test
    fun `should throw exception when register user with existing username`() {
        val username = randomAlphabetic(11)
        userDao.registerNewUser(username, randomAlphabetic(11))

        assertThrows(DataAccessException::class.java) {
            userDao.registerNewUser(username, randomAlphabetic(11))
        }
    }
}