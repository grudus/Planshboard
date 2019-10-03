package com.grudus.planshboard.stats

import com.grudus.planshboard.Tables.*
import com.grudus.planshboard.commons.Id
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jooq.impl.DSL.countDistinct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class GamesStatsDao
@Autowired
constructor(private val dsl: DSLContext) {

    fun countAllGames(opponentId: Id): Int =
            dsl.select(countDistinct(BOARDGAMES.ID))
                    .from(BOARDGAMES)
                    .join(PLAYS).on(PLAYS.BOARDGAME_ID.eq(BOARDGAMES.ID))
                    .join(PLAYS_RESULTS).on(PLAYS_RESULTS.PLAY_ID.eq(PLAYS.ID))
                    .where(PLAYS_RESULTS.OPPONENT_ID.eq(opponentId))
                    .fetchOneInto(Int::class.java)
}
