package com.grudus.planshboard.games.opponent

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
        Mockito.`when`(opponentDao.findAllOpponentsWithReal(anyLong()))
                .thenReturn(listOf(randomOpponent(ids[0]), randomOpponent(ids[1]), randomOpponent()))

        val allExists = opponentService.allExists(nextLong(), ids)
        assertTrue(allExists)
    }


    @Test
    fun `should return true if passing empty list`() {
        val ids = emptyList<Id>()
        Mockito.`when`(opponentDao.findAllOpponentsWithReal(anyLong()))
                .thenReturn(listOf(randomOpponent(), randomOpponent(), randomOpponent()))

        val allExists = opponentService.allExists(nextLong(), ids)
        assertTrue(allExists)
    }


    @Test
    fun `should return false if not all ids exists in db`() {
        val ids = listOf(nextLong(), nextLong(), nextLong())
        Mockito.`when`(opponentDao.findAllOpponentsWithReal(anyLong()))
                .thenReturn(listOf(randomOpponent(ids[0]), randomOpponent(ids[1]), randomOpponent()))

        val allExists = opponentService.allExists(nextLong(), ids)
        assertFalse(allExists)
    }


    private fun randomOpponent(id: Id = nextLong()) = Opponent(id, nextLong(), randomAlphabetic(11))

}