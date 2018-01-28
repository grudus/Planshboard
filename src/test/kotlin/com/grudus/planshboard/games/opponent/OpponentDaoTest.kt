package com.grudus.planshboard.games.opponent

import com.grudus.planshboard.AbstractDatabaseTest
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class OpponentDaoTest
@Autowired
constructor(private val opponentDao: OpponentDao) : AbstractDatabaseTest() {

    private val userId by lazy { addUser().id!! }

    @Test
    fun `should save opponent and return id`() {
        val id = opponentDao.addOpponent(userId, randomAlphabetic(11))

        assertNotNull(id)
    }

    @Test
    fun `should find all opponents for user`() {
        val count = 3
        (0 until count).map { randomAlphabetic(5 + it) }
                .forEach { opponentDao.addOpponent(userId, it) }
        opponentDao.addOpponent(addUser().id!!, randomAlphabetic(11))

        val opponents = opponentDao.findAllOpponents(userId)

        assertEquals(3, opponents.size)
    }

    @Test
    fun `should return empty list when no opponents for user`() {
        (0 until 10).map { randomAlphabetic(it + 4) }
                .forEach{opponentDao.addOpponent(userId, it)}

        val opponents = opponentDao.findAllOpponents(addUser().id!!)

        assertTrue(opponents.isEmpty())
    }

    @Test
    fun `should return empty list when no opponents at all`() {
        val opponents = opponentDao.findAllOpponents(userId)

        assertTrue(opponents.isEmpty())
    }

}