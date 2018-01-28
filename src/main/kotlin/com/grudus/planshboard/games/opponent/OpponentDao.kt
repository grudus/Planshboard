package com.grudus.planshboard.games.opponent

import com.grudus.planshboard.Tables.OPPONENTS
import com.grudus.planshboard.commons.Id
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class OpponentDao
@Autowired
constructor(private val dsl: DSLContext) {


    fun addOpponent(userId: Id, name: String): Id =
            dsl.insertInto(OPPONENTS, OPPONENTS.USER_ID, OPPONENTS.NAME)
                    .values(userId, name)
                    .returning()
                    .fetchOne()
                    .id

    fun findAllOpponents(userId: Id): List<Opponent> =
            dsl.selectFrom(OPPONENTS)
                    .where(OPPONENTS.USER_ID.eq(userId))
                    .fetchInto(Opponent::class.java)

    fun findByName(userId: Id, name: String): Opponent? =
            dsl.selectFrom(OPPONENTS)
                    .where(OPPONENTS.USER_ID.eq(userId).and(OPPONENTS.NAME.eq(name)))
                    .fetchOneInto(Opponent::class.java)
}