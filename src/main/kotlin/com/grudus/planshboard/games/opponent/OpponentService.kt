package com.grudus.planshboard.games.opponent

import com.grudus.planshboard.commons.Id
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OpponentService
@Autowired
constructor(private val opponentDao: OpponentDao){

    fun addOpponent(userId: Id, addOpponentRequest: AddOpponentRequest) =
            opponentDao.addOpponent(userId, addOpponentRequest.name)

    fun addCurrentUserAsOpponent(userId: Id, userName: String) =
            opponentDao.addOpponent(userId, userName, true)

    fun findAll(userId: Id): List<OpponentDto> =
            opponentDao.findAllOpponents(userId)
                    .map { OpponentDto(it.id!!, it.name) }

    fun exists(currentUserId: Id, name: String): Boolean =
            opponentDao.findByName(currentUserId, name) != null
}