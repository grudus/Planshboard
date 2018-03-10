package com.grudus.planshboard.plays

import com.grudus.planshboard.commons.Id

class AddPlayRequest(val boardGameId: Id, val opponents: List<AddPlayOpponent>)
