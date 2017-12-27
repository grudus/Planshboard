package com.grudus.planshboard

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.grudus.planshboard.commons.RestKeys
import com.grudus.planshboard.commons.RestKeys.PARAMETER_NOT_RESENT
import com.grudus.planshboard.configuration.security.AuthenticatedUser
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.validation.BindException
import org.springframework.validation.BindingResult
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@ControllerAdvice
@ResponseBody
class ErrorHandler {

    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(NoSuchElementException::class)
    @ResponseStatus(NOT_FOUND)
    fun noSuchElementException(e: NoSuchElementException) {
        logger.warn("Element not found", e)
    }


    @ExceptionHandler(BindException::class)
    @ResponseStatus(BAD_REQUEST)
    fun bindExceptionException(e: BindException): ErrorResponse =
            ErrorResponse("An error occurred while parsing", toCodes(e.bindingResult))


    @ExceptionHandler(AccessDeniedException::class)
    fun accessDenied(response: HttpServletResponse, request: HttpServletRequest, user: AuthenticatedUser) {
        logger.warn("Access denied for user [${user.user.name}] for resource [${request.requestURI}]")
        response.sendError(403)
    }


    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(BAD_REQUEST)
    fun methodArgumentNotValidException(e: MethodArgumentNotValidException): ErrorResponse =
            ErrorResponse("An error occurred while parsing", toCodes(e.bindingResult))

    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(BAD_REQUEST)
    fun messageNotReadable(e: HttpMessageNotReadableException): ErrorResponse {
        val cause = e.rootCause
        return when(cause) {
            is MissingKotlinParameterException -> ErrorResponse("Cannot find parameter [${cause.parameter.name}] in request", PARAMETER_NOT_RESENT)
            else -> ErrorResponse("Cannot read request message")
        }
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun unknownError(e: Exception): ErrorResponse {
        logger.warn("Catch unknown error [${e.message}]", e)
        return ErrorResponse("Internal server error")
    }

    private fun toCodes(b: BindingResult): List<String> =
            b.allErrors.map { it.code }
}