package com.grudus.planshboard.boardgame

import com.grudus.planshboard.MockitoExtension
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.apache.commons.lang3.RandomUtils.nextLong
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito

@ExtendWith(MockitoExtension::class)
class BoardGameServiceTest {

    @Mock
    lateinit var boardGameDao: BoardGameDao

    @Test
    fun `should detect if belongs to another user`() {
        val userId = nextLong()
        Mockito.`when`(boardGameDao.findById(anyLong())).thenReturn(BoardGame(nextLong(), randomAlphabetic(11), nextLong()))

        val belongsToAnotherUser = BoardGameService(boardGameDao).belongsToAnotherUser(userId, nextLong())

        assertTrue(belongsToAnotherUser)
    }

    @Test
    fun `should detect not belongs to another user`() {
        val userId = nextLong()
        Mockito.`when`(boardGameDao.findById(anyLong())).thenReturn(BoardGame(nextLong(), randomAlphabetic(11), userId))

        val belongsToAnotherUser = BoardGameService(boardGameDao).belongsToAnotherUser(userId, nextLong())

        assertFalse(belongsToAnotherUser)
    }

    @Test
    fun `should detect not belongs to another user when game not exists`() {
        val userId = nextLong()
        Mockito.`when`(boardGameDao.findById(anyLong())).thenReturn(null)

        val belongsToAnotherUser = BoardGameService(boardGameDao).belongsToAnotherUser(userId, nextLong())

        assertFalse(belongsToAnotherUser)
    }

    @Test
    fun `should change name and return updated object`() {
        Mockito.`when`(boardGameDao.updateName(anyLong(), anyString())).thenReturn(1)
        val name = randomAlphabetic(11)

        val updated = BoardGameService(boardGameDao).update(nextLong(), EditBoardGameRequest(name))

        assertEquals(name, updated.name)
    }


    @Test
    fun `should throw error if dao doesn't update any item`() {
        Mockito.`when`(boardGameDao.updateName(anyLong(), anyString())).thenReturn(0)
        val name = randomAlphabetic(11)

        assertThrows(BoardGameNotFoundException::class.java) {
            BoardGameService(boardGameDao).update(nextLong(), EditBoardGameRequest(name))
        }
    }

}