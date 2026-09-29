package com.example.data.model

/**
 * Quest & Career Mode ("Lab Assignments") Data Schema.
 * Defines laboratory contracts, client requests, stoichiometric requirements,
 * and completion rewards driving the player progression system.
 */
data class LabAssignment(
    val id: String,
    val clientName: String,          // e.g., "PharmaCorp", "City Water Authority"
    val title: String,               // e.g., "Synthesize Aspirin", "Neutralize Acid"
    val description: String,
    val requirements: AssignmentRequirements,
    val rewards: AssignmentRewards,
    val isCompleted: Boolean = false
)

data class AssignmentRequirements(
    val targetCompoundId: String,  // The requested chemical product
    val targetAmount: Double,      // Required mass (g) or volume (mL)
    val minPurity: Double         // Percentage (e.g., 90%)
)

data class AssignmentRewards(
    val credits: Int,              // Currency earned upon completion
    val unlocksToolId: String? = null // Specific equipment unlocked (optional)
)

object AssignmentCatalog {
    val DEFAULT_ASSIGNMENTS: List<LabAssignment> = listOf(
        LabAssignment(
            id = "quest_pharma_aspirin",
            clientName = "PharmaCorp Synthetics",
            title = "Synthesize Aspirin",
            description = "PharmaCorp urgently requests a clinical trial batch of pure Acetylsalicylic Acid (Aspirin) synthesized via acid-catalyzed esterification. Ensure stoichiometric precision and minimal side-product impurities.",
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
        ),
        LabAssignment(
            id = "quest_water_neutralize",
            clientName = "City Water Authority",
            title = "Neutralize Acid Runoff",
            description = "Industrial wastewater effluent contains dangerous excess hydrochloric acid. Titrate and neutralize the effluent using stoichiometric sodium hydroxide to yield non-hazardous sodium chloride brine.",
            requirements = AssignmentRequirements(
                targetCompoundId = "NaCl",
                targetAmount = 50.0,
                minPurity = 95.0
            ),
            rewards = AssignmentRewards(
                credits = 500,
                unlocksToolId = "TITRATION_BURET"
            ),
            isCompleted = false
        ),
        LabAssignment(
            id = "quest_metallurgy_fe2o3",
            clientName = "Apex Metallurgy",
            title = "Calcinate Iron Oxide Pigment",
            description = "Thermal calcination and oxidation of iron salts to precipitate pure Ferric Oxide (Fe2O3) pigment for heavy-duty marine anti-corrosion coating.",
            requirements = AssignmentRequirements(
                targetCompoundId = "Fe2O3",
                targetAmount = 30.0,
                minPurity = 88.0
            ),
            rewards = AssignmentRewards(
                credits = 650,
                unlocksToolId = "CRUCIBLE"
            ),
            isCompleted = false
        ),
        LabAssignment(
            id = "quest_greenbio_ammonia",
            clientName = "GreenBio AgroChem",
            title = "Synthesize Ammonia Precursor",
            description = "Produce high-purity ammonia (NH3) gas for nitrogen-fixing fertilizer formulations through controlled catalytic combination.",
            requirements = AssignmentRequirements(
                targetCompoundId = "NH3",
                targetAmount = 20.0,
                minPurity = 90.0
            ),
            rewards = AssignmentRewards(
                credits = 700,
                unlocksToolId = "GAS_SYRINGE"
            ),
            isCompleted = false
        ),
        LabAssignment(
            id = "quest_power_copper",
            clientName = "Metro Power & Grid",
            title = "Electrolytic Copper Sulfate",
            description = "Synthesize pure Copper Sulfate (CuSO4) crystals to supply electroplating cells for electrical busbar conductive plating.",
            requirements = AssignmentRequirements(
                targetCompoundId = "CuSO4",
                targetAmount = 40.0,
                minPurity = 92.0
            ),
            rewards = AssignmentRewards(
                credits = 800,
                unlocksToolId = "ELECTRODES"
            ),
            isCompleted = false
        ),
        LabAssignment(
            id = "quest_aerospace_co2",
            clientName = "Aerospace Atmospheric Systems",
            title = "Decompose Calcium Carbonate",
            description = "Thermally decompose calcium carbonate limestone chips into calcium oxide and high-grade carbon dioxide gas for environmental scrubber calibration.",
            requirements = AssignmentRequirements(
                targetCompoundId = "CO2",
                targetAmount = 15.0,
                minPurity = 95.0
            ),
            rewards = AssignmentRewards(
                credits = 400,
                unlocksToolId = "EVAPORATING_DISH"
            ),
            isCompleted = false
        ),
        LabAssignment(
            id = "quest_photonic_agcl",
            clientName = "Photonic NanoTech",
            title = "Silver Chloride Halide Precipitation",
            description = "Synthesize high-purity silver chloride precipitate by stoichiometric metathesis of silver nitrate and sodium chloride for optical sensors.",
            requirements = AssignmentRequirements(
                targetCompoundId = "AgCl",
                targetAmount = 12.0,
                minPurity = 98.0
            ),
            rewards = AssignmentRewards(
                credits = 1200,
                unlocksToolId = "CENTRIFUGE"
            ),
            isCompleted = false
        )
    )
}
