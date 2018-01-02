package com.grudus.planshboard.boardgame

import com.grudus.planshboard.MockitoExtension
import com.grudus.planshboard.commons.RestKeys.EMPTY_NAME
import com.grudus.planshboard.commons.RestKeys.NAME_EXISTS
import com.grudus.planshboard.user.auth.AuthenticationService
import com.grudus.planshboard.utils.ValidatorUtils.assertErrorCodes
import com.grudus.planshboard.utils.ValidatorUtils.getErrors
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.apache.commons.lang3.RandomUtils.nextLong
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito

@ExtendWith(MockitoExtension::class)
class AddBoardGameRequestValidatorTest {

    @Mock
    private lateinit var authService: AuthenticationService

    @Mock
    private lateinit var boardGameService: BoardGameService


    private lateinit var validator: AddBoardGameRequestValidator

    @BeforeEach
    fun init() {
        Mockito.`when`(authService.currentUserId()).thenReturn(nextLong())
        Mockito.`when`(boardGameService.exists(anyLong(), anyString())).thenReturn(false)
        validator = AddBoardGameRequestValidator(authService, boardGameService)
    }

    @Test
    fun `should validate properly`() {
        val request = AddBoardGameRequest(randomAlphabetic(11))
        val errors = getErrors(request)

        validator.validate(request, errors)

        assertFalse(errors.hasErrors())
    }


    @Test
    fun `should not validate properly when empty name`() {
        val request = AddBoardGameRequest("")
        val errors = getErrors(request)

        validator.validate(request, errors)

        assertErrorCodes(errors, EMPTY_NAME)
    }

    @Test
    fun `should not validate properly when name exists`() {
        val request = AddBoardGameRequest(randomAlphabetic(11))
        val errors = getErrors(request)
        Mockito.`when`(boardGameService.exists(anyLong(), anyString())).thenReturn(true)

        validator.validate(request, errors)

        assertErrorCodes(errors, NAME_EXISTS)
    }
}