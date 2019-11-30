package com.grudus.planshboard.ranking

import com.grudus.planshboard.configuration.security.AuthenticatedUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/ranking")
class RankingController
constructor(private val rankingService: RankingService) {

    @GetMapping("/most-frequent-first-position")
    fun getMostFrequentFirstPosition(user: AuthenticatedUser): List<MostFrequentFirstPosition> =
        rankingService.getMostFrequentFirstPosition(user.id)
}
