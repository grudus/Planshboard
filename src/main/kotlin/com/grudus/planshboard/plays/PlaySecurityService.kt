package com.grudus.planshboard.plays

import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.configuration.security.AuthenticatedUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PlaySecurityService
@Autowired
constructor(private val playService: PlayService) {

    fun hasAccessToPlay(user: AuthenticatedUser, id: Id): Boolean =
            !playService.belongsToAnotherUser(user.userId, id)
}