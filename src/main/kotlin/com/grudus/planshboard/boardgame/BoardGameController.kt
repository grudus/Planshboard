package com.grudus.planshboard.boardgame

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/board-games")
class BoardGameController
@Autowired
constructor(private val boardGameService: BoardGameService) {

    @GetMapping
    fun findAll(): List<BoardGameDto> =
            boardGameService.findAll()
}