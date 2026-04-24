package com.piggygoal.app.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class Goal(
    val id: Long,
    val name: String,
    val emoji: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val targetDate: LocalDate,
    val createdAt: LocalDateTime,
    val currency: String,
)
