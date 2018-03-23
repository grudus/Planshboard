package com.grudus.planshboard

import com.grudus.planshboard.commons.Id

const val OPPONENTS_URL = "/api/opponents"
const val BOARD_GAMES_URL = "/api/board-games"
const val USERS_URL = "/api/users"
const val USERS_AUTH_REGISTRATION_URL = "/api/auth/register"

fun playsUrlPattern(boardGameId: Id) = "/api/board-games/$boardGameId/plays"