package com.grudus.planshboard.plays.opponent

import com.grudus.planshboard.MockitoExtension
import com.grudus.planshboard.commons.RestKeys.EMPTY_NAME
import com.grudus.planshboard.commons.RestKeys.NAME_EXISTS
import com.grudus.planshboard.commons.RestKeys.USER_ASSIGNED_TO_ANOTHER_OPPONENT
import com.grudus.planshboard.commons.RestKeys.USER_NOT_EXIST
import com.grudus.planshboard.plays.opponent.model.OpponentDto
import com.grudus.planshboard.plays.opponent.model.SaveConnectedOpponentRequest
import com.grudus.planshboard.user.User
import com.grudus.planshboard.user.UserService
import com.grudus.planshboard.user.auth.AuthenticationService
import com.grudus.planshboard.utils.ValidatorUtils.assertErrorCodes
import com.grudus.planshboard.utils.ValidatorUtils.getErrors
import com.grudus.planshboard.utils.safeEq
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.*
import org.mockito.Mock
import org.mockito.Mockito.`when`
import kotlin.random.Random.Default.nextLong

@ExtendWith(MockitoExtension::class)
class SaveConnectedOpponentValidatorTest {

    @Mock
    private lateinit var authenticationService: AuthenticationService
    @Mock
    private lateinit var opponentService: OpponentService
    @Mock
    private lateinit var userService: UserService

    private lateinit var validator: SaveConnectedOpponentValidator

    @BeforeEach
    fun init() {
        validator = SaveConnectedOpponentValidator(opponentService, authenticationService, userService)
        `when`(authenticationService.currentUserId()).thenReturn(nextLong())
        `when`(opponentService.exists(anyLong(), anyString())).thenReturn(false)
    }

    @Test
    fun `should validate properly when creating new opponent not pointing to any user`() {
        val request = randomRequest(connectedUserName = null)
        val errors = getErrors(request)

        validator.validate(request, errors)

        assertFalse(errors.hasErrors())
    }

    @Test
    fun `should validate properly when creating new opponent pointing to some user`() {
        val user = randomUser()
        `when`(userService.findByUsername(anyString())).thenReturn(user)
        val request = randomRequest(connectedUserName = user.name)
        val errors = getErrors(request)

        validator.validate(request, errors)

        assertFalse(errors.hasErrors())
    }

    @Test
    fun `should validate properly when updating existing opponent`() {
        val id = nextLong()
        val name = randomAlphabetic(11)
        `when`(opponentService.findById(eq(id))).thenReturn(OpponentDto(id, name))
        val request = randomRequest(existingOpponentId = id, opponentName = name)
        val errors = getErrors(request)

        validator.validate(request, errors)

        assertFalse(errors.hasErrors())
    }

    @Test
    fun `should not validate properly when no opponent name`() {
        val request = randomRequest(opponentName = " ")
        val errors = getErrors(request)

        validator.validate(request, errors)

        assertErrorCodes(errors, EMPTY_NAME)
    }

    @Test
    fun `should not validate properly when saving opponent with non unique name`() {
        val name = randomAlphabetic(11)
        `when`(opponentService.findById(anyLong())).thenReturn(OpponentDto(nextLong(), randomAlphabetic(2)))
        `when`(opponentService.exists(anyLong(), safeEq(name))).thenReturn(true)
        val request = randomRequest(opponentName = name)
        val errors = getErrors(request)

        validator.validate(request, errors)

        assertErrorCodes(errors, NAME_EXISTS)
    }

    @Test
    fun `should not validate properly when saving opponent pointing to non existing user`() {
        val name = randomAlphabetic(11)
        `when`(userService.findByUsername(anyString())).thenReturn(null)

        val request = randomRequest(connectedUserName = name)
        val errors = getErrors(request)

        validator.validate(request, errors)

        assertErrorCodes(errors, USER_NOT_EXIST)
    }

    @Test
    fun `should not validate properly when saving opponent pointing to user, which already has pointing opponent`() {
        val name = randomAlphabetic(11)
        `when`(userService.findByUsername(anyString())).thenReturn(randomUser())
        `when`(opponentService.findOpponentByConnectedUser(anyLong(), anyLong())).thenReturn(OpponentDto(nextLong(), randomAlphabetic(1)))

        val request = randomRequest(connectedUserName = name)
        val errors = getErrors(request)

        validator.validate(request, errors)

        assertErrorCodes(errors, USER_ASSIGNED_TO_ANOTHER_OPPONENT)
    }

    private fun randomUser(): User =
            User(nextLong(), randomAlphabetic(11), randomAlphabetic(11))

    private fun randomRequest(opponentName: String = randomAlphabetic(11),
                              connectedUserName: String? = null,
                              existingOpponentId: Long? = null): SaveConnectedOpponentRequest =
            SaveConnectedOpponentRequest(opponentName, connectedUserName, existingOpponentId)
}

