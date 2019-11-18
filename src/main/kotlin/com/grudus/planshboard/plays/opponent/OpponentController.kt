package com.grudus.planshboard.plays.opponent

import com.grudus.planshboard.commons.IdResponse
import com.grudus.planshboard.configuration.security.AuthenticatedUser
import com.grudus.planshboard.user.UserService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/opponents")
class OpponentController
@Autowired
constructor(private val opponentService: OpponentService,
            private val addOpponentValidator: AddOpponentValidator,
            private val userService: UserService,
            private val saveConnectedOpponentValidator: SaveConnectedOpponentValidator) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addOpponent(@RequestBody @Valid addOpponentRequest: AddOpponentRequest,
                    authenticatedUser: AuthenticatedUser): IdResponse {
        logger.info("User [{}] adds new opponent: {}", authenticatedUser.userId, addOpponentRequest)
        return IdResponse(opponentService.addOpponent(authenticatedUser.userId, addOpponentRequest))
    }

    @GetMapping
    fun getAllOpponents(authenticatedUser: AuthenticatedUser): List<OpponentDto> =
            opponentService.findAll(authenticatedUser.userId)

    @PostMapping(params = ["withUser"])
    @ResponseStatus(HttpStatus.CREATED)
    fun createOpponent(@RequestBody @Valid saveConnectedOpponentRequest: SaveConnectedOpponentRequest,
                       authenticatedUser: AuthenticatedUser): IdResponse {
        logger.info("Creating new opponent with connected user: {}", saveConnectedOpponentRequest)
        val pointingToUser = saveConnectedOpponentRequest.connectedUserName?.let { userService.findByUsername(it) } ?. id
        return IdResponse(opponentService.addOpponent(authenticatedUser.userId, saveConnectedOpponentRequest.opponentName, pointingToUser))
    }

    @InitBinder("addOpponentRequest")
    protected fun initEditBinder(binder: WebDataBinder) {
        binder.validator = addOpponentValidator
    }

    @InitBinder("saveConnectedOpponentRequest")
    protected fun initSaveBinder(binder: WebDataBinder) {
        binder.validator = saveConnectedOpponentValidator
    }
}
