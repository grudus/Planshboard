package com.grudus.planshboard.plays

import com.grudus.planshboard.AbstractSpringServiceTest
import com.grudus.planshboard.Tables.PLAYS_RESULTS
import com.grudus.planshboard.boardgame.BoardGameService
import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.plays.opponent.OpponentNameId
import com.grudus.planshboard.plays.opponent.OpponentService
import com.grudus.planshboard.utils.randomStrings
import org.apache.commons.lang3.RandomStringUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class PlayServiceSpringTest
@Autowired
constructor(private val boardGameService: BoardGameService,
            private val opponentService: OpponentService,
            private val playService: PlayService) : AbstractSpringServiceTest() {


    private val userId: Id by lazy {
        addUser().id!!
    }
    private val boardGameId by lazy {
        boardGameService.createNew(userId, RandomStringUtils.randomAlphabetic(11))
    }


    @Test
    fun `should not be able to save play without opponents`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            playService.savePlay(userId, boardGameId, emptyList())
        }
    }

    @Test
    fun `should insert play when all opponents exists`() {
        val playOpponents = addOpponentsToDb(5)
                .mapIndexed { i, (name, id) -> AddPlayOpponent(name, i, id = id) }

        val id = playService.savePlay(userId, boardGameId, playOpponents)

        assertNotNull(id)
    }

    @Test
    fun `should insert new opponents and play when no opponent exists`() {
        val playOpponents = opponentsWithoutDb(3)
                .mapIndexed { i, (name, _) -> AddPlayOpponent(name, i) }

        val id = playService.savePlay(userId, boardGameId, playOpponents)

        assertNotNull(id)

        playOpponents.forEach { (name) ->
            assertTrue(opponentService.exists(userId, name))
        }
    }

    @Test
    fun `should insert play when mixed existing opponents and new`() {
        val playOpponents = (addOpponentsToDb(3) + opponentsWithoutDb(5))
                .mapIndexed { i, (name, id) -> AddPlayOpponent(name, i, id = id) }

        val id = playService.savePlay(userId, boardGameId, playOpponents)

        assertNotNull(id)

        playOpponents.forEach { (name) ->
            assertTrue(opponentService.exists(userId, name))
        }
    }


    @Test
    fun `should save play results`() {
        val opponentsCount = 5
        val playOpponents = addOpponentsToDb(opponentsCount)
                .mapIndexed { i, (name, id) -> AddPlayOpponent(name, i, id = id) }

        playService.savePlay(userId, boardGameId, playOpponents)

        //todo change when create returning method
        assertEquals(opponentsCount, dsl.fetchCount(PLAYS_RESULTS))
    }


    private fun addOpponentsToDb(count: Int): List<OpponentNameId> =
            randomStrings(count)
                    .map { name ->
                        val id = opponentService.addOpponent(userId, name)
                        OpponentNameId(name, id)
                    }

    private fun opponentsWithoutDb(count: Int): List<OpponentNameId> =
            randomStrings(count)
                    .map { name -> OpponentNameId(name) }
}