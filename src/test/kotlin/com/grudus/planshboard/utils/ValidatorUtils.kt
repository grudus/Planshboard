package com.grudus.planshboard.utils

import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors


object ValidatorUtils {

    fun <T> getErrors(request: T): Errors = BeanPropertyBindingResult(request, "request")

    fun assertErrorCodes(error: Errors, vararg codes: String) {
        assertTrue(codes.size == error.errorCount)
        assertTrue(errorCodes(error).containsAll(listOf(*codes)))
    }

    private fun errorCodes(errors: Errors): List<String> =
            errors.allErrors.map { it.code }
}