package com.grudus.planshboard.boardgame

class BoardGameNotFoundException(override val message: String = "Cannot find board game"): RuntimeException(message)