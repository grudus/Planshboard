package com.grudus.planshboard.games

import com.grudus.planshboard.Tables.*
import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.games.opponent.Opponent
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository


@Repository
class GameDao
@Autowired
constructor(private val dsl: DSLContext) {


    fun findOpponentsForGame(gameId: Id): List<Opponent> =
            dsl.select(OPPONENTS.ID, OPPONENTS.USER_ID, OPPONENTS.NAME)
                    .from(GAME_RESULTS)
                    .join(OPPONENTS).onKey()
                    .join(GAMES).onKey()
                    .where(GAMES.ID.eq(gameId))
                    .fetchInto(Opponent::class.java)


    fun insertGameOpponents(gameId: Id, opponents: List<Id>) {
        val batchStep = dsl.batch(dsl.insertInto(GAME_RESULTS, GAME_RESULTS.GAME_ID, GAME_RESULTS.OPPONENT_ID)
                .values(null as Long?, null))

        opponents.forEach { opponentId ->
            batchStep.bind(gameId, opponentId)
        }

        batchStep.execute()
    }

    fun insertGameAlone(boardGameId: Id): Id =
            dsl.insertInto(GAMES, GAMES.BOARDGAME_ID)
                    .values(boardGameId)
                    .returning()
                    .fetchOne()
                    .id
}