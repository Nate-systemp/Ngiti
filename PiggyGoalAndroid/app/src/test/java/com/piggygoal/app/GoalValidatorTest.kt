package com.piggygoal.app

import com.piggygoal.app.core.GoalValidator
import com.piggygoal.app.domain.model.GoalInput
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GoalValidatorTest {
    @Test
    fun rejectsBlankName() {
        val result = GoalValidator.validate(
            input = GoalInput(
                name = "",
                emoji = "🐷",
                targetAmount = 1000.0,
                currentAmount = 100.0,
                targetDate = LocalDate.of(2030, 1, 1),
                currency = "₱",
            ),
            today = LocalDate.of(2026, 4, 25),
        )

        assertEquals("Goal name can’t be empty.", result)
    }

    @Test
    fun acceptsValidInput() {
        val result = GoalValidator.validate(
            input = GoalInput(
                name = "New Phone",
                emoji = "📱",
                targetAmount = 30000.0,
                currentAmount = 5000.0,
                targetDate = LocalDate.of(2026, 5, 30),
                currency = "₱",
            ),
            today = LocalDate.of(2026, 4, 25),
        )

        assertNull(result)
    }
}
