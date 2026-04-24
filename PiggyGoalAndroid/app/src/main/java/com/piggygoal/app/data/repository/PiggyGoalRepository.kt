package com.piggygoal.app.data.repository

import androidx.room.withTransaction
import com.piggygoal.app.data.local.AppDatabase
import com.piggygoal.app.data.local.dao.DepositDao
import com.piggygoal.app.data.local.dao.GoalDao
import com.piggygoal.app.data.local.entity.DepositEntity
import com.piggygoal.app.data.local.toDomain
import com.piggygoal.app.data.local.toEntity
import com.piggygoal.app.domain.model.Goal
import com.piggygoal.app.domain.model.GoalDetail
import com.piggygoal.app.domain.model.GoalInput
import java.time.LocalDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class PiggyGoalRepository(
    private val database: AppDatabase,
    private val goalDao: GoalDao,
    private val depositDao: DepositDao,
) {
    fun observeGoals(): Flow<List<Goal>> = goalDao.observeGoals().map { goals ->
        goals.map { it.toDomain() }
    }

    fun observeGoalDetail(goalId: Long): Flow<GoalDetail?> = combine(
        goalDao.observeGoal(goalId),
        depositDao.observeDeposits(goalId),
    ) { goal, deposits ->
        goal?.toDomain()?.let {
            GoalDetail(
                goal = it,
                deposits = deposits.map { deposit -> deposit.toDomain() },
            )
        }
    }

    suspend fun getGoal(goalId: Long): Goal? = goalDao.getGoal(goalId)?.toDomain()

    suspend fun createGoal(input: GoalInput): Long = goalDao.insert(input.toEntity())

    suspend fun updateGoal(goalId: Long, input: GoalInput) {
        val existing = goalDao.getGoal(goalId) ?: return
        goalDao.update(input.toEntity(id = goalId, createdAt = existing.createdAt.let(LocalDateTime::parse)))
    }

    suspend fun deleteGoal(goalId: Long) {
        goalDao.deleteById(goalId)
    }

    suspend fun addDeposit(goalId: Long, amount: Double, note: String?) {
        database.withTransaction {
            val goal = goalDao.getGoal(goalId) ?: return@withTransaction
            depositDao.insert(
                DepositEntity(
                    goalId = goalId,
                    amount = amount,
                    date = LocalDateTime.now().toString(),
                    note = note?.takeIf { it.isNotBlank() },
                ),
            )
            goalDao.update(goal.copy(currentAmount = goal.currentAmount + amount))
        }
    }
}
