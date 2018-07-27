package com.grudus.planshboard.utils

import com.grudus.planshboard.boardgame.BoardGameService
import com.grudus.planshboard.commons.Id
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class BoardGameUtil
@Autowired
constructor(private val boardGameService: BoardGameService) {

    fun addRandomBoardGame(userId: Id, name: String = RandomStringUtils.randomAlphabetic(11)): Id =
            boardGameService.createNew(userId, name)

    fun addRandomBoardGames(userId: Id, numberOfGames: Int) {
        repeat(numberOfGames) {
            addRandomBoardGame(userId)
        }
    }
}