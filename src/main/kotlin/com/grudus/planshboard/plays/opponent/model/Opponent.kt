package com.grudus.planshboard.plays.opponent.model

import com.grudus.planshboard.commons.Id

class Opponent(var id: Id?, val name: String, val createdBy: Id, val pointingToUser: Id? = null) {
    @Suppress("unused") //Jooq uses it
    constructor(id: Id?, name: String, createdBy: Id): this(id, name, createdBy, null)
}
