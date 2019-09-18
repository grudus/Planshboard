package com.grudus.planshboard.plays.opponent

import com.grudus.planshboard.AbstractDatabaseTest
import com.grudus.planshboard.commons.Id
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
        val id = opponentDao.addOpponent(randomAlphabetic(11), userId)

        assertNotNull(id)
    }

    @Test
    fun `should find all opponents for user`() {
        val count = 3
        (0 until count).map { randomAlphabetic(5 + it) }
                .forEach { opponentDao.addOpponent(it, userId) }
        opponentDao.addOpponent(randomAlphabetic(11), addUser().id!!)

        val opponentsWithCreator = opponentDao.findAllOpponentsCreatedBy(userId)

        assertEquals(4, opponentsWithCreator.size)
    }

    @Test
    fun `should return only creator when no opponents for user`() {
        (0 until 10).map { randomAlphabetic(it + 4) }
                .forEach { opponentDao.addOpponent(it, userId) }

        val createdBy = addUser().id!!
        val opponents = opponentDao.findAllOpponentsCreatedBy(createdBy)

        assertOnlyCreatorExists(opponents, createdBy)
    }

    @Test
    fun `should return only creator when no opponents at all`() {
        val opponents = opponentDao.findAllOpponentsCreatedBy(userId)

        assertOnlyCreatorExists(opponents)
    }

    @Test
    fun `should not be able to save save opponent's name for user twice`() {
        val name = randomAlphabetic(11)

        opponentDao.addOpponent(name, userId)

        assertThrows(DataAccessException::class.java) {
            opponentDao.addOpponent(name, userId)
        }
    }

    @Test
    fun `should be able to save the same opponent's name for different users`() {
        val name = randomAlphabetic(11)
        val newUserId = addUser().id!!

        opponentDao.addOpponent(name, userId)
        opponentDao.addOpponent(name, newUserId)
    }

    @Test
    fun `should always has opponent when including real ones`() {
        val opponents = opponentDao.findAllOpponentsCreatedBy(userId)

        assertEquals(1, opponents.size)
    }

    @Test
    fun `should find opponents with current user`() {
        val count = 3
        (0 until count).map { randomAlphabetic(4 + it) }
                .forEach { opponentDao.addOpponent(it, userId) }

        val opponents = opponentDao.findAllOpponentsCreatedBy(userId)

        assertEquals(count + 1, opponents.size)
    }

    @Test
    fun `should find opponent entity pointing to current user`() {
        (0 until 10).forEach { _ -> opponentDao.addOpponent(randomAlphabetic(11), userId) }
        val newUserId = addUser().id!!
        (0 until 10).forEach { _ -> opponentDao.addOpponent(randomAlphabetic(11), newUserId) }

        val opponent = opponentDao.findOpponentPointingToCurrentUser(newUserId)

        assertEquals(newUserId, opponent.createdBy)
        assertEquals(newUserId, opponent.pointingToUser)
    }

    @Test
    fun `should find opponent entity pointing to current user when no other opponents`() {
        val opponent = opponentDao.findOpponentPointingToCurrentUser(userId)

        assertEquals(userId, opponent.createdBy)
        assertEquals(userId, opponent.pointingToUser)
    }

    private fun assertOnlyCreatorExists(opponents: List<Opponent>, createdBy: Id = userId) {
        assertEquals(1, opponents.size)
        assertEquals(createdBy, opponents[0].pointingToUser)
    }

}
