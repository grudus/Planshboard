package com.grudus.planshboard.plays.opponent

import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.plays.opponent.model.OpponentDto.Companion.fromOpponent
import com.grudus.planshboard.plays.opponent.model.AddOpponentRequest
import com.grudus.planshboard.plays.opponent.model.ConnectedOpponentDto
import com.grudus.planshboard.plays.opponent.model.OpponentDto
import com.grudus.planshboard.plays.opponent.model.SaveConnectedOpponentRequest
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

    fun addOpponent(userId: Id, opponentName: String, pointingToUserId: Id?): Id {
        return if (pointingToUserId != null)
            opponentDao.addOpponentPointingToUser(opponentName, userId, pointingToUserId)
        else opponentDao.addOpponent(opponentName, userId)
    }

    fun findAllWithConnectedUsers(userId: Id): List<ConnectedOpponentDto> =
            opponentDao.findAllWithConnectedUsers(userId)

    fun findAll(userId: Id): List<OpponentDto> =
            opponentDao.findAllOpponentsCreatedBy(userId)
                    .map(::fromOpponent)

    fun exists(currentUserId: Id, name: String): Boolean =
            opponentDao.findByName(name, currentUserId) != null

    fun allExists(userId: Id, opponents: List<Id>): Boolean {
        val allOpponents: List<Id> = opponentDao.findAllOpponentsCreatedBy(userId)
                .map { it.id!! }
        return allOpponents.containsAll(opponents)
    }

    fun allDoNotExist(currentUserId: Id, names: List<String>): Boolean =
            names.none { name -> exists(currentUserId, name) }

    fun findOpponentPointingToCurrentUser(currentUserId: Id): OpponentDto =
            fromOpponent(
                    opponentDao.findOpponentPointingToCurrentUser(currentUserId)
            )

    fun findById(opponentId: Id): OpponentDto? =
            opponentDao.findById(opponentId)
                    ?.let { fromOpponent(it) }

    fun findOpponentByConnectedUser(createdBy: Id, pointingTo: Id?): OpponentDto? =
            opponentDao.findOpponentByConnectedUser(createdBy, pointingTo)
                    ?.let { fromOpponent(it) }

    fun editOpponent(request: SaveConnectedOpponentRequest, pointingToUser: Id?) {
        requireNotNull(request.existingOpponentId) {"Cannot update opponent without id $request"}
        opponentDao.editOpponent(request.existingOpponentId, request.opponentName, pointingToUser)
    }

    fun getPointingToUserId(opponentId: Id): Id? =
            findById(opponentId)?.pointingToUser

    fun belongsToAnotherUser(userId: Id, opponentId: Id): Boolean {
        val opponent = opponentDao.findById(opponentId)
        return !(opponent == null || opponent.createdBy == userId)
    }
}
