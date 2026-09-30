package com.example.engine.plant

import com.example.data.model.plant.CentrifugalPump
import com.example.data.model.plant.GasScrubber
import com.example.data.model.plant.IndustrialContract
import com.example.data.model.plant.IndustrialProcessCatalog
import com.example.data.model.plant.IndustrialProcessRecipe
import com.example.data.model.plant.IndustrialReactor
import com.example.data.model.plant.NeutralizationBasin
import com.example.data.model.plant.ProcessValve
import com.example.data.model.plant.ReactorType
import com.example.data.model.plant.ThermalJacket
import com.example.data.model.plant.ThermalJacketMode
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

data class PlantViolationAlert(
    val id: String,
    val timestamp: Long,
    val title: String,
    val description: String,
    val finePenalty: Int,
    val isCritical: Boolean = false
)

data class PlantTelemetry(
    val residenceTimeMinutes: Double = 0.0,
    val conversionPercent: Double = 0.0,
    val productPurityPercent: Double = 0.0,
    val instantaneousProductionRateLpm: Double = 0.0,
    val cumulativeDeliveredVolumeL: Double = 0.0,
    val reactorLevelPercent: Double = 60.0,
    val linePressureKpa: Double = 101.3,
    val reactorTempC: Double = 65.0,
    val jacketTempC: Double = 58.0,
    val scrubberEfficiencyPercent: Double = 98.5,
    val ventedGasPpm: Double = 12.0,
    val effluentPh: Double = 7.2,
    val isCompliantEmissions: Boolean = true,
    val isCompliantEffluent: Boolean = true,
    val isOverpressurized: Boolean = false,
    val isEmergencyShutdownActive: Boolean = false
)

class IndustrialPlantEngine(
    initialRecipe: IndustrialProcessRecipe = IndustrialProcessCatalog.PROCESS_SULFURIC_ACID
) {
    var activeRecipe: IndustrialProcessRecipe = initialRecipe
        private set

    var reactor: IndustrialReactor = IndustrialReactor()
        private set

    var thermalJacket: ThermalJacket = ThermalJacket()
        private set

    var feedPumpA: CentrifugalPump = CentrifugalPump("PUMP_01", "Reactant A Feed Pump", true, 40.0, 40.0, 150.0)
        private set

    var feedPumpB: CentrifugalPump = CentrifugalPump("PUMP_02", "Reactant B Feed Pump", true, 40.0, 40.0, 150.0)
        private set

    var dischargePump: CentrifugalPump = CentrifugalPump("PUMP_03", "Reactor Discharge Pump", true, 80.0, 80.0, 250.0)
        private set

    var inletValveA: ProcessValve = ProcessValve("VALVE_01", "Feed A Inlet Valve", true, 100f)
        private set

    var inletValveB: ProcessValve = ProcessValve("VALVE_02", "Feed B Inlet Valve", true, 100f)
        private set

    var dischargeValve: ProcessValve = ProcessValve("VALVE_03", "Discharge Line Valve", true, 100f)
        private set

    var gasScrubber: GasScrubber = GasScrubber()
        private set

    var neutralizationBasin: NeutralizationBasin = NeutralizationBasin()
        private set

    var isEmergencyShutdown: Boolean = false
        private set

    var autoLevelControl: Boolean = true
        private set

    var totalFinesAssessed: Int = 0
        private set

    var totalCreditsEarned: Int = 0
        private set

    val recentAlerts = mutableListOf<PlantViolationAlert>()

    var activeContract: IndustrialContract? = IndustrialProcessCatalog.DEFAULT_CONTRACTS.firstOrNull()
        private set

    var telemetry: PlantTelemetry = PlantTelemetry()
        private set

    private val gasConstantR = 8.314 // J/(mol*K)

    fun selectRecipe(recipe: IndustrialProcessRecipe) {
        activeRecipe = recipe
        // Adjust default thermal jacket setpoint
        thermalJacket = thermalJacket.copy(setpointTempC = recipe.optimalTemperatureC)
        resetPlantState()
    }

    fun selectContract(contract: IndustrialContract) {
        activeContract = contract
        val recipe = IndustrialProcessCatalog.getRecipe(contract.recipeId)
        if (recipe != null) {
            selectRecipe(recipe)
        }
    }

    fun setReactorType(type: ReactorType) {
        reactor = reactor.copy(type = type)
    }

    fun configureReactor(volumeMaxL: Double, agitatorRpm: Double) {
        val safeMax = volumeMaxL.coerceIn(100.0, 10000.0)
        val clampedCur = min(reactor.currentVolumeL, safeMax * 0.9)
        reactor = reactor.copy(
            maxVolumeL = safeMax,
            currentVolumeL = clampedCur,
            agitatorRpm = agitatorRpm.coerceIn(0.0, 800.0)
        )
    }

    fun setThermalJacket(mode: ThermalJacketMode, setpointC: Double, coolantFlowLpm: Double) {
        thermalJacket = thermalJacket.copy(
            mode = mode,
            setpointTempC = setpointC.coerceIn(-20.0, 300.0),
            coolantFlowLpm = coolantFlowLpm.coerceIn(0.0, thermalJacket.maxCoolantFlowLpm)
        )
    }

    fun setPumpFlowRate(pumpId: String, flowLpm: Double) {
        val clamped = flowLpm.coerceIn(0.0, 250.0)
        when (pumpId) {
            feedPumpA.id -> feedPumpA = feedPumpA.copy(targetFlowRateLpm = clamped)
            feedPumpB.id -> feedPumpB = feedPumpB.copy(targetFlowRateLpm = clamped)
            dischargePump.id -> dischargePump = dischargePump.copy(targetFlowRateLpm = clamped)
        }
    }

    fun togglePumpPower(pumpId: String) {
        when (pumpId) {
            feedPumpA.id -> feedPumpA = feedPumpA.copy(isRunning = !feedPumpA.isRunning)
            feedPumpB.id -> feedPumpB = feedPumpB.copy(isRunning = !feedPumpB.isRunning)
            dischargePump.id -> dischargePump = dischargePump.copy(isRunning = !dischargePump.isRunning)
        }
    }

    fun setValvePercent(valveId: String, percent: Float) {
        val clamped = percent.coerceIn(0f, 100f)
        val isOpen = clamped > 0f
        when (valveId) {
            inletValveA.id -> inletValveA = inletValveA.copy(percentOpen = clamped, isOpen = isOpen)
            inletValveB.id -> inletValveB = inletValveB.copy(percentOpen = clamped, isOpen = isOpen)
            dischargeValve.id -> dischargeValve = dischargeValve.copy(percentOpen = clamped, isOpen = isOpen)
        }
    }

    fun setScrubberControls(isActive: Boolean, washFlowLpm: Double) {
        gasScrubber = gasScrubber.copy(
            isActive = isActive,
            washFlowLpm = washFlowLpm.coerceIn(0.0, 200.0)
        )
    }

    fun setEffluentDosing(causticLpm: Double, acidLpm: Double) {
        neutralizationBasin = neutralizationBasin.copy(
            causticDosingFlowLpm = causticLpm.coerceIn(0.0, 10.0),
            acidDosingFlowLpm = acidLpm.coerceIn(0.0, 10.0)
        )
    }

    fun toggleAutoLevelControl() {
        autoLevelControl = !autoLevelControl
    }

    fun triggerEmergencyShutdown(active: Boolean) {
        isEmergencyShutdown = active
        if (active) {
            // Cut feed pumps, close inlet valves, max out cooling
            feedPumpA = feedPumpA.copy(isRunning = false)
            feedPumpB = feedPumpB.copy(isRunning = false)
            inletValveA = inletValveA.copy(percentOpen = 0f, isOpen = false)
            inletValveB = inletValveB.copy(percentOpen = 0f, isOpen = false)
            thermalJacket = thermalJacket.copy(mode = ThermalJacketMode.COOLING, coolantFlowLpm = 200.0)
            addAlert(
                title = "EMERGENCY SHUTDOWN (ESD) ACTIVATED",
                description = "Master plant safety trip initiated. Inlet feed cut off and emergency cooling engaged.",
                penalty = 0,
                isCritical = true
            )
        }
    }

    fun resetPlantState() {
        reactor = reactor.copy(
            currentVolumeL = reactor.maxVolumeL * 0.65,
            temperatureC = 60.0,
            pressureKpa = 101.3
        )
        isEmergencyShutdown = false
    }

    fun clearViolations() {
        recentAlerts.clear()
    }

    /**
     * Advances continuous industrial flow physics:
     * - Mass & volumetric balance equations (Accumulation = FlowIn - FlowOut)
     * - Residence time tau = V / Q
     * - Arrhenius reaction kinetics & conversion yield
     * - Thermal exchange jacket heat dissipation/heating
     * - Gas scrubber absorption efficiency & emissions
     * - Effluent neutralization basin pH
     * - Safety overpressurization and EPA fine checks
     */
    fun tick(dtSeconds: Double = 1.0) {
        val dtMin = dtSeconds / 60.0

        // 1. Calculate effective inlet and outlet flow rates (L/min)
        val flowA = if (feedPumpA.isRunning && !isEmergencyShutdown) {
            feedPumpA.targetFlowRateLpm * (inletValveA.percentOpen / 100f)
        } else 0.0

        val flowB = if (feedPumpB.isRunning && !isEmergencyShutdown) {
            feedPumpB.targetFlowRateLpm * (inletValveB.percentOpen / 100f)
        } else 0.0

        val totalFlowIn = flowA + flowB

        feedPumpA = feedPumpA.copy(currentFlowRateLpm = flowA)
        feedPumpB = feedPumpB.copy(currentFlowRateLpm = flowB)

        // Auto level control balances discharge pump if active
        if (autoLevelControl && !isEmergencyShutdown) {
            val targetLevel = reactor.maxVolumeL * 0.65
            val levelDiff = reactor.currentVolumeL - targetLevel
            val compensatedDischarge = (totalFlowIn + levelDiff * 0.08).coerceIn(0.0, dischargePump.maxFlowRateLpm)
            dischargePump = dischargePump.copy(targetFlowRateLpm = compensatedDischarge)
        }

        val totalFlowOut = if (dischargePump.isRunning && dischargeValve.isOpen && reactor.currentVolumeL > 5.0) {
            min(reactor.currentVolumeL / dtMin, dischargePump.targetFlowRateLpm * (dischargeValve.percentOpen / 100f))
        } else 0.0

        dischargePump = dischargePump.copy(currentFlowRateLpm = totalFlowOut)

        // 2. Mass & Volumetric Balance: dV/dt = Flow In - Flow Out
        val accumulationL = (totalFlowIn - totalFlowOut) * dtMin
        val newVolumeL = (reactor.currentVolumeL + accumulationL).coerceIn(10.0, reactor.maxVolumeL)

        // 3. Residence Time tau = V / Q (minutes)
        val effectiveThroughput = max(totalFlowOut, 1.0) // L/min
        val tauMinutes = newVolumeL / effectiveThroughput

        // 4. Kinetic Conversion Yield (Arrhenius Rate Equation)
        // k(T) = A * exp(-Ea / (R * T_K))
        val currentTempK = reactor.temperatureC + 273.15
        val eaJoules = activeRecipe.activationEnergyEa * 1000.0 // kJ/mol to J/mol
        val expFactor = exp(-eaJoules / (gasConstantR * currentTempK))
        val kRate = activeRecipe.baseRateConstantK * expFactor * 2500.0

        // Agitator mixing factor for CSTR vs Ideal Plug Flow for PFR
        val conversion = when (reactor.type) {
            ReactorType.CSTR -> {
                val mixingFactor = (reactor.agitatorRpm / 300.0).coerceIn(0.4, 1.2)
                val kEff = kRate * mixingFactor
                // CSTR First order: X = k*tau / (1 + k*tau)
                (kEff * tauMinutes) / (1.0 + kEff * tauMinutes)
            }
            ReactorType.PFR -> {
                // PFR Ideal Plug Flow: X = 1 - exp(-k*tau)
                1.0 - exp(-kRate * tauMinutes)
            }
        }.coerceIn(0.02, 0.999)

        val purityPercent = (conversion * 100.0).coerceIn(10.0, 99.8)

        // 5. Thermal Balance:
        // Heat of reaction + Jacket heat transfer + Sensible feed heating
        // Q_rxn = (-DeltaH) * rate * V
        val molesReactedPerMin = (effectiveThroughput / 18.0) * conversion
        val heatGenWatts = (-activeRecipe.heatOfReactionDeltaH * 1000.0) * (molesReactedPerMin / 60.0) // J/s = W

        // Thermal Jacket closed-loop PID / Cooling / Heating
        var jacketTemp = thermalJacket.currentJacketTempC
        when (thermalJacket.mode) {
            ThermalJacketMode.AUTO_PID -> {
                val error = reactor.temperatureC - thermalJacket.setpointTempC
                val targetCoolant = (abs(error) * 25.0).coerceIn(10.0, thermalJacket.maxCoolantFlowLpm)
                thermalJacket = thermalJacket.copy(coolantFlowLpm = targetCoolant)
                val targetJacketTemp = if (error > 0) {
                    thermalJacket.setpointTempC - 15.0 // Chill below setpoint
                } else {
                    thermalJacket.setpointTempC + 25.0 // Heat above setpoint
                }
                jacketTemp += (targetJacketTemp - jacketTemp) * 0.1 * dtSeconds
            }
            ThermalJacketMode.COOLING -> {
                val chilledWaterTemp = 10.0
                jacketTemp += (chilledWaterTemp - jacketTemp) * 0.15 * dtSeconds
            }
            ThermalJacketMode.HEATING -> {
                val steamTemp = 160.0
                jacketTemp += (steamTemp - jacketTemp) * 0.15 * dtSeconds
            }
            ThermalJacketMode.OFF -> {
                val ambient = 25.0
                jacketTemp += (ambient - jacketTemp) * 0.02 * dtSeconds
            }
        }
        thermalJacket = thermalJacket.copy(currentJacketTempC = jacketTemp)

        // Jacket heat transfer: Q_hx = U * A * (T_jacket - T_reactor)
        val areaM2 = (newVolumeL / 1000.0).pow(2.0 / 3.0) * 4.8 // Estimated wetted surface area
        val heatTransferWatts = thermalJacket.heatTransferCoeffU * areaM2 * (jacketTemp - reactor.temperatureC)

        // Heat capacity of reactor contents (approximated as water/acid mixture ~ 3800 J/(kg*K))
        val totalHeatCap = newVolumeL * 1.1 * 3800.0 // Joules per Kelvin
        val netThermalPower = heatGenWatts + heatTransferWatts
        val deltaTemp = (netThermalPower / totalHeatCap) * dtSeconds

        val newTempC = (reactor.temperatureC + deltaTemp).coerceIn(5.0, 320.0)

        // 6. Pressure Dynamics (Ideal Gas vapor pressure + hydraulic flow friction)
        // Vapor pressure rises exponentially with temperature (Antoine-like approximation)
        val vaporPressureKpa = 101.3 * exp(0.045 * (newTempC - 100.0)).coerceAtLeast(0.0)
        val hydraulicFrictionKpa = (totalFlowIn / 50.0).pow(1.5) * 8.0
        val basePressure = 101.3 + hydraulicFrictionKpa + (vaporPressureKpa * 0.4)
        val newPressureKpa = basePressure.coerceIn(95.0, 600.0)

        // Safety overpressurization check
        val isOverpressured = newPressureKpa > reactor.maxSafePressureKpa
        if (isOverpressured && !isEmergencyShutdown) {
            val fine = 500
            totalFinesAssessed += fine
            addAlert(
                title = "HIGH PRESSURE RUPTURE WARNING (${newPressureKpa.roundToInt()} kPa)",
                description = "Reactor vessel exceeded maximum safe pressure rating (350 kPa)! Emergency pressure relief lifted with chemical spill fine assessed.",
                penalty = fine,
                isCritical = true
            )
            triggerEmergencyShutdown(true)
        }

        reactor = reactor.copy(
            currentVolumeL = newVolumeL,
            temperatureC = newTempC,
            pressureKpa = newPressureKpa
        )

        // 7. Gas Scrubber Absorption & Flue Gas Compliance
        var ventedPpm = 0.0
        var scrubberEff = 0.0
        if (activeRecipe.generatesHazardousGas) {
            val rawEmissionsPpm = activeRecipe.hazardousGasPpmFactor * (conversion * 120.0)
            if (gasScrubber.isActive && gasScrubber.washFlowLpm > 5.0) {
                // Absorption efficiency increases with wash solvent flow
                scrubberEff = (1.0 - exp(-gasScrubber.washFlowLpm / 18.0)) * gasScrubber.packingEfficiency
                ventedPpm = rawEmissionsPpm * (1.0 - scrubberEff)
            } else {
                scrubberEff = 0.0
                ventedPpm = rawEmissionsPpm
            }

            // EPA emission fine check if vented exceeds threshold
            if (ventedPpm > gasScrubber.complianceThresholdPpm) {
                val fine = 250
                totalFinesAssessed += fine
                addAlert(
                    title = "EPA EMISSION VIOLATION (${ventedPpm.roundToInt()} PPM)",
                    description = "Unscrubbed ${activeRecipe.hazardousGasName} vented past chimney stack limit (50 PPM). Verify scrubber wash pump and packing.",
                    penalty = fine
                )
            }
        }
        gasScrubber = gasScrubber.copy(
            efficiencyPercent = scrubberEff * 100.0,
            ventedGasPpm = ventedPpm
        )

        // 8. Effluent Neutralization Basin Dynamics
        val effluentInflow = totalFlowOut
        val basinDischarge = if (neutralizationBasin.isDischarging) neutralizationBasin.dischargeFlowLpm else 0.0
        val basinVolChange = (effluentInflow - basinDischarge) * dtMin
        val newBasinVol = (neutralizationBasin.currentVolumeL + basinVolChange).coerceIn(100.0, neutralizationBasin.maxCapacityL)

        // pH dynamics: Incoming acid/base + Dosing pumps
        var basinPh = neutralizationBasin.currentPh
        if (activeRecipe.effluentIsAcidic && effluentInflow > 1.0) {
            // Acidic stream drops pH
            basinPh -= 0.03 * (effluentInflow / 50.0) * dtSeconds
        }
        // Caustic dosing increases pH
        if (neutralizationBasin.causticDosingFlowLpm > 0.0) {
            basinPh += 0.06 * neutralizationBasin.causticDosingFlowLpm * dtSeconds
        }
        // Acid dosing decreases pH
        if (neutralizationBasin.acidDosingFlowLpm > 0.0) {
            basinPh -= 0.06 * neutralizationBasin.acidDosingFlowLpm * dtSeconds
        }
        basinPh = basinPh.coerceIn(1.0, 14.0)

        // Water compliance fine check if discharging out of legal range (6.5 - 8.5)
        if (neutralizationBasin.isDischarging && (basinPh < neutralizationBasin.minSafePh || basinPh > neutralizationBasin.maxSafePh)) {
            val fine = 300
            totalFinesAssessed += fine
            addAlert(
                title = "WATER DISCHARGE VIOLATION (pH ${String.format("%.1f", basinPh)})",
                description = "Effluent wastewater discharged outside legal neutral band (pH 6.5 - 8.5). Increase caustic or acid dosing to balance basin.",
                penalty = fine
            )
        }

        neutralizationBasin = neutralizationBasin.copy(
            currentVolumeL = newBasinVol,
            currentPh = basinPh
        )

        // 9. Contract delivery tracking
        val deliveredIncrement = totalFlowOut * (dtSeconds / 60.0)
        activeContract?.let { contract ->
            if (!contract.isCompleted && totalFlowOut > 0.1) {
                val newDelivered = min(contract.targetVolumeL, contract.deliveredVolumeL + deliveredIncrement)
                val updatedAvgPurity = if (contract.deliveredVolumeL > 0) {
                    (contract.currentAveragePurity * 0.95) + (purityPercent * 0.05)
                } else purityPercent

                val isNowComplete = newDelivered >= contract.targetVolumeL
                if (isNowComplete) {
                    totalCreditsEarned += contract.rewardCredits + contract.environmentalBonus
                    addAlert(
                        title = "INDUSTRIAL CONTRACT FULFILLED!",
                        description = "Successfully synthesized and delivered ${contract.targetVolumeL.roundToInt()} L of ${contract.targetProduct} for ${contract.clientName}. Payout: +${contract.rewardCredits + contract.environmentalBonus} Credits.",
                        penalty = 0
                    )
                }

                activeContract = contract.copy(
                    deliveredVolumeL = newDelivered,
                    currentAveragePurity = updatedAvgPurity,
                    isCompleted = isNowComplete
                )
            }
        }

        // Update telemetry
        telemetry = PlantTelemetry(
            residenceTimeMinutes = tauMinutes,
            conversionPercent = conversion * 100.0,
            productPurityPercent = purityPercent,
            instantaneousProductionRateLpm = totalFlowOut * conversion,
            cumulativeDeliveredVolumeL = activeContract?.deliveredVolumeL ?: 0.0,
            reactorLevelPercent = (newVolumeL / reactor.maxVolumeL) * 100.0,
            linePressureKpa = newPressureKpa,
            reactorTempC = newTempC,
            jacketTempC = jacketTemp,
            scrubberEfficiencyPercent = scrubberEff * 100.0,
            ventedGasPpm = ventedPpm,
            effluentPh = basinPh,
            isCompliantEmissions = ventedPpm <= gasScrubber.complianceThresholdPpm,
            isCompliantEffluent = basinPh in neutralizationBasin.minSafePh..neutralizationBasin.maxSafePh,
            isOverpressurized = isOverpressured,
            isEmergencyShutdownActive = isEmergencyShutdown
        )
    }

    private fun addAlert(title: String, description: String, penalty: Int, isCritical: Boolean = false) {
        val alert = PlantViolationAlert(
            id = "alert_${System.currentTimeMillis()}_${(0..999).random()}",
            timestamp = System.currentTimeMillis(),
            title = title,
            description = description,
            finePenalty = penalty,
            isCritical = isCritical
        )
        recentAlerts.add(0, alert)
        if (recentAlerts.size > 20) {
            recentAlerts.removeAt(recentAlerts.lastIndex)
        }
    }
}
