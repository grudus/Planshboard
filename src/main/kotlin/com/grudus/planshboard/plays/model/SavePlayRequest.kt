package com.grudus.planshboard.plays.model

import java.time.LocalDateTime
import java.time.LocalDateTime.now

data class SavePlayRequest(val results: List<AddPlayResult>,
                           val date: LocalDateTime? = now(),
                           val note: String? = null)
