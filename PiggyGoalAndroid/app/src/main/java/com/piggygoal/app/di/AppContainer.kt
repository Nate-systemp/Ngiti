package com.piggygoal.app.di

import android.content.Context
import androidx.room.Room
import com.piggygoal.app.data.local.AppDatabase
import com.piggygoal.app.data.preferences.SettingsRepository
import com.piggygoal.app.data.repository.PiggyGoalRepository

class AppContainer(context: Context) {
    private val database = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "piggygoal.db",
    ).addMigrations(*AppDatabase.MIGRATIONS_PLACEHOLDER)
        .build()

    val repository: PiggyGoalRepository = PiggyGoalRepository(
        database = database,
        goalDao = database.goalDao(),
        depositDao = database.depositDao(),
    )

    val settingsRepository: SettingsRepository = SettingsRepository(context)
}
