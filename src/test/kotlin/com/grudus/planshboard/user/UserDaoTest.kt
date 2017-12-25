package com.grudus.planshboard.user

import com.grudus.planshboard.SpringBasedTest
import com.grudus.planshboard.Tables.USERS
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class UserDaoTest
@Autowired
constructor(private val userDao: UserDao) : SpringBasedTest() {

    @Test
    fun shouldFindByUsername() {
        val name = randomAlphabetic(11)
        addUser(name)
        addUser(randomAlphabetic(11))

        val user = userDao.findByUsername(name)

        assertNotNull(user)
        assertEquals(name, user!!.name)
    }

    @Test
    fun shouldNotFindByUsername() {
        addUser(randomAlphabetic(11))

        val user = userDao.findByUsername(randomAlphabetic(11))

        assertNull(user)
    }



}