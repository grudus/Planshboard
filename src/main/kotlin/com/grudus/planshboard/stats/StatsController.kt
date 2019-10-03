package com.grudus.planshboard.stats

import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.configuration.security.AuthenticatedUser
import com.grudus.planshboard.stats.models.StatsDto
import com.grudus.planshboard.user.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/stats")
class StatsController
@Autowired
constructor(private val statsService: StatsService,
            private val userService: UserService) {

    @GetMapping
    fun generateStats(authUser: AuthenticatedUser,
                      @RequestParam(required = false) forOpponentId: Id?): StatsDto {
        val opponentId = forOpponentId ?: userService.getCurrentUser(authUser).opponentEntityId!!
        return statsService.generateStats(authUser.userId, opponentId)
    }
}
