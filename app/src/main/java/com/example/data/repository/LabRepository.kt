package com.example.data.repository

import com.example.data.db.CareerStatsEntity
import com.example.data.db.CompletedAssignmentEntity
import com.example.data.db.DiscoveredCompoundEntity
import com.example.data.db.LabDao
import com.example.data.db.ReactionLogEntity
import com.example.data.model.AssignmentCatalog
import com.example.data.model.ChemicalCatalog
import com.example.data.model.Compound
import com.example.data.model.LabAssignment
import com.example.data.model.Reaction
import com.example.data.model.toCompound
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class LabRepository(private val labDao: LabDao) {

    val discoveredEntities: Flow<List<DiscoveredCompoundEntity>> = labDao.getAllDiscovered()
    val reactionLogs: Flow<List<ReactionLogEntity>> = labDao.getReactionLogs()
    val completedAssignments: Flow<List<CompletedAssignmentEntity>> = labDao.getAllCompletedAssignments()
    val careerStats: Flow<CareerStatsEntity?> = labDao.getCareerStats()

    val assignments: Flow<List<LabAssignment>> = completedAssignments.map { completedList ->
        val completedIds = completedList.map { it.id }.toSet()
        AssignmentCatalog.DEFAULT_ASSIGNMENTS.map { assignment ->
            assignment.copy(isCompleted = assignment.id in completedIds)
        }
    }

    val compounds: Flow<List<Compound>> = discoveredEntities.map { entities ->
        val discoveredIds = entities.map { it.id }.toSet()
        ChemicalCatalog.ALL_CHEMICALS.map { chem ->
            chem.toCompound(discovered = chem.id in discoveredIds || chem.isPreUnlocked)
        }
    }

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    suspend fun seedInitialCompounds() = withContext(Dispatchers.IO) {
        val preUnlocked = ChemicalCatalog.ALL_CHEMICALS.filter { it.isPreUnlocked }
        for (chem in preUnlocked) {
            labDao.insertDiscovered(
                DiscoveredCompoundEntity(
                    id = chem.id,
                    discoveredAt = System.currentTimeMillis(),
                    timesProduced = 1
                )
            )
        }
    }

    suspend fun recordReaction(
        reaction: Reaction,
        currentTemp: Double,
        currentPh: Double,
        alreadyDiscoveredIds: Set<String>
    ): List<String> = withContext(Dispatchers.IO) {
        val newlyDiscovered = mutableListOf<String>()

        // Insert log
        labDao.insertReactionLog(
            ReactionLogEntity(
                equation = reaction.equation,
                observation = reaction.observation,
                temperature = currentTemp,
                ph = currentPh,
                timestamp = System.currentTimeMillis()
            )
        )

        // Process products
        for (productId in reaction.productIds) {
            if (productId !in alreadyDiscoveredIds) {
                newlyDiscovered.add(productId)
            }
            labDao.insertDiscovered(
                DiscoveredCompoundEntity(
                    id = productId,
                    discoveredAt = System.currentTimeMillis(),
                    timesProduced = 1
                )
            )
            labDao.incrementProducedCount(productId)
        }

        newlyDiscovered
    }

    suspend fun resetDiscoveries() = withContext(Dispatchers.IO) {
        val preUnlockedIds = ChemicalCatalog.ALL_CHEMICALS.filter { it.isPreUnlocked }.map { it.id }
        labDao.resetDiscovered(preUnlockedIds)
        seedInitialCompounds()
    }

    suspend fun clearLogs() = withContext(Dispatchers.IO) {
        labDao.clearReactionLogs()
    }

    suspend fun completeAssignment(assignment: LabAssignment) = withContext(Dispatchers.IO) {
        labDao.insertCompletedAssignment(
            CompletedAssignmentEntity(
                id = assignment.id,
                completedAt = System.currentTimeMillis(),
                earnedCredits = assignment.rewards.credits,
                unlockedToolId = assignment.rewards.unlocksToolId
            )
        )
        val currentStats = labDao.getCareerStats().firstOrNull() ?: CareerStatsEntity()
        labDao.updateCareerStats(
            currentStats.copy(
                totalCredits = currentStats.totalCredits + assignment.rewards.credits,
                completedQuestCount = currentStats.completedQuestCount + 1
            )
        )
    }

    suspend fun resetAssignments() = withContext(Dispatchers.IO) {
        labDao.clearCompletedAssignments()
        labDao.updateCareerStats(CareerStatsEntity(id = 1, totalCredits = 0, completedQuestCount = 0))
    }

    suspend fun checkLatestRelease(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("https://api.github.com/repos/astillasoftwares/elementa/releases/latest")
                .header("User-Agent", "Elementa-Android-App")
                .build()
            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body?.string() ?: ""
                val json = JSONObject(body)
                val tagName = json.optString("tag_name", "v1.0.0")
                Result.success(tagName)
            } else {
                // If repository is private or 404, we are on current stable release
                Result.success("v1.0.0 (Latest)")
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
