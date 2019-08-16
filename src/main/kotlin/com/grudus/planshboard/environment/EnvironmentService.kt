package com.grudus.planshboard.environment

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
class EnvironmentService
@Autowired
constructor(private val environment: Environment) {

    fun getTextSafe(key: String): String? =
            environment.getProperty(key)

    fun getText(key: String): String =
            environment.getProperty(key) ?: throw CannotFindKeyException(key)
}
