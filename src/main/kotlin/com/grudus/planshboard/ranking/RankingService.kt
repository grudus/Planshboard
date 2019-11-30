package com.grudus.planshboard.ranking

import com.grudus.planshboard.commons.Id
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RankingService
@Autowired
constructor(private val rankingDao: RankingDao) {

    fun getMostFrequentFirstPosition(userId: Id): List<MostFrequentFirstPosition> =
            rankingDao.getMostFrequentFirstPosition(userId)
}
