package com.grudus.planshboard.plays.opponent

import com.grudus.planshboard.commons.Id

class Opponent(var id: Id?, val userId: Id, val name: String, val isRealUser: Boolean) {
    @Suppress("unused") //Jooq uses it
    constructor(id: Id?, userId: Id, name: String): this(id, userId, name, false)
}