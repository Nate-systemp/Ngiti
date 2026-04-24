package com.piggygoal.app.domain.model

import java.time.LocalDate

data class GoalInput(
    val name: String,
    val emoji: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val targetDate: LocalDate,
    val currency: String,
)
