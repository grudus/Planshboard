package com.grudus.planshboard.plays.model

import com.grudus.planshboard.commons.Id
import java.time.LocalDateTime

data class Play(val id: Id, val boardGameId: Id, val date: LocalDateTime)