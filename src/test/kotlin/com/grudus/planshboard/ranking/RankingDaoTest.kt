package com.grudus.planshboard.ranking

import com.grudus.planshboard.AbstractDatabaseTest
import com.grudus.planshboard.plays.model.PlayResult
import com.grudus.planshboard.utils.BoardGameUtil
import com.grudus.planshboard.utils.OpponentsUtil
import com.grudus.planshboard.utils.PlayUtil
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class RankingDaoTest
@Autowired
constructor(private val rankingDao: RankingDao,
            private val opponentsUtil: OpponentsUtil,
            private val playUtil: PlayUtil,
            private val boardGameUtil: BoardGameUtil) : AbstractDatabaseTest() {

    private val userId by lazy { addUser().id!! }
    private val boardGameId by lazy { boardGameUtil.addRandomBoardGame(userId) }
    private val boardGameId2 by lazy { boardGameUtil.addRandomBoardGame(userId) }

    @Test
    fun `should get most frequent first position`() {
        val (opponentId1, opponentId2) = opponentsUtil.addOpponents(userId, 2)

        playUtil.addPlay(boardGameId, listOf(opponentId1, opponentId2), { id, playId ->
            if (id == opponentId1)
                PlayResult(playId, id, 11, 1)
            else PlayResult(playId, id, 0, 2)
        })

        (0 until 5).forEach { _ ->
            playUtil.addPlay(boardGameId, listOf(opponentId1), { id, playId ->
                PlayResult(playId, id, null, 1)
            })
        }
        (0 until 15).forEach { _ ->
            playUtil.addPlay(boardGameId2, listOf(opponentId2), { id, playId ->
                PlayResult(playId, id, null, 1)
            })}

        val ranking = rankingDao.getMostFrequentFirstPosition(userId)

        // user-opponent always exists in db
        assertEquals(3, ranking.size)
        assertEquals(opponentId2, ranking[0].opponentId)
        assertEquals(15, ranking[0].numberOfFirstPositions)
        assertEquals(opponentId1, ranking[1].opponentId)
        assertEquals(6, ranking[1].numberOfFirstPositions)
    }

    @Test
    fun `should return opponents without play`() {
        val (opponentId1, _) = opponentsUtil.addOpponents(userId, 2)
        playUtil.addPlay(boardGameId, listOf(opponentId1), { id, playId ->
            PlayResult(playId, id, null, 1)
        })

        val ranking = rankingDao.getMostFrequentFirstPosition(userId)

        // user-opponent always exists in db
        assertEquals(3, ranking.size)
        assertEquals(opponentId1, ranking[0].opponentId)
        assertEquals(1, ranking[0].numberOfFirstPositions)
        assertEquals(0, ranking[1].numberOfFirstPositions)
        assertEquals(0, ranking[2].numberOfFirstPositions)
    }

    @Test
    fun `should find opponents without winning plays`() {
        val opponentId = opponentsUtil.addOpponents(userId, 1)[0]

        playUtil.addPlay(boardGameId, listOf(opponentId), { id, playId ->
            PlayResult(playId, id, null, 33)
        })

        val ranking = rankingDao.getMostFrequentFirstPosition(userId)

        // user-opponent always exists in db
        assertEquals(2, ranking.size)
        assertEquals(0, ranking[0].numberOfFirstPositions)
        assertEquals(0, ranking[1].numberOfFirstPositions)
    }
}
