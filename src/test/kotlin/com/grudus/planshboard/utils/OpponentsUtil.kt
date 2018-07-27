package com.grudus.planshboard.utils

import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.plays.opponent.OpponentService
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class OpponentsUtil
@Autowired
constructor(private val opponentService: OpponentService){

    fun addOpponents(userId: Id, count: Int = 5): List<Id> =
            (0 until count).map { RandomStringUtils.randomAlphabetic(11 + it) }
                    .map { name -> opponentService.addOpponent(userId, name) }

}