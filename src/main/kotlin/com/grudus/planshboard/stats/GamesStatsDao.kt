package com.grudus.planshboard.stats

import com.grudus.planshboard.Tables.BOARDGAMES
import com.grudus.planshboard.commons.Id
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class GamesStatsDao
@Autowired
constructor(private val dsl: DSLContext) {

    fun countAllGames(userId: Id): Int =
            dsl.selectCount()
                    .from(BOARDGAMES)
                    .where(BOARDGAMES.USER_ID.eq(userId))
                    .fetchOneInto(Int::class.java)
}