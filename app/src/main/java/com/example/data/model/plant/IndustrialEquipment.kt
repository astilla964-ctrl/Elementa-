package com.example.data.model.plant

/**
 * Industrial Equipment & Unit Operations Model for Chemical Works.
 */

enum class ReactorType(val displayName: String, val description: String) {
    CSTR(
        "CSTR (Stirred Tank)",
        "Continuously Stirred Tank Reactor: perfectly mixed liquid volume with continuous impeller agitation."
    ),
    PFR(
        "PFR (Plug Flow)",
        "Plug Flow Reactor: cylindrical continuous tubular reactor with high conversion along axial flow."
    )
}

enum class ThermalJacketMode(val displayName: String) {
    OFF("Passive (Ambient)"),
    COOLING("Active Chilled Water"),
    HEATING("High-Pressure Steam"),
    AUTO_PID("Automated Closed-Loop PID")
}

data class IndustrialReactor(
    val id: String = "REACT_01",
    val name: String = "Primary Synthesis Reactor",
    val type: ReactorType = ReactorType.CSTR,
    val maxVolumeL: Double = 5000.0,
    val currentVolumeL: Double = 3200.0,
    val temperatureC: Double = 65.0,
    val pressureKpa: Double = 101.3,
    val maxSafePressureKpa: Double = 350.0,
    val agitatorRpm: Double = 250.0, // for CSTR
    val tubeDiameterMm: Double = 150.0, // for PFR
    val tubeLengthM: Double = 12.0 // for PFR
)

data class ThermalJacket(
    val mode: ThermalJacketMode = ThermalJacketMode.AUTO_PID,
    val setpointTempC: Double = 60.0,
    val currentJacketTempC: Double = 58.0,
    val coolantFlowLpm: Double = 80.0, // Liters per minute
    val maxCoolantFlowLpm: Double = 250.0,
    val heatTransferCoeffU: Double = 450.0 // W/(m^2*K)
)

data class CentrifugalPump(
    val id: String,
    val name: String,
    val isRunning: Boolean = true,
    val targetFlowRateLpm: Double = 50.0, // L/min
    val currentFlowRateLpm: Double = 50.0,
    val maxFlowRateLpm: Double = 200.0,
    val rpm: Double = 1750.0
)

data class ProcessValve(
    val id: String,
    val name: String,
    val isOpen: Boolean = true,
    val percentOpen: Float = 100f // 0f to 100f
)

data class GasScrubber(
    val id: String = "SCRUB_01",
    val name: String = "Counter-Current Wet Absorption Scrubber",
    val isActive: Boolean = true,
    val washSolvent: String = "Dilute NaOH Solution (1.5M)",
    val washFlowLpm: Double = 45.0,
    val packingEfficiency: Double = 0.985, // 98.5%
    val efficiencyPercent: Double = 98.5,
    val inletGasPpm: Double = 1200.0,
    val ventedGasPpm: Double = 18.0,
    val complianceThresholdPpm: Double = 50.0, // Regulatory limit
    val targetedGasType: String = "Acidic Flue Gases (SO2, HCl, Cl2)"
)

data class NeutralizationBasin(
    val id: String = "BASIN_01",
    val name: String = "Effluent Treatment & Neutralization Basin",
    val currentVolumeL: Double = 4200.0,
    val maxCapacityL: Double = 12000.0,
    val currentPh: Double = 7.2,
    val minSafePh: Double = 6.5,
    val maxSafePh: Double = 8.5,
    val heavyMetalsPrecipitated: Boolean = true,
    val acidDosingFlowLpm: Double = 0.0,
    val causticDosingFlowLpm: Double = 0.5,
    val isDischarging: Boolean = true,
    val dischargeFlowLpm: Double = 50.0
)

data class IndustrialProcessRecipe(
    val id: String,
    val name: String,
    val equation: String,
    val reactantAId: String,
    val reactantBId: String,
    val primaryProductId: String,
    val optimalTemperatureC: Double,
    val optimalResidenceTimeMinutes: Double,
    val baseRateConstantK: Double, // min^-1
    val activationEnergyEa: Double, // kJ/mol
    val heatOfReactionDeltaH: Double, // kJ/mol: negative = exothermic, positive = endothermic
    val generatesHazardousGas: Boolean = false,
    val hazardousGasName: String? = null,
    val hazardousGasPpmFactor: Double = 0.0,
    val effluentIsAcidic: Boolean = false
)
