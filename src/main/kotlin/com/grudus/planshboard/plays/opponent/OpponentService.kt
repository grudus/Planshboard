package com.grudus.planshboard.plays.opponent

import com.grudus.planshboard.commons.Id
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OpponentService
@Autowired
constructor(private val opponentDao: OpponentDao) {

    fun addOpponent(userId: Id, addOpponentRequest: AddOpponentRequest) =
            opponentDao.addOpponent(addOpponentRequest.name, userId)

    fun addOpponent(userId: Id, name: String) =
            addOpponent(userId, AddOpponentRequest(name))

    fun addCurrentUserAsOpponent(userId: Id, userName: String) =
            opponentDao.addOpponentPointingToUser(userName, userId, userId)

    fun findAll(userId: Id): List<OpponentDto> =
            opponentDao.findAllOpponentsCreatedBy(userId)
                    .map { OpponentDto(it.id!!, it.name) }

    fun exists(currentUserId: Id, name: String): Boolean =
            opponentDao.findByName(name, currentUserId) != null

    fun allExists(userId: Id, opponents: List<Id>): Boolean {
        val allOpponents: List<Id> = opponentDao.findAllOpponentsCreatedBy(userId)
                .map { it.id!! }
        return allOpponents.containsAll(opponents)
    }

    fun allDoNotExist(currentUserId: Id, names: List<String>): Boolean =
            names.none { name -> exists(currentUserId, name) }
}
