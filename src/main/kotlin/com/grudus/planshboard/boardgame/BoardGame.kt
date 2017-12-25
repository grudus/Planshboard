package com.grudus.planshboard.boardgame

import java.time.LocalDate

class BoardGame(val id: Int? = null,
                val name: String,
                val minPlayers: Int? = null,
                val maxPlayers: Int? = null,
                val averagePlayingTime: Int? = null,
                val releaseYear: LocalDate? = null) {
}