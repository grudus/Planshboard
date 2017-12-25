package com.grudus.planshboard.commons.date

import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.core.JsonGenerator
import java.time.LocalDateTime
import com.fasterxml.jackson.databind.JsonSerializer
import com.grudus.planshboard.commons.Constants.DATE_TIME_PATTERN
import java.time.format.DateTimeFormatter.ofPattern


object JsonLocalDateTimeSerializer : JsonSerializer<LocalDateTime>() {

    override fun serialize(localDateTime: LocalDateTime, jsonGenerator: JsonGenerator, serializerProvider: SerializerProvider) {
        jsonGenerator.writeString(localDateTime.format(ofPattern(DATE_TIME_PATTERN)))
    }
}