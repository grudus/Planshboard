package com.grudus.planshboard.stats

import com.grudus.planshboard.configuration.security.AuthenticatedUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/stats")
class StatsController
@Autowired
constructor(private val statsService: StatsService) {

    @GetMapping("/board-games")
    fun countBoardGames(user: AuthenticatedUser): CountResponse =
            statsService.countAllBoardGames(user.userId)
                    .let { CountResponse(it) }

    @GetMapping("/plays")
    fun countAllPlays(user: AuthenticatedUser): CountResponse =
            statsService.countAllPlays(user.userId)
                    .let { CountResponse(it) }

    @GetMapping("/wins")
    fun countPlayPositionPerOpponent(user: AuthenticatedUser): List<OpponentCount> =
            statsService.countPlayPositionPerOpponent(user.userId)

    @GetMapping("/plays", params = ["per-game"])
    fun countPlaysPerBoardGame(user: AuthenticatedUser): List<PlaysCount> =
            statsService.countPlaysPerBoardGame(user.userId)
}