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
