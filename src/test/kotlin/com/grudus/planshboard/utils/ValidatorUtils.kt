package com.grudus.planshboard.utils

import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors


object ValidatorUtils {

    fun <T> getErrors(request: T): Errors = BeanPropertyBindingResult(request, "request")

    fun assertErrorCodes(error: Errors, vararg codes: String) {
        assertTrue(codes.size == error.errorCount)
        val errorCodes = errorCodes(error)
        val providedCodes = listOf(*codes)

        providedCodes.forEach { code ->
            assertTrue(errorCodes.contains(code), "Error object does not contains $code code")
        }
        errorCodes.forEach {code ->
            assertTrue(providedCodes.contains(code), "Provided codes does not contain $code code")
        }
    }

    private fun errorCodes(errors: Errors): List<String> =
            errors.allErrors.map { it.code }
}
