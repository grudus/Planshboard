package com.grudus.planshboard.boardgame

data class BoardGameDto(val id: Long?, val name: String) {
    constructor(boardGame: BoardGame): this(boardGame.id, boardGame.name)
}