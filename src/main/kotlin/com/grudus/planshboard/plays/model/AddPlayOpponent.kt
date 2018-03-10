package com.grudus.planshboard.plays.model

import com.grudus.planshboard.commons.Id

data class AddPlayOpponent(val name: String, val position: Int, val points: Int? = null, val id: Id? = null)