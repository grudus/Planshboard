package com.grudus.planshboard.user.auth

import com.grudus.planshboard.MockitoExtension
import com.grudus.planshboard.commons.RestKeys
import com.grudus.planshboard.commons.RestKeys.EMPTY_PASSWORD
import com.grudus.planshboard.commons.RestKeys.EMPTY_USERNAME
import com.grudus.planshboard.commons.RestKeys.USERNAME_EXISTS
import com.grudus.planshboard.user.UserService
import com.grudus.planshboard.utils.ValidatorUtils.assertErrorCodes
import com.grudus.planshboard.utils.ValidatorUtils.getErrors
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*

@ExtendWith(MockitoExtension::class)
internal class AddUserRequestValidatorTest {

    @Mock
    private lateinit var userService: UserService

    private lateinit var validator: AddUserRequestValidator

    @BeforeEach
    fun init() {
        `when`(userService.usernameExists(anyString())).thenReturn(false)
        validator = AddUserRequestValidator(userService)
    }

    @Test
    fun `should pass validation`() {
        val request = AddUserRequest(randomAlphabetic(11), randomAlphabetic(11))
        val errors = getErrors(request)

        validator.validate(request, errors)

        assertFalse(errors.hasErrors())
    }

    @Test
    fun `should not pass validation when username isn't present`() {
        val request = AddUserRequest("", randomAlphabetic(11))
        val errors = getErrors(request)

        validator.validate(request, errors)

        assertErrorCodes(errors, EMPTY_USERNAME)
    }


    @Test
    fun `should not pass validation when password isn't present`() {
        val request = AddUserRequest(randomAlphabetic(11), " ")
        val errors = getErrors(request)

        validator.validate(request, errors)

        assertErrorCodes(errors, EMPTY_PASSWORD)
    }


    @Test
    fun `should not pass validation when username already exists`() {
        val request = AddUserRequest(randomAlphabetic(11), randomAlphabetic(11))
        val errors = getErrors(request)
        `when`(userService.usernameExists(anyString())).thenReturn(true)

        validator.validate(request, errors)

        assertErrorCodes(errors, USERNAME_EXISTS)
    }

    @Test
    fun `should not pass validation when password isn't present and username already exists`() {
        val request = AddUserRequest(randomAlphabetic(11), "")
        val errors = getErrors(request)
        `when`(userService.usernameExists(anyString())).thenReturn(true)

        validator.validate(request, errors)

        assertErrorCodes(errors, USERNAME_EXISTS, EMPTY_PASSWORD)
    }


    @Test
    fun `should not check if username is available when it isn't present`() {
        val request = AddUserRequest("", "")
        val errors = getErrors(request)
        `when`(userService.usernameExists(anyString())).thenReturn(true)

        validator.validate(request, errors)

        assertErrorCodes(errors, EMPTY_USERNAME, EMPTY_PASSWORD)
        verify(userService, never()).usernameExists(anyString())
    }
}