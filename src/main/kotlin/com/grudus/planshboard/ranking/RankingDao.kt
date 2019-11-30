package com.grudus.planshboard.ranking

import com.grudus.planshboard.Tables.OPPONENTS
import com.grudus.planshboard.Tables.PLAYS_RESULTS
import com.grudus.planshboard.commons.Id
import org.jooq.DSLContext
import org.jooq.impl.DSL.`val`
import org.jooq.impl.DSL.count
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class RankingDao
@Autowired
constructor(private val dsl: DSLContext) {

    // It can be optimised to one query, but it's more readable in this way
    fun getMostFrequentFirstPosition(userId: Id): List<MostFrequentFirstPosition> {
        val allOpponents = dsl.select(OPPONENTS.ID, OPPONENTS.NAME, `val`(0))
                .from(OPPONENTS)
                .where(OPPONENTS.CREATED_BY.eq(userId))
                .fetchInto(MostFrequentFirstPosition::class.java)

        val opponentIdToWinCount: Map<Long, Int> = dsl.select(OPPONENTS.ID, count())
                .from(PLAYS_RESULTS)
                .join(OPPONENTS).on(OPPONENTS.ID.eq(PLAYS_RESULTS.OPPONENT_ID))
                .where(OPPONENTS.CREATED_BY.eq(userId).and(PLAYS_RESULTS.POSITION.eq(1)))
                .groupBy(OPPONENTS.ID)
                .fetchMap(OPPONENTS.ID, count())

        return allOpponents.map {
            val winCount = opponentIdToWinCount[it.opponentId]
            if (winCount != null)
                it.copy(numberOfFirstPositions = winCount)
            else it
        }.sortedWith(compareBy({ -it.numberOfFirstPositions }, { it.opponentId }))
    }
}
