package com.grudus.planshboard.boardgame

import com.grudus.planshboard.Tables.BOARDGAMES
import com.grudus.planshboard.commons.Id
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class BoardGameDao
@Autowired
constructor(private val dsl: DSLContext) {

    fun findAll(userId: Id): List<BoardGame> =
            dsl.selectFrom(BOARDGAMES)
                    .where(BOARDGAMES.USER_ID.eq(userId))
                    .fetchInto(BoardGame::class.java)

    fun create(name: String, userId: Id): Id =
            dsl.insertInto(BOARDGAMES, BOARDGAMES.NAME, BOARDGAMES.USER_ID)
                    .values(name, userId)
                    .returning()
                    .fetchOne().id

    fun findByName(userId: Id, name: String): BoardGame? =
            dsl.selectFrom(BOARDGAMES)
                    .where(BOARDGAMES.USER_ID.eq(userId).and(BOARDGAMES.NAME.eq(name)))
                    .fetchOneInto(BoardGame::class.java)

    fun findById(id: Id): BoardGame? =
            dsl.selectFrom(BOARDGAMES)
                    .where(BOARDGAMES.ID.eq(id))
                    .fetchOneInto(BoardGame::class.java)

    fun delete(id: Id) {
        dsl.deleteFrom(BOARDGAMES)
                .where(BOARDGAMES.ID.eq(id))
                .execute()
    }

    fun updateName(id: Id, name: String): Int =
            dsl.update(BOARDGAMES)
                    .set(BOARDGAMES.NAME, name)
                    .where(BOARDGAMES.ID.eq(id))
                    .execute()

}