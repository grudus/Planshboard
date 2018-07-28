package com.grudus.planshboard.stats

import com.grudus.planshboard.configuration.security.AuthenticatedUser
import com.grudus.planshboard.stats.models.StatsDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/stats")
class StatsController
@Autowired
constructor(private val statsService: StatsService) {

    @GetMapping
    fun generateStats(authenticatedUser: AuthenticatedUser): StatsDto =
            statsService.generateStats(authenticatedUser.userId)
}