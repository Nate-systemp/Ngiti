package com.piggygoal.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.piggygoal.app.data.local.dao.DepositDao
import com.piggygoal.app.data.local.dao.GoalDao
import com.piggygoal.app.data.local.entity.DepositEntity
import com.piggygoal.app.data.local.entity.GoalEntity

@Database(
    entities = [GoalEntity::class, DepositEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun goalDao(): GoalDao
    abstract fun depositDao(): DepositDao

    companion object {
        val MIGRATIONS_PLACEHOLDER: Array<Migration> = emptyArray()
    }
}
