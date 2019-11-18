package com.grudus.planshboard.plays.opponent

import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.configuration.security.AuthenticatedUser
import com.grudus.planshboard.plays.PlayService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OpponentSecurityService
@Autowired
constructor(private val opponentService: OpponentService) {

    fun hasAccessToOpponent(user: AuthenticatedUser, id: Id): Boolean =
            !opponentService.belongsToAnotherUser(user.userId, id)
}
