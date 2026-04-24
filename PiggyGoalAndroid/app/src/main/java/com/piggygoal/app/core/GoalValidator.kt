package com.piggygoal.app.core

import com.piggygoal.app.domain.model.GoalInput
import java.time.LocalDate

object GoalValidator {
    fun validate(input: GoalInput, today: LocalDate = LocalDate.now()): String? {
        return when {
            input.name.isBlank() -> "Goal name can’t be empty."
            input.targetAmount <= 0.0 -> "Target amount must be greater than zero."
            input.currentAmount < 0.0 -> "Current saved amount can’t be negative."
            input.targetDate.isAfter(today).not() -> "Target date must be in the future."
            else -> null
        }
    }
}
