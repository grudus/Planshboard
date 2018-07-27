package com.grudus.planshboard.utils

import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.plays.PlayDao
import com.grudus.planshboard.plays.PlayService
import com.grudus.planshboard.plays.model.PlayResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PlayUtil
@Autowired
constructor(private val playDao: PlayDao){


    fun addPlay(boardGameId: Id, opponents: List<Id>): Id {
        val playId = playDao.insertPlayAlone(boardGameId)
        val playResults = opponents.map { PlayResult(playId, it, null, null) }
        playDao.savePlayResults(playResults)
        return playId
    }

    fun addPlays(boardGameId: Id, opponents: List<Id>, numberOfPlays: Int) {
        repeat(numberOfPlays) {
            addPlay(boardGameId, opponents)
        }
    }
}