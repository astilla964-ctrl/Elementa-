package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "discovered_compounds")
data class DiscoveredCompoundEntity(
    @PrimaryKey val id: String,
    val discoveredAt: Long = System.currentTimeMillis(),
    val timesProduced: Int = 1
)

@Entity(tableName = "reaction_logs")
data class ReactionLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val equation: String,
    val observation: String,
    val temperature: Double,
    val ph: Double,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "completed_assignments")
data class CompletedAssignmentEntity(
    @PrimaryKey val id: String,
    val completedAt: Long = System.currentTimeMillis(),
    val earnedCredits: Int,
    val unlockedToolId: String? = null
)

@Entity(tableName = "career_stats")
data class CareerStatsEntity(
    @PrimaryKey val id: Int = 1,
    val totalCredits: Int = 0,
    val completedQuestCount: Int = 0
)

