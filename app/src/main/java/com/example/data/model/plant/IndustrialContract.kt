package com.example.data.model.plant

/**
 * Industrial Contracts & Process Recipes Catalog for continuous chemical manufacturing.
 */

data class IndustrialContract(
    val id: String,
    val clientName: String,
    val title: String,
    val description: String,
    val recipeId: String,
    val targetProduct: String,
    val targetVolumeL: Double,
    val deliveredVolumeL: Double = 0.0,
    val minPurityPercent: Double = 90.0,
    val currentAveragePurity: Double = 0.0,
    val rewardCredits: Int,
    val environmentalBonus: Int = 500,
    val isCompleted: Boolean = false
)

object IndustrialProcessCatalog {

    val PROCESS_SULFURIC_ACID = IndustrialProcessRecipe(
        id = "proc_sulfuric_acid",
        name = "Contact Process (SO3 Hydration)",
        equation = "SO3 (g) + H2O (l) → H2SO4 (aq)",
        reactantAId = "SO3",
        reactantBId = "H2O",
        primaryProductId = "H2SO4",
        optimalTemperatureC = 85.0,
        optimalResidenceTimeMinutes = 24.0,
        baseRateConstantK = 0.08,
        activationEnergyEa = 45.0,
        heatOfReactionDeltaH = -132.0, // Highly exothermic
        generatesHazardousGas = true,
        hazardousGasName = "SO2 / SO3 Acid Mist",
        hazardousGasPpmFactor = 15.0,
        effluentIsAcidic = true
    )

    val PROCESS_AMMONIA_NEUTRALIZATION = IndustrialProcessRecipe(
        id = "proc_ammonia_neutral",
        name = "Ammonium Sulfate Fertilizer Synthesis",
        equation = "2NH3 (aq) + H2SO4 (aq) → (NH4)2SO4 (aq)",
        reactantAId = "NH3",
        reactantBId = "H2SO4",
        primaryProductId = "(NH4)2SO4",
        optimalTemperatureC = 60.0,
        optimalResidenceTimeMinutes = 18.0,
        baseRateConstantK = 0.12,
        activationEnergyEa = 38.0,
        heatOfReactionDeltaH = -280.0, // Very exothermic
        generatesHazardousGas = true,
        hazardousGasName = "NH3 Fumes",
        hazardousGasPpmFactor = 12.0,
        effluentIsAcidic = false
    )

    val PROCESS_BRINE_NEUTRALIZATION = IndustrialProcessRecipe(
        id = "proc_brine_neutral",
        name = "Industrial Acid-Base Effluent Neutralization",
        equation = "HCl (aq) + NaOH (aq) → NaCl (aq) + H2O (l)",
        reactantAId = "HCl",
        reactantBId = "NaOH",
        primaryProductId = "NaCl",
        optimalTemperatureC = 45.0,
        optimalResidenceTimeMinutes = 12.0,
        baseRateConstantK = 0.25,
        activationEnergyEa = 22.0,
        heatOfReactionDeltaH = -57.3, // Moderately exothermic
        generatesHazardousGas = false,
        hazardousGasName = null,
        hazardousGasPpmFactor = 0.0,
        effluentIsAcidic = false
    )

    val PROCESS_ESTERIFICATION = IndustrialProcessRecipe(
        id = "proc_esterification",
        name = "Ethyl Acetate Continuous Esterification",
        equation = "CH3COOH + C2H5OH ⇌ CH3COOC2H5 + H2O",
        reactantAId = "CH3COOH",
        reactantBId = "C2H5OH",
        primaryProductId = "CH3COOC2H5",
        optimalTemperatureC = 78.0,
        optimalResidenceTimeMinutes = 35.0,
        baseRateConstantK = 0.05,
        activationEnergyEa = 58.0,
        heatOfReactionDeltaH = -10.0, // Slightly exothermic equilibrium
        generatesHazardousGas = false,
        hazardousGasName = null,
        hazardousGasPpmFactor = 0.0,
        effluentIsAcidic = false
    )

    val ALL_RECIPES = listOf(
        PROCESS_SULFURIC_ACID,
        PROCESS_AMMONIA_NEUTRALIZATION,
        PROCESS_BRINE_NEUTRALIZATION,
        PROCESS_ESTERIFICATION
    )

    fun getRecipe(id: String): IndustrialProcessRecipe? =
        ALL_RECIPES.find { it.id == id } ?: ALL_RECIPES.firstOrNull()

    val DEFAULT_CONTRACTS: List<IndustrialContract> = listOf(
        IndustrialContract(
            id = "ind_contract_h2so4",
            clientName = "Apex Heavy Chemical Industries",
            title = "Bulk Sulfuric Acid Campaign",
            description = "Commissioning high-capacity CSTR contact line. Maintain reactor temperature at ~85°C and ensure continuous scrubber wash to suppress SO2/SO3 acid mist emissions.",
            recipeId = "proc_sulfuric_acid",
            targetProduct = "H2SO4",
            targetVolumeL = 5000.0,
            deliveredVolumeL = 0.0,
            minPurityPercent = 95.0,
            currentAveragePurity = 0.0,
            rewardCredits = 4500,
            environmentalBonus = 750
        ),
        IndustrialContract(
            id = "ind_contract_ammonia",
            clientName = "Titan Agro-Chemicals Ltd",
            title = "Continuous Ammonium Sulfate Stream",
            description = "Synthesize agricultural-grade ammonium sulfate fertilizer in Plug Flow Reactor (PFR). Ammonia fumes must be captured with >=98% absorption scrubber efficiency.",
            recipeId = "proc_ammonia_neutral",
            targetProduct = "(NH4)2SO4",
            targetVolumeL = 3500.0,
            deliveredVolumeL = 0.0,
            minPurityPercent = 92.0,
            currentAveragePurity = 0.0,
            rewardCredits = 3800,
            environmentalBonus = 600
        ),
        IndustrialContract(
            id = "ind_contract_effluent",
            clientName = "Metropolitan Water Reclamation",
            title = "Neutralized Industrial Brine Discharge",
            description = "High-flow acid-base neutralization stream. Effluent neutralization basin pH must strictly remain within legal discharge limits (pH 6.5 - 8.5) at all times.",
            recipeId = "proc_brine_neutral",
            targetProduct = "NaCl",
            targetVolumeL = 4000.0,
            deliveredVolumeL = 0.0,
            minPurityPercent = 90.0,
            currentAveragePurity = 0.0,
            rewardCredits = 3200,
            environmentalBonus = 800
        ),
        IndustrialContract(
            id = "ind_contract_ester",
            clientName = "SolventWorks Specialty Polymers",
            title = "Continuous Ethyl Acetate Refining",
            description = "Long residence time esterification run. Tune cooling jacket to avoid overheating equilibrium back-reactions and deliver solvent-grade pure ethyl acetate.",
            recipeId = "proc_esterification",
            targetProduct = "CH3COOC2H5",
            targetVolumeL = 2000.0,
            deliveredVolumeL = 0.0,
            minPurityPercent = 94.0,
            currentAveragePurity = 0.0,
            rewardCredits = 4000,
            environmentalBonus = 500
        )
    )
}
