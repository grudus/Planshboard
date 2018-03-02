package com.grudus.planshboard.plays.opponent

import com.grudus.planshboard.commons.Id
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OpponentService
@Autowired
constructor(private val opponentDao: OpponentDao) {

    fun addOpponent(userId: Id, addOpponentRequest: AddOpponentRequest) =
            opponentDao.addOpponent(userId, addOpponentRequest.name)

    fun addCurrentUserAsOpponent(userId: Id, userName: String) =
            opponentDao.addOpponent(userId, userName, true)

    fun findAll(userId: Id): List<OpponentDto> =
            opponentDao.findAllOpponentsWithoutReal(userId)
                    .map { OpponentDto(it.id!!, it.name) }

    fun exists(currentUserId: Id, name: String): Boolean =
            opponentDao.findByName(currentUserId, name) != null

    fun allExists(userId: Id, opponents: List<Id>): Boolean {
        val allOpponents: List<Id> = opponentDao.findAllOpponentsWithReal(userId)
                .map { it.id!! }
        return allOpponents.containsAll(opponents)
    }
}