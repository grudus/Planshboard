package com.grudus.planshboard.boardgame

import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.configuration.security.AuthenticatedUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BoardGameSecurityService
@Autowired constructor(private val boardGameService: BoardGameService) {

    fun hasAccessToBoardGame(user: AuthenticatedUser, id: Id): Boolean =
            !boardGameService.belongsToAnotherUser(user.userId, id)
}