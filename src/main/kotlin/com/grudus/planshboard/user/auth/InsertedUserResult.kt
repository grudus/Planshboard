package com.grudus.planshboard.user.auth

import com.grudus.planshboard.commons.Id
import java.time.LocalDateTime

data class InsertedUserResult(val id: Id, val registerDate: LocalDateTime)