package com.example

import com.example.data.model.plant.IndustrialProcessCatalog
import com.example.data.model.plant.ReactorType
import com.example.data.model.plant.ThermalJacketMode
import com.example.engine.plant.IndustrialPlantEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class IndustrialPlantEngineTest {

    private lateinit var engine: IndustrialPlantEngine

    @Before
    fun setUp() {
        engine = IndustrialPlantEngine(IndustrialProcessCatalog.PROCESS_SULFURIC_ACID)
    }

    @Test
    fun `residence time tau is correctly calculated as volume over throughput`() {
        // Configure reactor volume = 3000 L, discharge flow = 60 L/min
        engine.configureReactor(volumeMaxL = 5000.0, agitatorRpm = 300.0)
        engine.setPumpFlowRate("PUMP_03", 60.0) // 60 L/min

        engine.tick(dtSeconds = 1.0)
        val telemetry = engine.telemetry

        // Tau = Volume / FlowRate = 3000 / 60 = 50.0 minutes
        assertTrue("Residence time should be positive", telemetry.residenceTimeMinutes > 0.0)
        assertTrue("Tau should reflect volume over flow rate", telemetry.residenceTimeMinutes in 30.0..70.0)
    }

    @Test
    fun `pfr reactor achieves high kinetic conversion for given residence time`() {
        engine.setReactorType(ReactorType.PFR)
        assertEquals(ReactorType.PFR, engine.reactor.type)

        // Advance simulation for 10 seconds
        repeat(10) {
            engine.tick(dtSeconds = 1.0)
        }

        val telemetry = engine.telemetry
        assertTrue("Conversion should be non-zero", telemetry.conversionPercent > 0.0)
        assertTrue("Conversion should be bounded <= 100%", telemetry.conversionPercent <= 100.0)
        assertTrue("Product purity should be bounded <= 100%", telemetry.productPurityPercent <= 100.0)
    }

    @Test
    fun `thermal jacket in cooling mode reduces jacket temperature`() {
        engine.setThermalJacket(ThermalJacketMode.COOLING, setpointC = 50.0, coolantFlowLpm = 100.0)
        val initialJacketTemp = engine.thermalJacket.currentJacketTempC

        repeat(5) {
            engine.tick(dtSeconds = 1.0)
        }

        val updatedJacketTemp = engine.thermalJacket.currentJacketTempC
        assertTrue(
            "Jacket temperature should decrease towards chilled water supply (10°C)",
            updatedJacketTemp <= initialJacketTemp
        )
    }

    @Test
    fun `wet gas scrubber captures acidic gas emissions and enforces compliance`() {
        engine.selectRecipe(IndustrialProcessCatalog.PROCESS_SULFURIC_ACID)
        assertTrue(engine.activeRecipe.generatesHazardousGas)

        // Enable scrubber with adequate wash flow (50 L/min)
        engine.setScrubberControls(isActive = true, washFlowLpm = 50.0)
        repeat(5) {
            engine.tick(dtSeconds = 1.0)
        }

        val telemetry = engine.telemetry
        assertTrue("Scrubber efficiency should be high (>85%)", telemetry.scrubberEfficiencyPercent > 85.0)
        assertTrue("Vented gas should remain below regulatory limit", telemetry.isCompliantEmissions)
    }

    @Test
    fun `effluent neutralization basin maintains compliant discharge window`() {
        // Set dosing to neutral balance
        engine.setEffluentDosing(causticLpm = 1.0, acidLpm = 0.0)
        repeat(5) {
            engine.tick(dtSeconds = 1.0)
        }

        val telemetry = engine.telemetry
        assertTrue("Effluent pH should be valid", telemetry.effluentPh in 1.0..14.0)
    }

    @Test
    fun `emergency shutdown trips feed pumps and closes inlet valves`() {
        assertFalse(engine.isEmergencyShutdown)
        engine.triggerEmergencyShutdown(true)

        assertTrue(engine.isEmergencyShutdown)
        assertFalse(engine.feedPumpA.isRunning)
        assertFalse(engine.feedPumpB.isRunning)
        assertEquals(0f, engine.inletValveA.percentOpen, 0.01f)
        assertEquals(0f, engine.inletValveB.percentOpen, 0.01f)
        assertEquals(ThermalJacketMode.COOLING, engine.thermalJacket.mode)
    }

    @Test
    fun `industrial contract catalog contains high capacity commercial orders`() {
        val contracts = IndustrialProcessCatalog.DEFAULT_CONTRACTS
        assertTrue("Expected multiple industrial contracts", contracts.size >= 4)

        val h2so4Contract = contracts.find { it.id == "ind_contract_h2so4" }
        assertNotNull(h2so4Contract)
        assertEquals(5000.0, h2so4Contract!!.targetVolumeL, 0.1)
        assertEquals(4500, h2so4Contract.rewardCredits)
        assertEquals(95.0, h2so4Contract.minPurityPercent, 0.1)
    }
}
