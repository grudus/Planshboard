package com.grudus.planshboard.games.opponent

import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.user.UserDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OpponentService
@Autowired
constructor(private val opponentDao: OpponentDao){

    fun addOpponent(userId: Id, addOpponentRequest: AddOpponentRequest) =
            opponentDao.addOpponent(userId, addOpponentRequest.name)

    fun findAll(userId: Id): List<OpponentDto> =
            opponentDao.findAllOpponents(userId)
                    .map { OpponentDto(it.id!!, it.name) }
}