package com.grudus.planshboard.plays.model

import java.time.LocalDateTime
import java.time.LocalDateTime.now

class AddPlayRequest(val results: List<AddPlayResult>, val date: LocalDateTime = now())
