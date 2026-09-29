package com.example.data.model

/**
 * Data models for the Quantitative Chemistry & Reaction Mechanics Module.
 * Defines laboratory measurement apparatuses, dispensed quantities,
 * and stoichiometric outcomes (limiting reagents, yields, and unreacted remainders).
 */
enum class DispenserApparatusType(
    val title: String,
    val apparatusName: String,
    val defaultUnit: String,
    val iconEmoji: String,
    val description: String
) {
    ANALYTICAL_BALANCE(
        title = "Analytical Balance",
        apparatusName = "Digital Analytical Balance & Spatula",
        defaultUnit = "g",
        iconEmoji = "⚖️",
        description = "Precision 4-decimal analytical balance with tare button and micro-spatula for solid reagents."
    ),
    GRADUATED_CYLINDER(
        title = "Graduated Cylinder / Buret",
        apparatusName = "Volumetric Graduated Cylinder & Precision Buret",
        defaultUnit = "mL",
        iconEmoji = "🧪",
        description = "Calibrated borosilicate glass cylinder delivering exact liquid aliquot volumes and solution molarities."
    ),
    GAS_SYRINGE(
        title = "Gas Syringe & Valve",
        apparatusName = "Gastight Glass Syringe & STP Pressure Valve",
        defaultUnit = "L",
        iconEmoji = "💨",
        description = "Calibrated gas syringe measuring vapor volumes at Standard Temperature & Pressure (22.414 L/mol)."
    )
}

/**
 * Represents a chemical reagent dispensed into the vessel with explicit quantitative measurement.
 */
data class DispensedChemical(
    val chemical: Chemical,
    val apparatusType: DispenserApparatusType,
    val amountValue: Double, // grams for solid, mL for liquid, L for gas
    val molarity: Double? = null, // for liquid solutions (M)
    val moles: Double // calculated stoichiometric moles (mol)
) {
    val displayQuantity: String
        get() = when (apparatusType) {
            DispenserApparatusType.ANALYTICAL_BALANCE -> String.format("%.2f g (%.4f mol)", amountValue, moles)
            DispenserApparatusType.GRADUATED_CYLINDER -> {
                if (molarity != null) {
                    String.format("%.1f mL @ %.2f M (%.4f mol)", amountValue, molarity, moles)
                } else {
                    String.format("%.1f mL (%.4f mol)", amountValue, moles)
                }
            }
            DispenserApparatusType.GAS_SYRINGE -> String.format("%.2f L STP (%.4f mol)", amountValue, moles)
        }

    val displayCompact: String
        get() = when (apparatusType) {
            DispenserApparatusType.ANALYTICAL_BALANCE -> String.format("%.1fg", amountValue)
            DispenserApparatusType.GRADUATED_CYLINDER -> {
                if (molarity != null) String.format("%.0fmL/%.1fM", amountValue, molarity)
                else String.format("%.0fmL", amountValue)
            }
            DispenserApparatusType.GAS_SYRINGE -> String.format("%.2fL", amountValue)
        }
}

/**
 * Output yield of a single synthesized product.
 */
data class ProductYield(
    val chemical: Chemical,
    val molesProduced: Double,
    val massGrams: Double,
    val volumeMl: Double? = null,
    val isSolidOrPrecipitate: Boolean
) {
    val displayYield: String
        get() = if (volumeMl != null && !isSolidOrPrecipitate) {
            String.format("%.2f g (approx. %.1f mL, %.4f mol)", massGrams, volumeMl, molesProduced)
        } else {
            String.format("%.2f g (%.4f mol)", massGrams, molesProduced)
        }
}

/**
 * Complete outcome of a stoichiometric reaction execution.
 */
data class StoichiometryResult(
    val reaction: Reaction,
    val limitingReagentId: String,
    val initialQuantities: Map<String, DispensedChemical>,
    val consumedMoles: Map<String, Double>,
    val remainingQuantities: Map<String, DispensedChemical>,
    val productYields: Map<String, ProductYield>,
    val extentOfReaction: Double, // xi in moles
    val summaryMessage: String,
    val unreactedExcessDescriptions: List<String>,
    val isExactStoichiometricRatio: Boolean
)
