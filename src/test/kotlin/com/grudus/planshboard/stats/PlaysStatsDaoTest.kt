package com.grudus.planshboard.stats

import com.grudus.planshboard.AbstractDatabaseTest
import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.plays.model.PlayResult
import com.grudus.planshboard.utils.BoardGameUtil
import com.grudus.planshboard.utils.OpponentsUtil
import com.grudus.planshboard.utils.PlayUtil
import org.apache.commons.lang3.RandomStringUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class PlaysStatsDaoTest
@Autowired
constructor(private val dao: PlaysStatsDao,
            private val boardGameUtil: BoardGameUtil,
            private val opponentsUtil: OpponentsUtil,
            private val playUtil: PlayUtil) : AbstractDatabaseTest() {

    private val userId: Id by lazy { addUser(RandomStringUtils.randomAlphabetic(11)).id!! }

    @Test
    fun `should count all plays`() {
        val gameId = boardGameUtil.addRandomBoardGame(userId)
        val numberOfPlays = 5

        playUtil.addPlays(gameId, addOpponents(), numberOfPlays)

        val playsCount = dao.countAllPlays(userId)

        assertEquals(numberOfPlays, playsCount)
    }


    @Test
    fun `should count plays from all board games`() {
        val gameId = boardGameUtil.addRandomBoardGame(userId)
        val gameId2 = boardGameUtil.addRandomBoardGame(userId)
        val numberOfPlays = 5

        playUtil.addPlays(gameId, addOpponents(), numberOfPlays)
        playUtil.addPlays(gameId2, addOpponents(), numberOfPlays)

        val playsCount = dao.countAllPlays(userId)

        assertEquals(numberOfPlays * 2, playsCount)
    }

    @Test
    fun `should count plays only from one user`() {
        val gameId = boardGameUtil.addRandomBoardGame(userId)
        val gameId2 = boardGameUtil.addRandomBoardGame(addUser().id!!)
        val numberOfPlays = 5

        playUtil.addPlays(gameId, addOpponents(), numberOfPlays)
        playUtil.addPlays(gameId2, addOpponents(), numberOfPlays)

        val playsCount = dao.countAllPlays(userId)

        assertEquals(numberOfPlays, playsCount)
    }


    @Test
    fun `should count won games and group it by opponent`() {
        val gameId = boardGameUtil.addRandomBoardGame(userId)
        val opponents = addOpponents(3)

        val playsResults: List<Map<Id, (Id, Id) -> PlayResult>> = listOf(
                mapOf(opponents[0] to position(1),
                        opponents[1] to position(2),
                        opponents[2] to position(3)),
                mapOf(opponents[0] to position(2),
                        opponents[1] to position(1),
                        opponents[2] to position(2)),
                mapOf(opponents[0] to position(1),
                        opponents[1] to position(3),
                        opponents[2] to position(2))
        )

        playsResults.forEach{
            playUtil.addPlay(gameId, opponents) { id, playId -> it[id]!!(playId, id) }
        }

        val firstPositionsPerOpponent = dao.countPlayPositionPerOpponent(userId)

        val count = {id: Id -> firstPositionsPerOpponent.find { it.opponent.id == id }?.count }

        assertEquals(2, firstPositionsPerOpponent.size)
        assertEquals(2, count(opponents[0]))
        assertEquals(1, count(opponents[1]))
    }


    @Test
    fun `should count all plays per board games`() {
        val gamesCount = 3
        val opponents = addOpponents()
        val games: List<Id> = boardGameUtil.addRandomBoardGames(userId, gamesCount)
        val numbersOfPlays = listOf(3, 5, 2)

        repeat(gamesCount) { i ->
            playUtil.addPlays(games[i], opponents, numbersOfPlays[i])
        }

        val numberOfPlaysPerGame = dao.countPlaysPerBoardGames(userId)

        val count = {id: Id -> numberOfPlaysPerGame.find { it.boardGame.id == id }?.count }

        assertEquals(gamesCount, numberOfPlaysPerGame.size)

        repeat(gamesCount) {i ->
            assertEquals(numbersOfPlays[i], count(games[i]))
        }
    }

    @Test
    fun `should only count plays per board games of one user`() {
        val gameId = boardGameUtil.addRandomBoardGame(userId)
        val gameId2 = boardGameUtil.addRandomBoardGame(addUser().id!!)

        val playCount1 = 4
        val playCount2 = 7

        playUtil.addPlays(gameId, addOpponents(), playCount1)
        playUtil.addPlays(gameId2, addOpponents(), playCount2)

        val numberOfPlaysPerGame = dao.countPlaysPerBoardGames(userId)

        assertEquals(1, numberOfPlaysPerGame.size)
        assertEquals(playCount1, numberOfPlaysPerGame[0].count)
    }

    @Test
    fun `should count plays per board game and return game id`() {
        val gamesCount = 3
        val sortedGameIds = boardGameUtil.addRandomBoardGames(userId, gamesCount)
                .sorted()

        repeat(gamesCount) {i ->
            playUtil.addPlays(sortedGameIds[i], addOpponents(), i+1)
        }

        val sortedNumberOfPlaysPerGame = dao.countPlaysPerBoardGames(userId)
                .sortedBy { it.boardGame.id }

        repeat(gamesCount) {i ->
            assertEquals(sortedGameIds[i], sortedNumberOfPlaysPerGame[i].boardGame.id)
        }
    }


    private fun position(position: Int): (Id, Id) -> PlayResult =
            { playId, id -> PlayResult(playId, id, null, position) }

    private fun addOpponents(count: Int = 5) =
            opponentsUtil.addOpponents(userId, count)
}