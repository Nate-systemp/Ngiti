package com.piggygoal.app.domain.model

import java.time.LocalDateTime

data class Deposit(
    val id: Long,
    val goalId: Long,
    val amount: Double,
    val date: LocalDateTime,
    val note: String?,
)
