package com.grudus.planshboard.plays

import com.grudus.planshboard.Tables.*
import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.plays.model.*
import com.grudus.planshboard.plays.opponent.model.Opponent
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.time.LocalDateTime.now


@Repository
class PlayDao
@Autowired
constructor(private val dsl: DSLContext) {

    fun findOpponentsForPlay(playId: Id): List<Opponent> =
            dsl.select(OPPONENTS.ID, OPPONENTS.NAME, OPPONENTS.CREATED_BY)
                    .from(PLAYS_RESULTS)
                    .join(OPPONENTS).onKey()
                    .join(PLAYS).onKey()
                    .where(PLAYS.ID.eq(playId))
                    .fetchInto(Opponent::class.java)


    fun savePlayResults(playResults: List<PlayResult>) {
        val sql = dsl.insertInto(PLAYS_RESULTS, PLAYS_RESULTS.PLAY_ID, PLAYS_RESULTS.OPPONENT_ID, PLAYS_RESULTS.POINTS, PLAYS_RESULTS.POSITION)
                .values(null as Long?, null, null, null)
        val batchStep = dsl.batch(sql)

        playResults.forEach { result ->
            batchStep.bind(result.playId, result.opponentId, result.points, result.position)
        }

        batchStep.execute()
    }

    fun removeAllPlayResults(playId: Id) {
        dsl.deleteFrom(PLAYS_RESULTS)
                .where(PLAYS_RESULTS.PLAY_ID.eq(playId))
                .execute()
    }

    fun insertPlayAlone(boardGameId: Id, date: LocalDateTime? = now(), note: String? = null): Id =
            dsl.insertInto(PLAYS, PLAYS.BOARDGAME_ID, PLAYS.DATE, PLAYS.NOTE)
                    .values(boardGameId, date, note)
                    .returning()
                    .fetchOne()
                    .id


    fun updatePlayAlone(playId: Id, date: LocalDateTime?, note: String?) {
        dsl.update(PLAYS)
                .set(PLAYS.DATE, date)
                .set(PLAYS.NOTE, note)
                .where(PLAYS.ID.eq(playId))
                .execute()
    }

    fun findById(playId: Id): Play? =
            dsl.selectFrom(PLAYS)
                    .where(PLAYS.ID.eq(playId))
                    .fetchOneInto(Play::class.java)


    fun findPlaysForBoardGame(boardGameId: Id): List<PlayResponse> =
            dsl.select(
                    PLAYS.ID,
                    PLAYS.NOTE,
                    PLAYS.DATE,
                    PLAYS_RESULTS.OPPONENT_ID,
                    PLAYS_RESULTS.POINTS,
                    PLAYS_RESULTS.POSITION,
                    OPPONENTS.NAME
            ).from(PLAYS)
                    .join(PLAYS_RESULTS).on(PLAYS_RESULTS.PLAY_ID.eq(PLAYS.ID))
                    .join(OPPONENTS).on(OPPONENTS.ID.eq(PLAYS_RESULTS.OPPONENT_ID))
                    .where(PLAYS.BOARDGAME_ID.eq(boardGameId))
                    .orderBy(PLAYS.DATE.desc(), PLAYS_RESULTS.POSITION, OPPONENTS.NAME)
                    .fetchGroups({ record ->
                        Triple(record[PLAYS.ID], record[PLAYS.NOTE], record[PLAYS.DATE])
                    }, { record ->
                        PlayOpponentsResponse(
                                record[PLAYS_RESULTS.OPPONENT_ID],
                                record[OPPONENTS.NAME],
                                record[PLAYS_RESULTS.POSITION],
                                record[PLAYS_RESULTS.POINTS]
                        )
                    }).map { (triple, opponents) ->
                        val (id, note, date) = triple
                        PlayResponse(id, date, opponents, note)
                    }


    fun findPlayResultsForPlays(playsIds: List<Id>): List<PlayOpponentsDto> =
            dsl.select(PLAYS_RESULTS.PLAY_ID, OPPONENTS.ID, OPPONENTS.NAME, PLAYS_RESULTS.POSITION, PLAYS_RESULTS.POINTS)
                    .from(PLAYS_RESULTS).innerJoin(OPPONENTS).on(OPPONENTS.ID.eq(PLAYS_RESULTS.OPPONENT_ID))
                    .where(PLAYS_RESULTS.PLAY_ID.`in`(playsIds))
                    .fetchInto(PlayOpponentsDto::class.java)

    fun delete(playId: Id) {
        dsl.deleteFrom(PLAYS)
                .where(PLAYS.ID.eq(playId))
                .execute()
    }
}
