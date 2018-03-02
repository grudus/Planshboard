package com.grudus.planshboard.plays

import com.grudus.planshboard.Tables.*
import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.plays.opponent.Opponent
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository


@Repository
class PlayDao
@Autowired
constructor(private val dsl: DSLContext) {


    fun findOpponentsForPlay(playId: Id): List<Opponent> =
            dsl.select(OPPONENTS.ID, OPPONENTS.USER_ID, OPPONENTS.NAME)
                    .from(PLAYS_RESULTS)
                    .join(OPPONENTS).onKey()
                    .join(PLAYS).onKey()
                    .where(PLAYS.ID.eq(playId))
                    .fetchInto(Opponent::class.java)


    fun insertPlayOpponents(playId: Id, opponents: List<Id>) {
        val batchStep = dsl.batch(dsl.insertInto(PLAYS_RESULTS, PLAYS_RESULTS.PLAY_ID, PLAYS_RESULTS.OPPONENT_ID)
                .values(null as Long?, null))

        opponents.forEach { opponentId ->
            batchStep.bind(playId, opponentId)
        }

        batchStep.execute()
    }

    fun insertPlayAlone(boardGameId: Id): Id =
            dsl.insertInto(PLAYS, PLAYS.BOARDGAME_ID)
                    .values(boardGameId)
                    .returning()
                    .fetchOne()
                    .id

    fun findById(playId: Id): Play? =
            dsl.selectFrom(PLAYS)
                    .where(PLAYS.ID.eq(playId))
                    .fetchOneInto(Play::class.java)
}