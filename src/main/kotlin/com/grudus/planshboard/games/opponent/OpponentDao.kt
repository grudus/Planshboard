package com.grudus.planshboard.games.opponent

import com.grudus.planshboard.Tables.OPPONENTS
import com.grudus.planshboard.commons.Id
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class OpponentDao
@Autowired
constructor(private val dsl: DSLContext) {


    fun addOpponent(userId: Id, name: String, isRealUser: Boolean = false): Id =
            dsl.insertInto(OPPONENTS, OPPONENTS.USER_ID, OPPONENTS.NAME, OPPONENTS.IS_REAL_USER)
                    .values(userId, name, isRealUser)
                    .returning()
                    .fetchOne()
                    .id

    fun findByName(userId: Id, name: String): Opponent? =
            dsl.selectFrom(OPPONENTS)
                    .where(OPPONENTS.USER_ID.eq(userId).and(OPPONENTS.NAME.eq(name)))
                    .fetchOneInto(Opponent::class.java)

    fun findAllOpponentsWithoutReal(userId: Id): List<Opponent> =
            findAllOpponents(userId, OPPONENTS.IS_REAL_USER.isFalse)

    fun findAllOpponentsWithReal(userId: Id): List<Opponent> =
            findAllOpponents(userId, DSL.trueCondition())

    private fun findAllOpponents(userId: Id, condition: Condition): List<Opponent> =
            dsl.selectFrom(OPPONENTS)
                    .where(OPPONENTS.USER_ID.eq(userId)
                            .and(condition))
                    .fetchInto(Opponent::class.java)
}