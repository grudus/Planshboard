package com.grudus.planshboard.games

import com.grudus.planshboard.AbstractDatabaseTest
import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.games.opponent.OpponentDao
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class GameDaoTest
@Autowired
constructor(private val gameDao: GameDao, private val opponentDao: OpponentDao) : AbstractDatabaseTest() {

    private val userId: Id by lazy {
        addUser().id!!
    }


    @Test
    fun `should save game`() {
        val opponentIds = addOpponents(5)

        val id = gameDao.saveGame(userId, opponentIds)

        assertNotNull(id)
    }

    @Test
    fun `should not be able to save game without opponents`() {
        assertThrows(IllegalArgumentException::class.java) {
            gameDao.saveGame(userId, emptyList())
        }
    }

    @Test
    fun `should be able to save game for the same opponents`() {
        val opponentIds = addOpponents(4)

        val id1 = gameDao.saveGame(userId, opponentIds)
        val id2 = gameDao.saveGame(userId, opponentIds)

        assertNotEquals(id1, id2)
    }

    @Test
    fun `should find all opponents for game`() {
        val opponentIds = addOpponents(3)
        val gameId = gameDao.saveGame(userId, opponentIds)

        val opponents = gameDao.findOpponentsForGame(gameId)
                .map { it.id!! }

        assertEquals(3, opponents.size)
        assertThat(opponentIds, containsInAnyOrder(*opponents.toTypedArray()))
    }

    @Test
    fun `should find opponents for specific game when multiple exists`() {
        val game1Count = 5
        val game2Count = 3
        val game1Id = gameDao.saveGame(userId, addOpponents(game1Count))
        val game2Id = gameDao.saveGame(userId, addOpponents(game2Count))

        val opponents1 = gameDao.findOpponentsForGame(game1Id)
        val opponents2 = gameDao.findOpponentsForGame(game2Id)

        assertEquals(game1Count, opponents1.size)
        assertEquals(game2Count, opponents2.size)
    }


    private fun addOpponents(count: Int = 5): List<Id> =
            (0 until count).map { randomAlphabetic(11 + it) }
                    .map { name -> opponentDao.addOpponent(userId, name) }

}