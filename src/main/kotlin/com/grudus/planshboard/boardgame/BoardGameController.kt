package com.grudus.planshboard.boardgame

import com.grudus.planshboard.commons.ExistsResponse
import com.grudus.planshboard.commons.Id
import com.grudus.planshboard.commons.IdResponse
import com.grudus.planshboard.configuration.security.AuthenticatedUser
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/board-games")
class BoardGameController
@Autowired
constructor(private val boardGameService: BoardGameService,
            private val addBoardGameRequestValidator: AddBoardGameRequestValidator,
            private val editBoardGameRequestValidator: EditBoardGameRequestValidator) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @GetMapping
    fun findAll(authenticatedUser: AuthenticatedUser): List<BoardGameDto> =
            boardGameService.findAllSortedByName(authenticatedUser.userId)

    @GetMapping("/{id}")
    @PreAuthorize("@boardGameSecurityService.hasAccessToBoardGame(#user, #id)")
    fun findById(user: AuthenticatedUser,
                 @PathVariable id: Id): BoardGameDto =
            boardGameService.findById(id)

    @PostMapping
    @ResponseStatus(CREATED)
    fun save(authenticatedUser: AuthenticatedUser,
             @RequestBody @Valid addBoardGameRequest: AddBoardGameRequest): IdResponse {
        logger.info("User [{}] creates board game: {}", authenticatedUser.userId, addBoardGameRequest)
        return IdResponse(boardGameService.createNew(authenticatedUser.userId, addBoardGameRequest.name))
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("@boardGameSecurityService.hasAccessToBoardGame(#user, #id)")
    fun delete(user: AuthenticatedUser, @PathVariable id: Id) {
        logger.info("User [{}] deletes board game [{}]", user.userId, id)
        boardGameService.delete(id)
    }

    @PutMapping("/{id}")
    @PreAuthorize("@boardGameSecurityService.hasAccessToBoardGame(#user, #id)")
    fun edit(user: AuthenticatedUser,
             @PathVariable id: Id,
             @RequestBody @Valid editBoardGameRequest: EditBoardGameRequest): BoardGameDto {
        logger.info("User [{}] updates game [{}]: {}", user.userId, id, editBoardGameRequest)
        return boardGameService.update(id, editBoardGameRequest)
    }


    @GetMapping("/exists")
    fun existsForUser(@RequestParam("name") name: String, authenticatedUser: AuthenticatedUser): ExistsResponse =
            ExistsResponse(boardGameService.exists(authenticatedUser.userId, name))


    @InitBinder("addBoardGameRequest")
    protected fun initAddBinder(binder: WebDataBinder) {
        binder.validator = addBoardGameRequestValidator
    }

    @InitBinder("editBoardGameRequest")
    protected fun initEditBinder(binder: WebDataBinder) {
        binder.validator = editBoardGameRequestValidator
    }
}
