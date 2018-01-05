package com.grudus.planshboard.boardgame

import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.commons.exceptions.DuplicateEntryException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BoardGameService
@Autowired
constructor(private val boardGameDao: BoardGameDao) {

    fun findAll(userId: Id): List<BoardGameDto> =
            boardGameDao.findAll(userId)
                    .map { BoardGameDto(it) }

    fun createNew(userId: Id, name: String): Id =
            boardGameDao.create(name, userId)

    fun exists(userId: Id, name: String): Boolean =
            boardGameDao.findByName(userId, name) != null

    fun belongsToAnotherUser(userId: Id, boardGameId: Id): Boolean =
            boardGameDao.findById(boardGameId)
                    ?.let { game -> game.userId != userId } ?: false

    fun delete(boardGameId: Id) {
        boardGameDao.delete(boardGameId)
    }

    fun update(id: Id, editBoardGameRequest: EditBoardGameRequest): BoardGameDto {
        val updatedItemsCount = boardGameDao.updateName(id, editBoardGameRequest.name)
        return if (updatedItemsCount == 1)
            BoardGameDto(id, editBoardGameRequest.name)
        else throw BoardGameNotFoundException("Cannot find board game with id [$id]")
    }
}