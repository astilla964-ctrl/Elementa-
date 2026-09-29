package com.example

import com.example.data.model.ChemicalCatalog
import com.example.data.model.LabToolType
import com.example.engine.ChemistryEngine
import com.example.engine.model.ParticlePhase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ChemistryEngineTest {

    private lateinit var engine: ChemistryEngine

    @Before
    fun setup() {
        engine = ChemistryEngine(initialTool = LabToolType.BEAKER)
    }

    @Test
    fun `spawnElement creates 2D particle with correct properties`() {
        val hydrogen = ChemicalCatalog.getChemical("H")
        assertNotNull(hydrogen)

        val particle = engine.spawnElement(hydrogen!!)
        assertNotNull(particle.instanceId)
        assertEquals(hydrogen, particle.chemical)
        assertEquals(ParticlePhase.GAS, particle.phase)
        assertTrue(particle.mass > 0.0)

        val state = engine.engineState.value
        assertEquals(1, state.particles.size)
    }

    @Test
    fun `step updates particle positions within 2D container bounds`() {
        val water = ChemicalCatalog.getChemical("H2O")!!
        val p = engine.spawnElement(water, x = 0.5f, y = 0.6f)
        val initialX = p.x
        val initialY = p.y

        engine.step(0.016f)

        // Particle has moved due to thermal Brownian motion or gravity
        assertTrue(p.x != initialX || p.y != initialY)
        // Particle remains within container bounds
        assertTrue(p.x in 0.14f..0.86f)
        assertTrue(p.y in 0.35f..0.89f)
    }

    @Test
    fun `potential interaction is detected when reactants are present`() {
        engine.clear()
        val h = ChemicalCatalog.getChemical("H")!!
        val o = ChemicalCatalog.getChemical("O")!!

        engine.spawnElement(h)
        engine.spawnElement(o)

        val interactions = engine.detectPotentialInteractions()
        assertTrue("Expected potential water combustion interaction", interactions.isNotEmpty())
        val combustion = interactions.find { it.reaction.id == "H2_O2_COMBUSTION" }
        assertNotNull(combustion)
        assertEquals(2, combustion!!.participatingParticles.size)

        // At 25°C, activation energy (80°C) is not yet met
        assertFalse(combustion.isActivationEnergyMet)

        // Heating above 80°C satisfies activation energy
        engine.updateEnvironment(
            tool = LabToolType.BEAKER,
            temperature = 100.0,
            heating = true,
            electricity = false,
            centrifuging = false
        )

        val updatedInteractions = engine.detectPotentialInteractions()
        val updatedCombustion = updatedInteractions.find { it.reaction.id == "H2_O2_COMBUSTION" }
        assertNotNull(updatedCombustion)
        assertTrue(updatedCombustion!!.isActivationEnergyMet)
    }

    @Test
    fun `executeBestInteraction converts reactants into products`() {
        engine.clear()
        val h = ChemicalCatalog.getChemical("H")!!
        val o = ChemicalCatalog.getChemical("O")!!
        engine.spawnElement(h)
        engine.spawnElement(o)

        engine.updateEnvironment(
            tool = LabToolType.BEAKER,
            temperature = 100.0,
            heating = true,
            electricity = false,
            centrifuging = false
        )

        val executed = engine.executeBestInteraction()
        assertNotNull(executed)

        val state = engine.engineState.value
        // Water product spawned
        assertTrue(state.particles.any { it.chemical.id == "H2O" })
        // Exothermic heat released
        assertTrue(state.averageTemperature > 100.0)
    }
}
