package com.grudus.planshboard.plays

import com.grudus.planshboard.MockitoExtension
import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.commons.RestKeys
import com.grudus.planshboard.plays.model.AddPlayRequest
import com.grudus.planshboard.plays.model.AddPlayResult
import com.grudus.planshboard.plays.opponent.OpponentService
import com.grudus.planshboard.user.auth.AuthenticationService
import com.grudus.planshboard.utils.ValidatorUtils.assertErrorCodes
import com.grudus.planshboard.utils.ValidatorUtils.getErrors
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.apache.commons.lang3.RandomUtils.nextInt
import org.apache.commons.lang3.RandomUtils.nextLong
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyList
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mock
import org.mockito.Mockito.`when`

@ExtendWith(MockitoExtension::class)
class AddPlayRequestValidatorTest {


    @Mock
    private lateinit var authenticationService: AuthenticationService

    @Mock
    private lateinit var opponentService: OpponentService

    private lateinit var validator: AddPlayRequestValidator

    @BeforeEach
    fun init() {
        validator = AddPlayRequestValidator(authenticationService, opponentService)
        `when`(authenticationService.currentUserId()).thenReturn(nextLong())
        `when`(opponentService.allExists(anyLong(), anyList())).thenReturn(true)
        `when`(opponentService.allDoNotExist(anyLong(), anyList())).thenReturn(true)
    }

    @Test
    fun `should validate properly`() {
        val request = AddPlayRequest(randomOpponents())
        val errors = getErrors(request)

        validator.validate(request, errors)

        assertFalse(errors.hasErrors())
    }


    @Test
    fun `should not validate properly when no opponents`() {
        val request = AddPlayRequest(emptyList())
        val errors = getErrors(request)

        validator.validate(request, errors)

        assertEquals(1, errors.errorCount)
        assertErrorCodes(errors, RestKeys.NO_RESULTS)
    }

    @Test
    fun `should not validate properly when opponents with id doesn't exist`() {
        `when`(opponentService.allExists(anyLong(), anyList())).thenReturn(false)

        val request = AddPlayRequest(randomOpponents(id = 5))
        val errors = getErrors(request)

        validator.validate(request, errors)

        assertEquals(1, errors.errorCount)
        assertErrorCodes(errors, RestKeys.OPPONENTS_NOT_EXISTS)
    }

    @Test
    fun `should not validate properly when opponents without id exist`() {
        `when`(opponentService.allDoNotExist(anyLong(), anyList())).thenReturn(false)

        val request = AddPlayRequest(randomOpponents())
        val errors = getErrors(request)

        validator.validate(request, errors)

        assertEquals(1, errors.errorCount)
        assertErrorCodes(errors, RestKeys.OPPONENTS_EXISTS)
    }





    private fun randomOpponents(name: String = randomAlphabetic(11), id: Id? = null) = listOf(AddPlayResult(name, nextInt(), nextInt(), id))

}