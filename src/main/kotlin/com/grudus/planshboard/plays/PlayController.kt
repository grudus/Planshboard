package com.grudus.planshboard.plays

import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.commons.IdResponse
import com.grudus.planshboard.configuration.security.AuthenticatedUser
import com.grudus.planshboard.plays.model.AddPlayRequest
import com.grudus.planshboard.plays.model.PlayResponse
import com.grudus.planshboard.plays.opponent.OpponentDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/board-games/{boardGameId}/plays")
class PlayController
@Autowired
constructor(private val playService: PlayService,
            private val addPlayRequestValidator: AddPlayRequestValidator) {

    @GetMapping("/{id}/opponents")
    @PreAuthorize("@playSecurityService.hasAccessToPlay(#user, #playId)")
    fun findOpponentsForPlay(@PathVariable("id") playId: Id,
                             user: AuthenticatedUser): List<OpponentDto> =
            playService.findOpponentsForPlay(playId)
                    .map { OpponentDto(it.id!!, it.name) }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addPlay(@RequestBody @Valid addPlayRequest: AddPlayRequest,
                @PathVariable("boardGameId") boardGameId: Id,
                authenticatedUser: AuthenticatedUser): IdResponse =
            playService.savePlay(authenticatedUser.userId, boardGameId, addPlayRequest)
                    .let { id -> IdResponse(id) }


    @GetMapping("/results")
    @PreAuthorize("@boardGameSecurityService.hasAccessToBoardGame(#user, #boardGameId)")
    fun getPlayResults(@PathVariable("boardGameId") boardGameId: Id,
                       user: AuthenticatedUser): List<PlayResponse> =
            playService.getPlayResults(user.userId, boardGameId)

    @InitBinder("addPlayRequest")
    protected fun initEditBinder(binder: WebDataBinder) {
        binder.validator = addPlayRequestValidator
    }
}