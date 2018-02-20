package com.grudus.planshboard.games

import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.configuration.security.AuthenticatedUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GameSecurityService
@Autowired
constructor(private val gameService: GameService) {

    fun hasAccessToGame(user: AuthenticatedUser, id: Id): Boolean =
            !gameService.belongsToAnotherUser(user.userId, id)
}