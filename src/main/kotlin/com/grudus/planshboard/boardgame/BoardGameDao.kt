package com.grudus.planshboard.boardgame

import com.grudus.planshboard.Tables.BOARDGAMES
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class BoardGameDao
@Autowired
constructor(private val dsl: DSLContext) {

    fun findAll(): List<BoardGame> =
            dsl.selectFrom(BOARDGAMES)
                    .fetchInto(BoardGame::class.java)
}