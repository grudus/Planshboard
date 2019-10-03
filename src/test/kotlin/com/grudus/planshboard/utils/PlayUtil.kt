package com.grudus.planshboard.utils

import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.plays.PlayDao
import com.grudus.planshboard.plays.PlayService
import com.grudus.planshboard.plays.model.PlayResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.LocalDateTime.now

@Component
class PlayUtil
@Autowired
constructor(private val playDao: PlayDao){

    fun addPlay(boardGameId: Id,
                opponents: List<Id>,
                opponentsMapper: (Id, Id) -> PlayResult = {id, playId -> PlayResult(playId, id, null, null) },
                date: LocalDateTime = now(),
                note: String? = null): Id {
        val playId = playDao.insertPlayAlone(boardGameId, date, note)
        val playResults = opponents.map { id -> opponentsMapper(id, playId) }
        playDao.savePlayResults(playResults)
        return playId
    }

    fun addPlays(boardGameId: Id, opponents: List<Id>, numberOfPlays: Int) {
        repeat(numberOfPlays) {
            addPlay(boardGameId, opponents)
        }
    }
}
