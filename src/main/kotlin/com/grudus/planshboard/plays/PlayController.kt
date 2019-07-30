package com.grudus.planshboard.plays

import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.commons.IdResponse
import com.grudus.planshboard.configuration.security.AuthenticatedUser
import com.grudus.planshboard.plays.model.SavePlayRequest
import com.grudus.planshboard.plays.model.PlayResponse
import com.grudus.planshboard.plays.opponent.OpponentDto
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus.*
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/board-games/{boardGameId}/plays")
class PlayController
@Autowired
constructor(private val playService: PlayService,
            private val savePlayRequestValidator: SavePlayRequestValidator) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @GetMapping("/{id}/opponents")
    @PreAuthorize("@playSecurityService.hasAccessToPlay(#user, #playId)")
    fun findOpponentsForPlay(@PathVariable("id") playId: Id,
                             user: AuthenticatedUser): List<OpponentDto> =
            playService.findOpponentsForPlay(playId)
                    .map { OpponentDto(it.id!!, it.name) }


    @PostMapping
    @ResponseStatus(CREATED)
    fun addPlay(@RequestBody @Valid savePlayRequest: SavePlayRequest,
                @PathVariable("boardGameId") boardGameId: Id,
                authenticatedUser: AuthenticatedUser): IdResponse {
        logger.info("User {} adds new play: {}", authenticatedUser.userId, savePlayRequest)
        return IdResponse(playService.savePlay(authenticatedUser.userId, boardGameId, savePlayRequest))
    }


    @GetMapping("/results")
    @PreAuthorize("@boardGameSecurityService.hasAccessToBoardGame(#user, #boardGameId)")
    fun getPlayResults(@PathVariable("boardGameId") boardGameId: Id,
                       user: AuthenticatedUser): List<PlayResponse> =
            playService.getPlayResults(user.userId, boardGameId)

    @DeleteMapping("/{id}")
    @PreAuthorize("@playSecurityService.hasAccessToPlay(#user, #playId)")
    @ResponseStatus(NO_CONTENT)
    fun deletePlay(@PathVariable("id") playId: Id,
                   user: AuthenticatedUser) {
        logger.info("User {} deletes play {}", user.userId, playId)
        playService.delete(playId)
    }

    @PutMapping("/{id}")
    @PreAuthorize("@playSecurityService.hasAccessToPlay(#user, #playId)")
    fun updatePlay(@PathVariable("id") playId: Id,
                   @RequestBody @Valid savePlayRequest: SavePlayRequest,
                   user: AuthenticatedUser): PlayResponse {
        logger.info("User {} updates play {}: {}", user.userId, playId, savePlayRequest)
        return playService.updatePlay(user.userId, playId, savePlayRequest)
    }

    @InitBinder("savePlayRequest")
    protected fun initEditBinder(binder: WebDataBinder) {
        binder.validator = savePlayRequestValidator
    }
}
