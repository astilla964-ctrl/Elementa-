package com.example

import com.example.data.model.AssignmentCatalog
import com.example.data.model.AssignmentRequirements
import com.example.data.model.AssignmentRewards
import com.example.data.model.LabAssignment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LabAssignmentTest {

    @Test
    fun `schema fields properly instantiate and hold contract values`() {
        val assignment = LabAssignment(
            id = "quest_custom_aspirin",
            clientName = "PharmaCorp",
            title = "Synthesize Aspirin",
            description = "High purity batch of acetylsalicylic acid",
            requirements = AssignmentRequirements(
                targetCompoundId = "C9H8O4",
                targetAmount = 25.0,
                minPurity = 90.0
            ),
            rewards = AssignmentRewards(
                credits = 850,
                unlocksToolId = "CONDENSER"
            ),
            isCompleted = false
        )

        assertEquals("quest_custom_aspirin", assignment.id)
        assertEquals("PharmaCorp", assignment.clientName)
        assertEquals("Synthesize Aspirin", assignment.title)
        assertEquals("C9H8O4", assignment.requirements.targetCompoundId)
        assertEquals(25.0, assignment.requirements.targetAmount, 0.001)
        assertEquals(90.0, assignment.requirements.minPurity, 0.001)
        assertEquals(850, assignment.rewards.credits)
        assertEquals("CONDENSER", assignment.rewards.unlocksToolId)
        assertFalse(assignment.isCompleted)
    }

    @Test
    fun `default assignment catalog contains valid quests and clients`() {
        val catalog = AssignmentCatalog.DEFAULT_ASSIGNMENTS
        assertTrue(catalog.isNotEmpty())
        assertTrue(catalog.size >= 5)

        val aspirinQuest = catalog.find { it.id == "quest_pharma_aspirin" }
        assertNotNull(aspirinQuest)
        assertEquals("PharmaCorp Synthetics", aspirinQuest?.clientName)
        assertEquals("C9H8O4", aspirinQuest?.requirements?.targetCompoundId)
        assertEquals(25.0, aspirinQuest?.requirements?.targetAmount ?: 0.0, 0.001)

        val waterQuest = catalog.find { it.id == "quest_water_neutralize" }
        assertNotNull(waterQuest)
        assertEquals("City Water Authority", waterQuest?.clientName)
        assertEquals("NaCl", waterQuest?.requirements?.targetCompoundId)
    }

    @Test
    fun `completing assignment preserves state and toggles completion flag`() {
        val original = AssignmentCatalog.DEFAULT_ASSIGNMENTS.first()
        assertFalse(original.isCompleted)

        val completed = original.copy(isCompleted = true)
        assertTrue(completed.isCompleted)
        assertEquals(original.id, completed.id)
        assertEquals(original.requirements, completed.requirements)
        assertEquals(original.rewards, completed.rewards)
    }
}
