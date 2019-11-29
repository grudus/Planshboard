package com.grudus.planshboard.plays

import com.grudus.planshboard.boardgame.BoardGameNotFoundException
import com.grudus.planshboard.boardgame.BoardGameService
import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.commons.exceptions.ResourceNotFoundException
import com.grudus.planshboard.notifications.publisher.NotificationPublisher
import com.grudus.planshboard.plays.model.*
import com.grudus.planshboard.plays.opponent.model.Opponent
import com.grudus.planshboard.plays.opponent.OpponentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PlayService
@Autowired
constructor(private val playDao: PlayDao,
            private val boardGameService: BoardGameService,
            private val opponentService: OpponentService,
            private val notificationPublisher: NotificationPublisher) {

    fun findOpponentsForPlay(playId: Id): List<Opponent> =
            if (playDao.findById(playId) != null)
                playDao.findOpponentsForPlay(playId)
            else throw ResourceNotFoundException("Cannot find play with id [$playId]")


    fun belongsToAnotherUser(userId: Id, playId: Id): Boolean =
            playDao.findById(playId)
                    ?.let { play -> boardGameService.belongsToAnotherUser(userId, play.boardGameId) }
                    ?: false


    fun getPlayResults(userId: Id, boardGameId: Id): List<PlayResponse> {
        if (!boardGameService.existsForUser(userId, boardGameId))
            throw BoardGameNotFoundException("Cannot find board game with id [$boardGameId]")

       return playDao.findPlaysForBoardGame(boardGameId)
    }

    fun savePlay(userId: Id,
                 boardGameId: Id,
                 request: SavePlayRequest): Id {
        val playId = playDao.insertPlayAlone(boardGameId, request.date, request.note)
        addOpponentsToPlay(userId, playId, request.results)

        return playId
    }


    fun delete(playId: Id) {
        playDao.delete(playId)
    }

    fun updatePlay(userId: Id, playId: Id, request: SavePlayRequest): PlayResponse {
        playDao.updatePlayAlone(playId, request.date, request.note)
        playDao.removeAllPlayResults(playId)
        addOpponentsToPlay(userId, playId, request.results)

        return findPlay(playId)
    }

    private fun findPlay(playId: Id): PlayResponse {
        val play: Play = playDao.findById(playId) ?: throw ResourceNotFoundException("Cannot find play with id $playId")
        val results: List<PlayOpponentsDto> = playDao.findPlayResultsForPlays(listOf(playId))

        return PlayResponse(playId, play.date, results.map { PlayOpponentsResponse(it) }, play.note)
    }

    private fun addOpponentsToPlay(userId: Id, playId: Id, results: List<AddPlayResult>) {
        val (existingOpponents, newOpponents) = results.partition { it.opponentId != null }
        val insertedOpponents = newOpponents.map {
            it.copy(opponentId = opponentService.addOpponent(userId, it.opponentName))
        }

        val playResults = (existingOpponents + insertedOpponents).map { opponent ->
            PlayResult(playId, opponent.opponentId!!, opponent.points, opponent.position)
        }

        playDao.savePlayResults(playResults)
        notificationPublisher.notifyUsedIsMarkedAsOpponent(userId, playId, results)
    }
}
