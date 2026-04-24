package com.piggygoal.app.data.local

import com.piggygoal.app.data.local.entity.DepositEntity
import com.piggygoal.app.data.local.entity.GoalEntity
import com.piggygoal.app.domain.model.Deposit
import com.piggygoal.app.domain.model.Goal
import com.piggygoal.app.domain.model.GoalInput
import java.time.LocalDate
import java.time.LocalDateTime

fun GoalEntity.toDomain(): Goal = Goal(
    id = id,
    name = name,
    emoji = emoji,
    targetAmount = targetAmount,
    currentAmount = currentAmount,
    targetDate = LocalDate.parse(targetDate),
    createdAt = LocalDateTime.parse(createdAt),
    currency = currency,
)

fun DepositEntity.toDomain(): Deposit = Deposit(
    id = id,
    goalId = goalId,
    amount = amount,
    date = LocalDateTime.parse(date),
    note = note,
)

fun GoalInput.toEntity(id: Long = 0, createdAt: LocalDateTime = LocalDateTime.now()): GoalEntity = GoalEntity(
    id = id,
    name = name,
    emoji = emoji,
    targetAmount = targetAmount,
    currentAmount = currentAmount,
    targetDate = targetDate.toString(),
    createdAt = createdAt.toString(),
    currency = currency,
)
