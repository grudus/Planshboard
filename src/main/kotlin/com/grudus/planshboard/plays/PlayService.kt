package com.grudus.planshboard.plays

import com.grudus.planshboard.boardgame.BoardGameNotFoundException
import com.grudus.planshboard.boardgame.BoardGameService
import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.commons.exceptions.ResourceNotFoundException
import com.grudus.planshboard.plays.model.*
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


    fun getPlayResults(userId: Id, boardGameId: Id): List<PlayResponse> {
        if (!boardGameService.existsForUser(userId, boardGameId))
            throw BoardGameNotFoundException("Cannot find board game with id [$boardGameId]")

        val plays: List<Play> = playDao.findPlaysForBoardGame(boardGameId)
        val playResults: List<PlayOpponentsDto> = playDao.findPlayResultsForPlays(plays.map { it.id })
        val playResultsPerPlay = playResults.groupBy({ it.playId }, { PlayOpponentsResponse(it) })

        return plays.sortedByDescending { it.date }
                .map { play ->
                    PlayResponse(play.id, play.date, playResultsPerPlay[play.id]!!, play.note)
                }
    }

    fun savePlay(userId: Id,
                 boardGameId: Id,
                 request: AddPlayRequest): Id {
        val playId = playDao.insertPlayAlone(boardGameId, request.date, request.note)
        addOpponentsToPlay(userId, playId, request.results)

        return playId
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
    }
}