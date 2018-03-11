package com.grudus.planshboard.plays.model

import com.grudus.planshboard.commons.Id

data class AddPlayResult(val opponentName: String, val position: Int, val points: Int? = null, val opponentId: Id? = null)