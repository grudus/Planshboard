package com.grudus.planshboard.games

import com.grudus.planshboard.MockitoExtension
import com.grudus.planshboard.boardgame.BoardGameService
import com.grudus.planshboard.commons.RestKeys
import com.grudus.planshboard.games.opponent.OpponentService
import com.grudus.planshboard.user.auth.AuthenticationService
import com.grudus.planshboard.utils.ValidatorUtils.assertErrorCodes
import com.grudus.planshboard.utils.ValidatorUtils.getErrors
import org.apache.commons.lang3.RandomUtils.nextLong
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyList
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mock
import org.mockito.Mockito.`when`

@ExtendWith(MockitoExtension::class)
class AddGameRequestValidatorTest {


    @Mock
    private lateinit var authenticationService: AuthenticationService

    @Mock
    private lateinit var boardGameService: BoardGameService

    @Mock
    private lateinit var opponentService: OpponentService

    private lateinit var validator: AddGameRequestValidator

    @BeforeEach
    fun init() {
        validator = AddGameRequestValidator(authenticationService, boardGameService, opponentService)
        `when`(authenticationService.currentUserId()).thenReturn(nextLong())
        `when`(opponentService.allExists(anyLong(), anyList())).thenReturn(true)

    }

    @Test
    fun `should validate properly`() {
        `when`(boardGameService.existsForUser(anyLong(), anyLong())).thenReturn(true)
        val request = AddGameRequest(nextLong(), listOf(nextLong()))
        val errors = getErrors(request)

        validator.validate(request, errors)

        assertFalse(errors.hasErrors())
    }

    @Test
    fun `should not validate properly when no opponents`() {
        `when`(boardGameService.existsForUser(anyLong(), anyLong())).thenReturn(true)
        val request = AddGameRequest(nextLong(), listOf())
        val errors = getErrors(request)

        validator.validate(request, errors)

        assertErrorCodes(errors, RestKeys.NO_OPPONENTS)
    }

    @Test
    fun `should not validate properly when opponents not exists`() {
        `when`(boardGameService.existsForUser(anyLong(), anyLong())).thenReturn(true)
        `when`(opponentService.allExists(anyLong(), anyList())).thenReturn(false)

        val request = AddGameRequest(nextLong(), listOf(nextLong()))
        val errors = getErrors(request)

        validator.validate(request, errors)

        assertErrorCodes(errors, RestKeys.OPPONENTS_NOT_EXISTS)
    }



    @Test
    fun `should not validate properly when game not exists`() {
        `when`(boardGameService.existsForUser(anyLong(), anyLong())).thenReturn(false)
        val request = AddGameRequest(nextLong(), listOf(nextLong(), nextLong()))
        val errors = getErrors(request)

        validator.validate(request, errors)

        assertErrorCodes(errors, RestKeys.BOARD_GAME_NOT_EXISTS)
    }

}