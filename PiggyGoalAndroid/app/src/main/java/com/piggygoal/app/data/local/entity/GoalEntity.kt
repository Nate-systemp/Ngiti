package com.piggygoal.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val emoji: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val targetDate: String,
    val createdAt: String,
    val currency: String,
)
