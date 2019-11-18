package com.grudus.planshboard.plays.opponent

import com.grudus.planshboard.Tables.OPPONENTS
import com.grudus.planshboard.commons.Id
import org.jooq.Condition
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class OpponentDao
@Autowired
constructor(private val dsl: DSLContext) {


    fun addOpponent(name: String, createdBy: Id): Id =
            dsl.insertInto(OPPONENTS, OPPONENTS.NAME, OPPONENTS.CREATED_BY)
                    .values(name, createdBy)
                    .returning()
                    .fetchOne()
                    .id

    fun addOpponentPointingToUser(name: String, createdBy: Id, pointingToUser: Id): Id =
            dsl.insertInto(OPPONENTS, OPPONENTS.NAME, OPPONENTS.CREATED_BY, OPPONENTS.POINTING_TO_USER)
                    .values(name, createdBy, pointingToUser)
                    .returning()
                    .fetchOne()
                    .id

    fun findAllOpponentsCreatedBy(createdBy: Id): List<Opponent> =
            dsl.selectFrom(OPPONENTS)
                    .where(OPPONENTS.CREATED_BY.eq(createdBy))
                    .fetchInto(Opponent::class.java)

    fun findByName(name: String, createdBy: Id): Opponent? =
            selectOpponent(OPPONENTS.CREATED_BY.eq(createdBy).and(OPPONENTS.NAME.eq(name)))

    fun findOpponentPointingToCurrentUser(currentUserId: Id): Opponent =
            selectOpponent(OPPONENTS.CREATED_BY.eq(currentUserId).and(OPPONENTS.POINTING_TO_USER.eq(currentUserId)))!!

    fun findById(opponentId: Id): Opponent? =
            selectOpponent(OPPONENTS.ID.eq(opponentId))

    fun findOpponentByConnectedUser(createdBy: Id, pointingToUser: Id?): Opponent? =
            selectOpponent(OPPONENTS.CREATED_BY.eq(createdBy).and(OPPONENTS.POINTING_TO_USER.eq(pointingToUser)))


    private fun selectOpponent(condition: Condition): Opponent? =
            dsl.selectFrom(OPPONENTS)
                    .where(condition)
                    .fetchOneInto(Opponent::class.java)

}
