package com.grudus.planshboard.boardgame

import com.grudus.planshboard.AbstractDatabaseTest
import com.grudus.planshboard.commons.Id
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.jooq.exception.DataAccessException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class BoardGameDaoTest
@Autowired
constructor(private val boardGameDao: BoardGameDao) : AbstractDatabaseTest() {

    private val userId: Id by lazy { addUser(randomAlphabetic(11)).id!! }

    @Test
    fun `should create new board game`() {
        val id = boardGameDao.create(randomAlphabetic(11), userId)

        assertNotNull(id)
    }


    @Test
    fun `should not create new board game when name exists for user`() {
        val name = randomAlphabetic(11)
        boardGameDao.create(name, userId)

        assertThrows(DataAccessException::class.java) {
            boardGameDao.create(name, userId)
        }
    }


    @Test
    fun `should create new board game when name exists for another user`() {
        val name = randomAlphabetic(11)
        boardGameDao.create(name, userId)

        val newUserId = addUser(randomAlphabetic(11)).id!!
        val id = boardGameDao.create(name, newUserId)

        assertNotNull(id)
    }

    @Test
    fun `should find all for user`() {
        val items = 5
        (0 until items).map { randomAlphabetic(it + 5) }
                .forEach { boardGameDao.create(it, userId) }

        val games = boardGameDao.findAll(userId)

        assertEquals(items, games.size)
    }

    @Test
    fun `should find all only for given user`() {
        val newUserId = addUser(randomAlphabetic(11)).id!!
        val name = randomAlphabetic(11)

        boardGameDao.create(randomAlphabetic(11), newUserId)
        boardGameDao.create(name, userId)


        val games = boardGameDao.findAll(userId)

        assertEquals(1, games.size)
        assertEquals(name, games[0].name)
    }

    @Test
    fun `should return empty list if doesn't exists for user`() {
        val newUserId = addUser(randomAlphabetic(11)).id!!
        boardGameDao.create(randomAlphabetic(11), newUserId)

        val games = boardGameDao.findAll(userId)

        assertTrue(games.isEmpty())
    }

    @Test
    fun `should find by name`() {
        val name = randomAlphabetic(11)
        boardGameDao.create(name, userId)

        val game = boardGameDao.findByName(userId, name)

        assertNotNull(game)
        assertNotNull(game!!.id)
        assertEquals(name, game.name)
    }


    @Test
    fun `should not find by name`() {
        val name = randomAlphabetic(11)
        boardGameDao.create(randomAlphabetic(11), userId)

        val game = boardGameDao.findByName(userId, name)

        assertNull(game)
    }

    @Test
    fun `should find by name if duplicate names for different users`() {
        val name = randomAlphabetic(11)
        boardGameDao.create(name, userId)

        val newUserId = addUser(randomAlphabetic(11)).id!!
        boardGameDao.create(name, newUserId)

        val user1Game = boardGameDao.findByName(userId, name)
        val user2Game = boardGameDao.findByName(newUserId, name)

        assertNotNull(user1Game)
        assertNotNull(user2Game)
        assertNotEquals(user1Game!!.id, user2Game!!.id)
    }

}