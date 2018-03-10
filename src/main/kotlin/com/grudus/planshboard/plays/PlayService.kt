package com.grudus.planshboard.plays

import com.grudus.planshboard.boardgame.BoardGameService
import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.commons.exceptions.ResourceNotFoundException
import com.grudus.planshboard.plays.model.AddPlayOpponent
import com.grudus.planshboard.plays.model.PlayResult
import com.grudus.planshboard.plays.opponent.Opponent
import com.grudus.planshboard.plays.opponent.OpponentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PlayService
@Autowired
constructor(private val playDao: PlayDao,
            private val boardGameService: BoardGameService,
            private val opponentService: OpponentService) {

    fun findOpponentsForPlay(playId: Id): List<Opponent> =
            if (playDao.findById(playId) != null)
                playDao.findOpponentsForPlay(playId)
            else throw ResourceNotFoundException("Cannot find play with id [$playId]")


    fun belongsToAnotherUser(userId: Id, playId: Id): Boolean =
            playDao.findById(playId)
                    ?.let { play -> boardGameService.belongsToAnotherUser(userId, play.boardGameId) }
                    ?: false


    fun savePlay(userId: Id,
                 boardGameId: Id,
                 opponents: List<AddPlayOpponent>): Id {
        require(opponents.isNotEmpty()) { "Cannot save play without any opponent" }

        val playId = playDao.insertPlayAlone(boardGameId)
        addOpponentsToPlay(userId, playId, opponents)

        return playId
    }


    private fun addOpponentsToPlay(userId: Id, playId: Id, opponents: List<AddPlayOpponent>) {
        val (existingOpponents, newOpponents) = opponents.partition { it.id != null }
        val insertedOpponents = newOpponents.map {
            it.copy(id = opponentService.addOpponent(userId, it.name))
        }

        val playResults = (existingOpponents + insertedOpponents).map { opponent ->
            PlayResult(playId, opponent.id!!, opponent.points, opponent.position)
        }

        playDao.savePlayResults(playResults)
    }
}