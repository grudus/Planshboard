package com.grudus.planshboard.plays

import com.grudus.planshboard.boardgame.BoardGameService
import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.commons.exceptions.ResourceNotFoundException
import com.grudus.planshboard.plays.opponent.Opponent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PlayService
@Autowired
constructor(private val playDao: PlayDao,
            private val boardGameService: BoardGameService) {

    fun findOpponentsForPlay(playId: Id): List<Opponent> =
            if (playDao.findById(playId) != null)
                playDao.findOpponentsForPlay(playId)
            else throw ResourceNotFoundException("Cannot find play with id [$playId]")

    fun savePlay(boardGameId: Id, opponents: List<Id>): Id {
        require(opponents.isNotEmpty()) { "Cannot save play without any opponent" }

        val playId = playDao.insertPlayAlone(boardGameId)
        playDao.insertPlayOpponents(playId, opponents)

        return playId
    }

    fun belongsToAnotherUser(userId: Id, playId: Id): Boolean =
            playDao.findById(playId)
                    ?.let { play -> boardGameService.belongsToAnotherUser(userId, play.boardGameId) }
                    ?: false
}