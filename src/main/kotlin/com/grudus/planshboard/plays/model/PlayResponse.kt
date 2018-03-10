package com.grudus.planshboard.plays.model

import com.grudus.planshboard.commons.Id
import java.time.LocalDateTime

class PlayResponse(val id: Id, val date: LocalDateTime, val results: List<PlayOpponentsResponse>)