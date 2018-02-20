package com.grudus.planshboard.games

import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.commons.IdResponse
import com.grudus.planshboard.configuration.security.AuthenticatedUser
import com.grudus.planshboard.games.opponent.OpponentDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/games")
class GameController
@Autowired
constructor(private val gameService: GameService,
            private val addGameRequestValidator: AddGameRequestValidator) {

    @GetMapping("/{id}/opponents")
    @PreAuthorize("@gameSecurityService.hasAccessToGame(#user, #gameId)")
    fun findOpponentsForGame(@PathVariable("id") gameId: Id,
                             user: AuthenticatedUser): List<OpponentDto> =
            gameService.findOpponentsForGame(gameId)
                    .map { OpponentDto(it.id!!, it.name) }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addGame(@RequestBody @Valid addGameRequest: AddGameRequest,
                authenticatedUser: AuthenticatedUser): IdResponse =
            gameService.saveGame(addGameRequest.boardGameId, addGameRequest.opponents)
                    .let { id -> IdResponse(id) }



    @InitBinder("addGameRequest")
    protected fun initEditBinder(binder: WebDataBinder) {
        binder.validator = addGameRequestValidator
    }
}