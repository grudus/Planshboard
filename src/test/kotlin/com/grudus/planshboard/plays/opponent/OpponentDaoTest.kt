package com.grudus.planshboard.plays.opponent

import com.grudus.planshboard.AbstractDatabaseTest
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.jooq.exception.DataAccessException
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

        val opponents = opponentDao.findAllOpponentsWithoutReal(userId)

        assertEquals(3, opponents.size)
    }

    @Test
    fun `should return empty list when no opponents for user`() {
        (0 until 10).map { randomAlphabetic(it + 4) }
                .forEach { opponentDao.addOpponent(userId, it) }

        val opponents = opponentDao.findAllOpponentsWithoutReal(addUser().id!!)

        assertTrue(opponents.isEmpty())
    }

    @Test
    fun `should return empty list when no opponents at all`() {
        val opponents = opponentDao.findAllOpponentsWithoutReal(userId)

        assertTrue(opponents.isEmpty())
    }

    @Test
    fun `should not be able to save save opponent's name for user twice`() {
        val name = randomAlphabetic(11)

        opponentDao.addOpponent(userId, name)

        assertThrows(DataAccessException::class.java) {
            opponentDao.addOpponent(userId, name)
        }
    }

    @Test
    fun `should be able to save the same opponent's name for different users`() {
        val name = randomAlphabetic(11)
        val newUserId = addUser().id!!

        opponentDao.addOpponent(userId, name)
        opponentDao.addOpponent(newUserId, name)
    }

    @Test
    fun `should always has opponent when including real ones`() {
        val opponents = opponentDao.findAllOpponentsWithReal(userId)

        assertEquals(1, opponents.size)
    }


    @Test
    fun `should find opponents with current user`() {
        val count = 3
        (0 until count).map { randomAlphabetic(4 + it) }
                .forEach { opponentDao.addOpponent(userId, it) }

        val opponents = opponentDao.findAllOpponentsWithReal(userId)

        assertEquals(count + 1, opponents.size)
    }

}