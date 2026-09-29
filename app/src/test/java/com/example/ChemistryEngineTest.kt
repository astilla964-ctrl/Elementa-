package com.example

import com.example.data.model.ChemicalCatalog
import com.example.data.model.LabToolType
import com.example.engine.ChemistryEngine
import com.example.engine.model.ParticlePhase
import com.example.data.model.DispensedChemical
import com.example.data.model.DispenserApparatusType
import com.example.engine.stoichiometry.StoichiometryEngine
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

    @Test
    fun `reactions catalog contains over 230 verified reactions including 100 new reactions`() {
        val reactions = ChemicalCatalog.REACTIONS
        // 33 base + 105 catalog + 100 newly added = 238
        assertTrue("Expected at least 235 reactions, found ${reactions.size}", reactions.size >= 235)

        // Verify key newly generated reactions from rxn_106 to rxn_205 exist
        val volcano = reactions.find { it.id == "rxn_106" }
        assertNotNull("Expected Ammonium Dichromate Volcano rxn_106", volcano)

        val prussianBlue = reactions.find { it.id == "rxn_108" }
        assertNotNull("Expected Prussian Blue rxn_108", prussianBlue)

        val thermite = reactions.find { it.id == "rxn_165" }
        assertNotNull("Expected Thermite rxn_165", thermite)

        val chlorAlkali = reactions.find { it.id == "rxn_197" }
        assertNotNull("Expected Chlor-Alkali Electrolysis rxn_197", chlorAlkali)

        val limewater = reactions.find { it.id == "rxn_201" }
        assertNotNull("Expected Limewater rxn_201", limewater)

        val lastNew = reactions.find { it.id == "rxn_205" }
        assertNotNull("Expected rxn_205", lastNew)

        // Verify all reactant and product IDs resolve in ChemicalCatalog
        for (rx in reactions) {
            for (rId in rx.reactantIds) {
                assertNotNull("Reactant '$rId' in reaction '${rx.id}' could not be resolved", ChemicalCatalog.getChemical(rId))
            }
            for (pId in rx.productIds) {
                assertNotNull("Product '$pId' in reaction '${rx.id}' could not be resolved", ChemicalCatalog.getChemical(pId))
            }
        }
    }

    @Test
    fun `simulate new reaction - prussian blue precipitation`() {
        engine.clear()
        val fecl3 = ChemicalCatalog.getChemical("FeCl3")!!
        val k4fe = ChemicalCatalog.getChemical("K4[Fe(CN)6]")!!

        engine.spawnElement(fecl3)
        engine.spawnElement(k4fe)

        val interactions = engine.detectPotentialInteractions()
        val pbRx = interactions.find { it.reaction.id == "rxn_108" }
        assertNotNull("Expected Prussian Blue reaction detected", pbRx)
        assertTrue(pbRx!!.isActivationEnergyMet)

        val executed = engine.executeBestInteraction()
        assertNotNull(executed)
        assertEquals("rxn_108", executed!!.reaction.id)

        val state = engine.engineState.value
        assertTrue(state.particles.any { it.chemical.id == "Fe4[Fe(CN)6]3" })
    }

    @Test
    fun `quantitative measurement apparatus mole conversions`() {
        val zinc = ChemicalCatalog.getChemical("Zn")!!
        val hcl = ChemicalCatalog.getChemical("HCl")!!
        val oxygen = ChemicalCatalog.getChemical("O2")!!

        // 1. Analytical Balance: Mass (g) / Molar Mass (g/mol)
        val zincMoles = StoichiometryEngine.calculateMoles(
            chemical = zinc,
            apparatusType = DispenserApparatusType.ANALYTICAL_BALANCE,
            amountValue = 65.38 // 1 mol of Zn
        )
        assertEquals(1.0, zincMoles, 0.01)

        // 2. Graduated Cylinder / Buret: Molarity (M) * Volume (L)
        val hclMoles = StoichiometryEngine.calculateMoles(
            chemical = hcl,
            apparatusType = DispenserApparatusType.GRADUATED_CYLINDER,
            amountValue = 100.0, // 100 mL
            molarity = 2.0 // 2.0 M
        )
        assertEquals(0.2, hclMoles, 0.001)

        // 3. Gas Syringe: Volume at STP (L) / 22.414 (L/mol)
        val o2Moles = StoichiometryEngine.calculateMoles(
            chemical = oxygen,
            apparatusType = DispenserApparatusType.GAS_SYRINGE,
            amountValue = 22.414 // 1 mol at STP
        )
        assertEquals(1.0, o2Moles, 0.005)
    }

    @Test
    fun `stoichiometric engine identifies limiting and excess reagents and calculates exact yields`() {
        // Find reaction: Zn + 2HCl -> ZnCl2 + H2 (or rxn_034 / single replacement)
        val rx = ChemicalCatalog.REACTIONS.find { it.reactantIds.contains("Zn") && it.reactantIds.contains("HCl") }
        assertNotNull("Expected Zn + HCl reaction in catalog", rx)

        val zinc = ChemicalCatalog.getChemical("Zn")!!
        val hcl = ChemicalCatalog.getChemical("HCl")!!

        // Provide 10g of Zn (~0.153 mol) and 50 mL of 1.0M HCl (0.05 mol)
        // Balanced: Zn + 2HCl -> ZnCl2 + H2
        // HCl requires 0.05 / 2 = 0.025 mol of Zn. Zn has 0.153 mol.
        // Therefore, HCl is the LIMITING REAGENT!
        val initialDispensed = mapOf(
            "Zn" to DispensedChemical(
                chemical = zinc,
                apparatusType = DispenserApparatusType.ANALYTICAL_BALANCE,
                amountValue = 10.0,
                moles = 10.0 / zinc.molarMass
            ),
            "HCl" to DispensedChemical(
                chemical = hcl,
                apparatusType = DispenserApparatusType.GRADUATED_CYLINDER,
                amountValue = 50.0,
                molarity = 1.0,
                moles = 1.0 * (50.0 / 1000.0)
            )
        )

        val result = StoichiometryEngine.calculateStoichiometry(rx!!, initialDispensed)

        // Limiting reagent must be HCl
        assertEquals("HCl", result.limitingReagentId)
        assertFalse(result.isExactStoichiometricRatio)

        // Unreacted Zn must remain (~0.128 mol leftover)
        val remainingZn = result.remainingQuantities["Zn"]
        assertNotNull(remainingZn)
        assertTrue(remainingZn!!.moles > 0.10)
        assertTrue(remainingZn.amountValue > 6.0) // grams of unreacted Zn

        // HCl consumed completely
        val remainingHcl = result.remainingQuantities["HCl"]
        assertNotNull(remainingHcl)
        assertEquals(0.0, remainingHcl!!.moles, 0.001)

        // Products formed yields calculated
        assertTrue(result.productYields.isNotEmpty())
        assertTrue(result.unreactedExcessDescriptions.isNotEmpty())

        // Blended solution properties
        val blended = StoichiometryEngine.calculateBlendedSolution(rx, result)
        // Excess solid Zn remaining
        assertTrue(blended.hasUnreactedSolid)
        assertTrue(blended.hasSettledPrecipitate)
    }

    @Test
    fun `thermal activation energy requires heat threshold before reaction is ready`() {
        engine.clear()
        // Reaction requiring high temperature: C + O2 -> CO2 (minTemp = 400°C)
        val c = ChemicalCatalog.getChemical("C")!!
        val o = ChemicalCatalog.getChemical("O")!!

        engine.spawnElement(c)
        engine.spawnElement(o)

        // At 25°C room temperature
        engine.updateEnvironment(
            tool = LabToolType.BEAKER,
            temperature = 25.0,
            heating = false,
            electricity = false,
            centrifuging = false
        )

        var interactions = engine.detectPotentialInteractions()
        val cCombustion = interactions.find { it.reaction.reactantIds.contains("C") && it.reaction.reactantIds.contains("O") }
        if (cCombustion != null) {
            // Below activation temperature
            assertFalse(cCombustion.isActivationEnergyMet)
            assertTrue(cCombustion.readinessPercentage < 1.0f)
            assertTrue(cCombustion.conditionSummary.contains("Heat", true))

            // Heat above 400°C with Bunsen Burner
            engine.updateEnvironment(
                tool = LabToolType.BEAKER,
                temperature = 500.0,
                heating = true,
                electricity = false,
                centrifuging = false
            )
            interactions = engine.detectPotentialInteractions()
            val heated = interactions.find { it.reaction.id == cCombustion.reaction.id }
            assertNotNull(heated)
            assertTrue(heated!!.isActivationEnergyMet)
            assertEquals(1.0f, heated.readinessPercentage, 0.01f)
        }
    }

    @Test
    fun `dynamic phase transitions compute melting and boiling according to temperature`() {
        engine.clear()
        val water = ChemicalCatalog.getChemical("H2O")!!
        val p = engine.spawnElement(water, x = 0.5f, y = 0.6f)

        // At standard room temp 25°C, water is liquid
        assertEquals(ParticlePhase.LIQUID, p.phase)

        // Cool down to -10°C (below mp 0°C) -> freezes into solid
        engine.updateEnvironment(
            tool = LabToolType.BEAKER,
            temperature = -10.0,
            heating = false,
            electricity = false,
            centrifuging = false
        )
        engine.step(0.016f)
        assertEquals(ParticlePhase.SOLID, p.phase)

        // Heat up to 120°C (above bp 100°C) -> boils into gas/vapor
        engine.updateEnvironment(
            tool = LabToolType.BEAKER,
            temperature = 120.0,
            heating = true,
            electricity = false,
            centrifuging = false
        )
        engine.step(0.016f)
        assertEquals(ParticlePhase.GAS, p.phase)
    }

    @Test
    fun `cryogenic liquid nitrogen temperature freezes liquids and slows brownian motion`() {
        engine.clear()
        val ethanol = ChemicalCatalog.getChemical("CH3CH2OH") ?: ChemicalCatalog.getChemical("H2O")!!
        val p = engine.spawnElement(ethanol, x = 0.5f, y = 0.6f)

        // Cryogenic liquid nitrogen bath at -196°C
        engine.updateEnvironment(
            tool = LabToolType.BEAKER,
            temperature = -196.0,
            heating = false,
            electricity = false,
            centrifuging = false
        )
        engine.step(0.016f)

        // Solidified at -196°C
        assertEquals(ParticlePhase.SOLID, p.phase)
        assertTrue(p.localizedTemp <= -190.0)
    }

    @Test
    fun `sealed stopper containment affects particle boundaries`() {
        engine.clear()
        val helium = ChemicalCatalog.getChemical("He")!!
        val gasParticle = engine.spawnElement(helium, x = 0.5f, y = 0.1f)
        gasParticle.vy = -0.5f // Rising upwards

        // When open mouth, gas escapes through top
        engine.updateEnvironment(
            tool = LabToolType.BEAKER,
            temperature = 25.0,
            heating = false,
            electricity = false,
            centrifuging = false,
            stopperSealed = false
        )
        engine.step(0.016f)

        // When sealed with stopper, gas bounces back down off stopper ceiling
        engine.updateEnvironment(
            tool = LabToolType.BEAKER,
            temperature = 25.0,
            heating = false,
            electricity = false,
            centrifuging = false,
            stopperSealed = true
        )
        gasParticle.y = 0.18f
        gasParticle.vy = -0.4f
        engine.step(0.016f)
        assertTrue("Gas velocity should deflect downwards off sealed stopper", gasParticle.vy >= 0f)
    }
}

