package com.grudus.planshboard.boardgame

import com.grudus.planshboard.commons.exceptions.ResourceNotFoundException

class BoardGameNotFoundException(override val message: String = "Cannot find board game"): ResourceNotFoundException(message)