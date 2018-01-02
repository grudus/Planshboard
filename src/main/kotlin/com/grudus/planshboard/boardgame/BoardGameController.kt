package com.grudus.planshboard.boardgame

import com.grudus.planshboard.commons.IdResponse
import com.grudus.planshboard.configuration.security.AuthenticatedUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/board-games")
class BoardGameController
@Autowired
constructor(private val boardGameService: BoardGameService,
            private val addBoardGameRequestValidator: AddBoardGameRequestValidator) {

    @GetMapping
    fun findAll(authenticatedUser: AuthenticatedUser): List<BoardGameDto> =
            boardGameService.findAll(authenticatedUser.userId)

    @PostMapping
    fun save(authenticatedUser: AuthenticatedUser,
             @RequestBody @Valid addBoardGameRequest: AddBoardGameRequest): IdResponse =
            boardGameService.createNew(authenticatedUser.userId, addBoardGameRequest.name)
                    .let { id -> IdResponse(id) }


    @InitBinder("addBoardGameRequest")
    protected fun initBinder(binder: WebDataBinder) {
        binder.validator = addBoardGameRequestValidator
    }
}