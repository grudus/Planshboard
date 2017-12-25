package com.grudus.planshboard.boardgame

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BoardGameService
@Autowired
constructor(private val boardGameDao: BoardGameDao) {

    fun findAll(): List<BoardGameDto> =
            boardGameDao.findAll()
                    .map { BoardGameDto(it) }

}