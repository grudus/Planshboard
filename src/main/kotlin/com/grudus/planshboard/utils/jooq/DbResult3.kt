package com.grudus.planshboard.utils.jooq

import org.jooq.Record3

data class DbResult3<A, B, C>(val a: A, val b: B, val c: C) {
    constructor(record: Record3<A, B, C>): this(record.field1()[record], record.field2()[record], record.field3()[record])
}