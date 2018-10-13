package com.grudus.planshboard.boardgame

import com.grudus.planshboard.commons.Id
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BoardGameService
@Autowired
constructor(private val boardGameDao: BoardGameDao) {

    fun findAllSortedByName(userId: Id): List<BoardGameDto> =
            boardGameDao.findAllSortedByName(userId)
                    .map { BoardGameDto(it) }

    fun createNew(userId: Id, name: String): Id =
            boardGameDao.create(name, userId)

    fun exists(userId: Id, name: String): Boolean =
            boardGameDao.findByName(userId, name) != null

    fun belongsToAnotherUser(userId: Id, boardGameId: Id): Boolean =
            boardGameDao.findById(boardGameId)
                    ?.let { game -> game.userId != userId } ?: false

    fun existsForUser(userId: Id, boardGameId: Id): Boolean =
            boardGameDao.findById(boardGameId)
                    ?.let { boardGame -> boardGame.userId == userId } ?: false

    fun delete(boardGameId: Id) {
        boardGameDao.delete(boardGameId)
    }

    fun update(id: Id, editBoardGameRequest: EditBoardGameRequest): BoardGameDto {
        val updatedItemsCount = boardGameDao.updateName(id, editBoardGameRequest.name)
        return if (updatedItemsCount == 1)
            BoardGameDto(id, editBoardGameRequest.name)
        else throw BoardGameNotFoundException("Cannot find board game with id [$id]")
    }

    fun findById(id: Id): BoardGameDto =
            boardGameDao.findById(id)
                    ?.let { BoardGameDto(it) }
                    ?: throw BoardGameNotFoundException("Cannot find board game with id [$id]")

}