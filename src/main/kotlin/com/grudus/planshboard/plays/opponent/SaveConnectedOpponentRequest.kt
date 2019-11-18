package com.grudus.planshboard.plays.opponent

import com.grudus.planshboard.commons.Id

data class SaveConnectedOpponentRequest(val opponentName: String,
                                        val connectedUserName: String? = null,
                                        val existingOpponentId: Id? = null)
