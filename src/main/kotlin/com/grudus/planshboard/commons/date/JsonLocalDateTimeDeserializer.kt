package com.grudus.planshboard.commons.date

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import java.time.LocalDateTime
import com.fasterxml.jackson.databind.JsonDeserializer
import com.grudus.planshboard.commons.Constants.DATE_TIME_PATTERN
import java.time.format.DateTimeFormatter.ofPattern


object JsonLocalDateTimeDeserializer : JsonDeserializer<LocalDateTime>() {

    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): LocalDateTime =
            LocalDateTime.parse(jsonParser.text, ofPattern(DATE_TIME_PATTERN))
}