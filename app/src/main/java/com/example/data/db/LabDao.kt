package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LabDao {
    @Query("SELECT * FROM discovered_compounds")
    fun getAllDiscovered(): Flow<List<DiscoveredCompoundEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDiscovered(compound: DiscoveredCompoundEntity)

    @Query("UPDATE discovered_compounds SET timesProduced = timesProduced + 1 WHERE id = :id")
    suspend fun incrementProducedCount(id: String)

    @Query("SELECT * FROM reaction_logs ORDER BY timestamp DESC LIMIT 60")
    fun getReactionLogs(): Flow<List<ReactionLogEntity>>

    @Insert
    suspend fun insertReactionLog(log: ReactionLogEntity)

    @Query("DELETE FROM reaction_logs")
    suspend fun clearReactionLogs()

    @Query("DELETE FROM discovered_compounds WHERE id NOT IN (:preUnlockedIds)")
    suspend fun resetDiscovered(preUnlockedIds: List<String>)

    @Query("SELECT * FROM completed_assignments")
    fun getAllCompletedAssignments(): Flow<List<CompletedAssignmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletedAssignment(assignment: CompletedAssignmentEntity)

    @Query("SELECT * FROM career_stats WHERE id = 1")
    fun getCareerStats(): Flow<CareerStatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCareerStats(stats: CareerStatsEntity)

    @Query("DELETE FROM completed_assignments")
    suspend fun clearCompletedAssignments()
}
