package com.piggygoal.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.piggygoal.app.data.local.entity.DepositEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DepositDao {
    @Query("SELECT * FROM deposits WHERE goalId = :goalId ORDER BY date DESC, id DESC")
    fun observeDeposits(goalId: Long): Flow<List<DepositEntity>>

    @Insert
    suspend fun insert(deposit: DepositEntity): Long

    @Query("DELETE FROM deposits WHERE goalId = :goalId")
    suspend fun deleteByGoalId(goalId: Long)
}
