package com.grudus.planshboard.boardgame

import com.grudus.planshboard.commons.IdResponse
import com.grudus.planshboard.configuration.security.AuthenticatedUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*
import org.springframework.security.access.prepost.PreAuthorize
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
    @ResponseStatus(CREATED)
    fun save(authenticatedUser: AuthenticatedUser,
             @RequestBody @Valid addBoardGameRequest: AddBoardGameRequest): IdResponse =
            boardGameService.createNew(authenticatedUser.userId, addBoardGameRequest.name)
                    .let { id -> IdResponse(id) }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("@boardGameSecurityService.hasAccessToBoardGame(#user, #id)")
    fun delete(user: AuthenticatedUser, @PathVariable id: Long) {
        boardGameService.delete(id)
    }

    @GetMapping("/exists")
    fun existsForUser(@RequestParam("name") name: String, authenticatedUser: AuthenticatedUser): Map<String, Boolean> =
            boardGameService.exists(authenticatedUser.userId, name)
                    .let { exists -> mapOf("exists" to exists) }

    @InitBinder("addBoardGameRequest")
    protected fun initBinder(binder: WebDataBinder) {
        binder.validator = addBoardGameRequestValidator
    }
}