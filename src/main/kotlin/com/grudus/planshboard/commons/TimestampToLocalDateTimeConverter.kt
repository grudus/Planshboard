package com.grudus.planshboard.commons

import org.jooq.Converter
import java.sql.Timestamp
import java.time.LocalDateTime


class TimestampToLocalDateTimeConverter : Converter<Timestamp, LocalDateTime> {

    override fun from(databaseObject: Timestamp?): LocalDateTime? =
            databaseObject?.toLocalDateTime()

    override fun to(userObject: LocalDateTime): Timestamp =
            Timestamp.valueOf(userObject)

    override fun fromType(): Class<Timestamp> =
            Timestamp::class.java

    override fun toType(): Class<LocalDateTime> =
            LocalDateTime::class.java
}