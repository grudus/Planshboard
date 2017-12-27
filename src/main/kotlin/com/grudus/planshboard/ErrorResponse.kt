package com.grudus.planshboard

import com.grudus.planshboard.commons.RestKeys

class ErrorResponse(val message: String, val codes: List<String> = listOf(RestKeys.UNKNOWN_ERROR)) {
    constructor(message: String, code: String): this(message, listOf(code))
}