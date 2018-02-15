package com.grudus.planshboard.games.opponent

import com.grudus.planshboard.commons.Id

class Opponent(var id: Id?, val userId: Id, val name: String, val isRealUser: Boolean) {
    constructor(id: Id?, userId: Id, name: String): this(id, userId, name, false)
}