package com.grudus.planshboard.stats

import com.grudus.planshboard.Tables.*
import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.plays.opponent.OpponentDto
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.Record3
import org.jooq.impl.DSL
import org.jooq.impl.DSL.count
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class PlaysStatsDao
@Autowired
constructor(private val dsl: DSLContext) {

    fun countAllPlays(userId: Id): Int =
            dsl.selectCount()
                    .from(PLAYS)
                    .join(BOARDGAMES).onKey()
                    .where(BOARDGAMES.USER_ID.eq(userId))
                    .fetchOneInto(Int::class.java)

    fun countPlayPositionPerOpponent(userId: Id, position: Int = 1): List<OpponentCount> =
            dsl.select(OPPONENTS.ID, OPPONENTS.NAME, count())
                    .from(PLAYS_RESULTS)
                    .join(OPPONENTS).on(OPPONENTS.ID.eq(PLAYS_RESULTS.OPPONENT_ID))
                    .join(PLAYS).on(PLAYS.ID.eq(PLAYS_RESULTS.PLAY_ID))
                    .join(BOARDGAMES).on(BOARDGAMES.ID.eq(PLAYS.BOARDGAME_ID))
                    .where(OPPONENTS.USER_ID.eq(userId)).and(PLAYS_RESULTS.POSITION.eq(position))
                    .groupBy(OPPONENTS.ID)
                    .fetch { record ->
                        val opponentId = record.field1()[record]
                        val opponentName = record.field2()[record]
                        val count = record.field3()[record]
                        OpponentCount(OpponentDto(opponentId, opponentName), count)
                    }
}