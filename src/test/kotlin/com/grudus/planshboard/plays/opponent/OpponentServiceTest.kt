package com.grudus.planshboard.plays.opponent

import com.grudus.planshboard.MockitoExtension
import com.grudus.planshboard.commons.Id
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.apache.commons.lang3.RandomUtils.nextLong
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`

@ExtendWith(MockitoExtension::class)
class OpponentServiceTest {

    @Mock
    private lateinit var opponentDao: OpponentDao

    private lateinit var opponentService: OpponentService

    @BeforeEach
    fun init() {
        opponentService = OpponentService(opponentDao)
    }

    @Test
    fun `should detect if all exists`() {
        val ids = listOf(nextLong(), nextLong())
        Mockito.`when`(opponentDao.findAllOpponentsCreatedBy(anyLong()))
                .thenReturn(listOf(randomOpponent(ids[0]), randomOpponent(ids[1]), randomOpponent()))

        val allExists = opponentService.allExists(nextLong(), ids)
        assertTrue(allExists)
    }


    @Test
    fun `should return true if passing empty list`() {
        val ids = emptyList<Id>()
        Mockito.`when`(opponentDao.findAllOpponentsCreatedBy(anyLong()))
                .thenReturn(listOf(randomOpponent(), randomOpponent(), randomOpponent()))

        val allExists = opponentService.allExists(nextLong(), ids)
        assertTrue(allExists)
    }


    @Test
    fun `should return false if not all ids exists in db`() {
        val ids = listOf(nextLong(), nextLong(), nextLong())
        Mockito.`when`(opponentDao.findAllOpponentsCreatedBy(anyLong()))
                .thenReturn(listOf(randomOpponent(ids[0]), randomOpponent(ids[1]), randomOpponent()))

        val allExists = opponentService.allExists(nextLong(), ids)
        assertFalse(allExists)
    }

    @Test
    fun `should detect that does not belong to another user when no opponent`() {
        `when`(opponentDao.findById(anyLong())).thenReturn(null)

        val belongsToAnother = opponentService.belongsToAnotherUser(nextLong(), nextLong())

        assertFalse(belongsToAnother)
    }

    @Test
    fun `should detect that does not belong to another user when created by current user`() {
        val userId = nextLong()
        `when`(opponentDao.findById(anyLong())).thenReturn(Opponent(nextLong(), randomAlphabetic(11), userId))

        val belongsToAnother = opponentService.belongsToAnotherUser(userId, nextLong())

        assertFalse(belongsToAnother)
    }

    @Test
    fun `should detect that belongs to another user`() {
        `when`(opponentDao.findById(anyLong())).thenReturn(Opponent(nextLong(), randomAlphabetic(11), nextLong()))

        val belongsToAnother = opponentService.belongsToAnotherUser(nextLong(), nextLong())

        assertTrue(belongsToAnother)
    }



    private fun randomOpponent(id: Id = nextLong()) = Opponent(id, randomAlphabetic(11), nextLong())

}
