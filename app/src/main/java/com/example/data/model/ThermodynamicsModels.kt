package com.example.data.model

/**
 * Models and enumeration types for the Environmental & Thermodynamics Simulation Engine.
 */
enum class ThermalApparatus(
    val title: String,
    val iconEmoji: String,
    val minTemp: Double,
    val maxTemp: Double,
    val defaultTargetTemp: Double,
    val isCooling: Boolean,
    val description: String
) {
    NONE(
        title = "Ambient Air (25°C)",
        iconEmoji = "🍃",
        minTemp = 25.0,
        maxTemp = 25.0,
        defaultTargetTemp = 25.0,
        isCooling = false,
        description = "Standard laboratory ambient room temperature equilibrium (25°C / 298.15 K)."
    ),
    HOT_PLATE(
        title = "Digital Hot Plate",
        iconEmoji = "♨️",
        minTemp = 25.0,
        maxTemp = 550.0,
        defaultTargetTemp = 180.0,
        isCooling = false,
        description = "Electric ceramic top plate delivering uniform, thermostatically controlled surface heating up to 550°C."
    ),
    BUNSEN_BURNER(
        title = "Bunsen Burner Flame",
        iconEmoji = "🔥",
        minTemp = 25.0,
        maxTemp = 1500.0,
        defaultTargetTemp = 450.0,
        isCooling = false,
        description = "High-output gas burner producing adjustable roaring oxidizing blue flame up to 1,500°C."
    ),
    ICE_BATH(
        title = "Ice Water Bath",
        iconEmoji = "🧊",
        minTemp = 0.0,
        maxTemp = 25.0,
        defaultTargetTemp = 0.0,
        isCooling = true,
        description = "Crushed ice and distilled water slurry establishing a 0.0°C (273.15 K) freezing equilibrium."
    ),
    DRY_ICE_BATH(
        title = "Dry Ice / Acetone Bath",
        iconEmoji = "❄️",
        minTemp = -78.5,
        maxTemp = 25.0,
        defaultTargetTemp = -78.5,
        isCooling = true,
        description = "Subliming solid carbon dioxide in acetone maintaining a stable sub-zero bath at -78.5°C (194.65 K)."
    ),
    LIQUID_NITROGEN_BATH(
        title = "Liquid Nitrogen Dewar",
        iconEmoji = "⚡🧊",
        minTemp = -196.0,
        maxTemp = 25.0,
        defaultTargetTemp = -196.0,
        isCooling = true,
        description = "Cryogenic liquefied nitrogen boiling at -196.0°C (77.15 K) for deep thermal quenching."
    )
}

enum class PressureMode(
    val title: String,
    val iconEmoji: String,
    val description: String
) {
    OPEN_ATMOSPHERE(
        title = "Open Mouth (1 atm)",
        iconEmoji = "💨",
        description = "Container opening is exposed to standard atmospheric pressure (1.0 atm / 101.3 kPa). Gases freely evolve."
    ),
    SEALED_STOPPER_GAUGE(
        title = "Stopper & Pressure Gauge",
        iconEmoji = "🔒",
        description = "Gastight rubber stopper with calibrated dial pressure gauge (0 - 100 atm). Traps all evolved gases (PV = nRT)."
    )
}

enum class ContainerHazardState(
    val title: String,
    val alertLevel: String,
    val colorHex: Long
) {
    INTACT(
        title = "Glassware Safe & Intact",
        alertLevel = "NOMINAL",
        colorHex = 0xFF10B981L // Green
    ),
    THERMAL_STRESS_WARNING(
        title = "Thermal Stress Alert",
        alertLevel = "WARNING",
        colorHex = 0xFFF59E0BL // Amber
    ),
    PRESSURE_WARNING(
        title = "High Pressure Hazard",
        alertLevel = "DANGER",
        colorHex = 0xFFEF4444L // Red
    ),
    CRACKED(
        title = "Glass Stressed & Fractured",
        alertLevel = "CRITICAL",
        colorHex = 0xFFDC2626L // Dark Red
    ),
    RUPTURED_EXPLODED(
        title = "CONTAINER EXPLOSION / RUPTURE",
        alertLevel = "CATASTROPHIC",
        colorHex = 0xFF991B1BL // Intense Red
    )
}

data class ThermodynamicReading(
    val timestampMs: Long,
    val temperatureCelsius: Double,
    val pressureAtm: Double,
    val hazardState: ContainerHazardState
)
