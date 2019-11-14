package com.grudus.planshboard.stats

import com.grudus.planshboard.MockitoExtension
import com.grudus.planshboard.plays.opponent.OpponentDto
import com.grudus.planshboard.stats.models.OpponentCount
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito
import kotlin.random.Random

@ExtendWith(MockitoExtension::class)
class StatsServiceTest {

    @Mock
    private lateinit var gamesStatsDao: GamesStatsDao

    @Mock
    private lateinit var playsStatsDao: PlaysStatsDao

    private lateinit var statsService: StatsService

    @BeforeEach
    fun init() {
        statsService = StatsService(gamesStatsDao, playsStatsDao)
    }

    @Test
    fun `should generate correct stats with opponent wins`() {
        val counts = Random.nextInt()
        val opponentWinsCount = Random.nextInt()
        val opponentId = Random.nextLong()
        Mockito.`when`(gamesStatsDao.countAllGames(anyLong())).thenReturn(counts)
        Mockito.`when`(playsStatsDao.countAllPlays(anyLong())).thenReturn(counts)
        Mockito.`when`(playsStatsDao.countPlaysPerBoardGames(anyLong())).thenReturn(listOf())

        val opponentCounts = listOf(OpponentCount(opponent(opponentId), opponentWinsCount), OpponentCount(opponent(1L), counts))
        Mockito.`when`(playsStatsDao.countPlayPositionPerOpponent(anyLong(), eq(1))).thenReturn(opponentCounts)

        val stats = statsService.generateStats(Random.nextLong(), opponentId)

        assertEquals(opponentWinsCount, stats.opponentWins)
    }

    private fun opponent(opponentId: Long) = OpponentDto(opponentId, randomAlphabetic(11))
}
