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
import kotlin.random.Random.Default.nextInt

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

        playUtil.addPlays(gameId, addNewOpponentsAndUser(), numberOfPlays)

        val playsCount = dao.countAllPlays(opponentsUtil.asOpponent(userId))

        assertEquals(numberOfPlays, playsCount)
    }


    @Test
    fun `should count plays from all board games`() {
        val gameId = boardGameUtil.addRandomBoardGame(userId)
        val gameId2 = boardGameUtil.addRandomBoardGame(userId)
        val numberOfPlays = 5

        playUtil.addPlays(gameId, addNewOpponentsAndUser(), numberOfPlays)
        playUtil.addPlays(gameId2, addNewOpponentsAndUser(), numberOfPlays)

        val playsCount = dao.countAllPlays(opponentsUtil.asOpponent(userId))

        assertEquals(numberOfPlays * 2, playsCount)
    }

    @Test
    fun `should count plays only from one user`() {
        val gameId = boardGameUtil.addRandomBoardGame(userId)
        val gameId2 = boardGameUtil.addRandomBoardGame(addUser().id!!)
        val numberOfPlays = 5

        playUtil.addPlays(gameId, addNewOpponentsAndUser(numberOfPlays), numberOfPlays)
        playUtil.addPlays(gameId2, addNewOpponentsWithoutTheUser(numberOfPlays), numberOfPlays)

        val playsCount = dao.countAllPlays(opponentsUtil.asOpponent(userId))

        assertEquals(numberOfPlays, playsCount)
    }


    @Test
    fun `should count opponent wins`() {
        val gameId = boardGameUtil.addRandomBoardGame(userId)
        val opponents = addNewOpponentsAndUser(3)
        val userAsOpponent = opponents[2]

        val playsResults: List<Map<Id, (Id, Id) -> PlayResult>> = listOf(
                mapOf(opponents[0] to position(3),
                        opponents[1] to position(2),
                        userAsOpponent to position(1)),
                mapOf(opponents[0] to position(2),
                        opponents[1] to position(1),
                        userAsOpponent to position(2)),
                mapOf(opponents[0] to position(2),
                        opponents[1] to position(3),
                        userAsOpponent to position(1))
        )

        playsResults.forEach {
            playUtil.addPlay(gameId, opponents, { id, playId -> it.getValue(id)(playId, id) })
        }

        val opponentWins1 = dao.findOpponentWins(userAsOpponent)
        val opponentWins2 = dao.findOpponentWins(opponents[1])

        assertEquals(2, opponentWins1)
        assertEquals(1, opponentWins2)
    }


    @Test
    fun `should count all plays per board games`() {
        val gamesCount = 3
        val opponents = addNewOpponentsAndUser()
        val games: List<Id> = boardGameUtil.addRandomBoardGames(userId, gamesCount)
        val numbersOfPlays = listOf(3, 5, 2)

        repeat(gamesCount) { i ->
            playUtil.addPlays(games[i], opponents, numbersOfPlays[i])
        }

        val numberOfPlaysPerGame = dao.countPlaysPerBoardGames(opponentsUtil.asOpponent(userId))

        val count = { id: Id -> numberOfPlaysPerGame.find { it.boardGame.id == id }?.count }

        assertEquals(gamesCount, numberOfPlaysPerGame.size)

        repeat(gamesCount) { i ->
            assertEquals(numbersOfPlays[i], count(games[i]))
        }
    }

    @Test
    fun `should only count plays per board games of one user`() {
        val gameId = boardGameUtil.addRandomBoardGame(userId)
        val gameId2 = boardGameUtil.addRandomBoardGame(addUser().id!!)

        val playCount1 = 4
        val playCount2 = 7

        playUtil.addPlays(gameId, addNewOpponentsAndUser(), playCount1)
        playUtil.addPlays(gameId2, addNewOpponentsWithoutTheUser(), playCount2)

        val numberOfPlaysPerGame = dao.countPlaysPerBoardGames(opponentsUtil.asOpponent(userId))

        assertEquals(1, numberOfPlaysPerGame.size)
        assertEquals(playCount1, numberOfPlaysPerGame[0].count)
    }

    @Test
    fun `should count plays per board game and return game id`() {
        val gamesCount = 3
        val sortedGameIds = boardGameUtil.addRandomBoardGames(userId, gamesCount)
                .sorted()

        repeat(gamesCount) { i ->
            playUtil.addPlays(sortedGameIds[i], addNewOpponentsAndUser(), i + 1)
        }

        val sortedNumberOfPlaysPerGame = dao.countPlaysPerBoardGames(opponentsUtil.asOpponent(userId))
                .sortedBy { it.boardGame.id }

        repeat(gamesCount) { i ->
            assertEquals(sortedGameIds[i], sortedNumberOfPlaysPerGame[i].boardGame.id)
        }
    }

    @Test
    fun `should count all plays only for specific opponent`() {
        val boardGame = boardGameUtil.addRandomBoardGame(userId)
        val opponents = opponentsUtil.addOpponents(userId, 3)

        playUtil.addPlays(boardGame, opponents.subList(0, 1), 5)
        playUtil.addPlays(boardGame, opponents, 2)

        val plays1 = dao.countAllPlays(opponents[0])
        val plays2 = dao.countAllPlays(opponents[1])

        assertEquals(7, plays1)
        assertEquals(2, plays2)
    }

    @Test
    fun `should find opponent wins per board game`() {
        val boardGame = boardGameUtil.addRandomBoardGame(userId)
        val boardGame2 = boardGameUtil.addRandomBoardGame(userId)
        val (opponent1, opponent2) = opponentsUtil.addOpponents(userId, 2)

        playUtil.addPlay(boardGame, listOf(opponent1, opponent2), { opId, playId ->
            if (opId == opponent1)
                PlayResult(playId, opId, 11, 1)
            else PlayResult(playId, opId, 1, 2)
        })
        playUtil.addPlay(boardGame, listOf(opponent1, opponent2), { opId, playId ->
            if (opId == opponent2)
                PlayResult(playId, opId, 11, 1)
            else PlayResult(playId, opId, 1, 2)
        })
        addWinsFor(boardGame2, opponent1, 5)


        val wins: Map<Long, Int> = dao.findOpponentWinsPerBoardGame(opponent1)
                .associate { it.boardGame.id!! to it.count }

        assertEquals(1, wins[boardGame])
        assertEquals(5, wins[boardGame2])
    }

    @Test
    fun `should find opponent wins per borad game and sort it by count desc`() {
        val boardGame = boardGameUtil.addRandomBoardGame(userId)
        val boardGame2 = boardGameUtil.addRandomBoardGame(userId)
        val boardGame3 = boardGameUtil.addRandomBoardGame(userId)
        val opponentId = opponentsUtil.addOpponents(userId, 1)[0]

        addWinsFor(boardGame, opponentId, 3)
        addWinsFor(boardGame2, opponentId, 17)
        addWinsFor(boardGame3, opponentId, 8)

        val wins = dao.findOpponentWinsPerBoardGame(opponentId)

        assertEquals(17, wins[0].count)
        assertEquals(boardGame2, wins[0].boardGame.id)
        assertEquals(8, wins[1].count)
        assertEquals(boardGame3, wins[1].boardGame.id)
        assertEquals(3, wins[2].count)
        assertEquals(boardGame, wins[2].boardGame.id)
    }


    private fun addWinsFor(boardGameId: Id, opponentId: Id, count: Int = 1) {
        (0 until count).forEach { _ ->
            playUtil.addPlay(boardGameId, listOf(opponentId), { opId, playId ->
                PlayResult(playId, opId, nextInt(), 1)
            })
        }
    }

    private fun position(position: Int): (Id, Id) -> PlayResult =
            { playId, id -> PlayResult(playId, id, null, position) }

    private fun addNewOpponentsAndUser(count: Int = 5, userId: Id = this.userId) =
            opponentsUtil.addOpponents(userId, count - 1) + opponentsUtil.asOpponent(userId)

    private fun addNewOpponentsWithoutTheUser(count: Int = 5) =
            opponentsUtil.addOpponents(userId, count)
}
