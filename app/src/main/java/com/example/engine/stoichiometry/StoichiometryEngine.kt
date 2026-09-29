package com.example.engine.stoichiometry

import com.example.data.model.Chemical
import com.example.data.model.ChemicalCatalog
import com.example.data.model.DispensedChemical
import com.example.data.model.DispenserApparatusType
import com.example.data.model.ProductYield
import com.example.data.model.Reaction
import com.example.data.model.StoichiometryResult
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * StoichiometryEngine performs rigorous real-time quantitative chemical calculations:
 * 1. Mass-to-mole and volume-to-mole conversions.
 * 2. Parses balanced chemical equation coefficients.
 * 3. Identifies the Limiting Reagent and Excess Reagents.
 * 4. Calculates theoretical yields of all synthesized products.
 * 5. Computes exact unreacted remainders of excess reagents.
 */
object StoichiometryEngine {

    const val STP_MOLAR_VOLUME = 22.414 // L / mol

    /**
     * Converts a raw physical measurement into moles based on apparatus and chemical properties.
     */
    fun calculateMoles(
        chemical: Chemical,
        apparatusType: DispenserApparatusType,
        amountValue: Double,
        molarity: Double? = null
    ): Double {
        if (amountValue <= 0.0) return 0.0
        val mw = max(1.0, chemical.molarMass)

        return when (apparatusType) {
            DispenserApparatusType.ANALYTICAL_BALANCE -> {
                // Mass (g) / Molar Mass (g/mol)
                amountValue / mw
            }
            DispenserApparatusType.GRADUATED_CYLINDER -> {
                if (molarity != null && molarity > 0.0) {
                    // Molarity (mol/L) * Volume (L)
                    molarity * (amountValue / 1000.0)
                } else {
                    // Assume water-like density ~1.0 g/mL for pure liquids
                    amountValue / mw
                }
            }
            DispenserApparatusType.GAS_SYRINGE -> {
                // Volume at STP (L) / 22.414 (L/mol)
                amountValue / STP_MOLAR_VOLUME
            }
        }
    }

    /**
     * Converts moles back into the natural unit (grams, mL, or L) for a given apparatus.
     */
    fun convertMolesToUnit(
        chemical: Chemical,
        apparatusType: DispenserApparatusType,
        moles: Double,
        originalMolarity: Double? = null,
        solutionVolumeMl: Double? = null
    ): Pair<Double, Double?> {
        val mw = max(1.0, chemical.molarMass)
        return when (apparatusType) {
            DispenserApparatusType.ANALYTICAL_BALANCE -> {
                Pair(moles * mw, null)
            }
            DispenserApparatusType.GRADUATED_CYLINDER -> {
                if (originalMolarity != null && originalMolarity > 0.0 && solutionVolumeMl != null && solutionVolumeMl > 0.0) {
                    // Remaining concentration in the active solution volume
                    val remainingM = moles / (solutionVolumeMl / 1000.0)
                    Pair(solutionVolumeMl, remainingM)
                } else {
                    Pair(moles * mw, originalMolarity)
                }
            }
            DispenserApparatusType.GAS_SYRINGE -> {
                Pair(moles * STP_MOLAR_VOLUME, null)
            }
        }
    }

    /**
     * Parses stoichiometric coefficients from a balanced chemical equation.
     * Example: "2Al + 3CuCl2 -> 2AlCl3 + 3Cu"
     * returns reactant coefficients {Al=2, CuCl2=3} and product coefficients {AlCl3=2, Cu=3}.
     */
    fun parseCoefficients(
        equation: String,
        reactantIds: Set<String>,
        productIds: List<String>
    ): Pair<Map<String, Int>, Map<String, Int>> {
        val reactantCoeffs = reactantIds.associateWith { 1 }.toMutableMap()
        val productCoeffs = productIds.associateWith { 1 }.toMutableMap()

        try {
            // Split equation on arrow
            val arrowRegex = Regex("[→⇌]|->|-->")
            val parts = equation.split(arrowRegex)
            if (parts.size >= 2) {
                val leftSide = parts[0]
                val rightSide = parts[1]

                // Parse left side
                parseSideTerms(leftSide).forEach { (formula, coeff) ->
                    val matchedId = matchChemicalId(formula, reactantIds)
                    if (matchedId != null) {
                        reactantCoeffs[matchedId] = coeff
                    }
                }

                // Parse right side
                parseSideTerms(rightSide).forEach { (formula, coeff) ->
                    val matchedId = matchChemicalId(formula, productIds.toSet())
                    if (matchedId != null) {
                        productCoeffs[matchedId] = coeff
                    }
                }
            }
        } catch (_: Exception) {
            // Fallback to 1:1 if parsing encounters complex custom notation
        }

        return Pair(reactantCoeffs, productCoeffs)
    }

    private fun parseSideTerms(sideText: String): List<Pair<String, Int>> {
        val results = mutableListOf<Pair<String, Int>>()
        val cleanTerms = sideText.split("+")

        for (rawTerm in cleanTerms) {
            var term = rawTerm.trim()
            // Strip state tags like (s), (l), (g), (aq), (Conc), (Dilute), ↑, ↓, etc.
            term = term.replace(Regex("\\(.*?\\)"), "")
            term = term.replace("↑", "").replace("↓", "").trim()
            if (term.isBlank() || term.equals("Heat", ignoreCase = true) || term.equals("Electricity", ignoreCase = true)) {
                continue
            }

            // Extract leading integer coefficient
            val match = Regex("^(\\d+)\\s*(.*)$").find(term)
            if (match != null) {
                val coeff = match.groupValues[1].toIntOrNull() ?: 1
                val formula = match.groupValues[2].trim()
                if (formula.isNotBlank()) {
                    results.add(Pair(formula, coeff))
                }
            } else {
                results.add(Pair(term, 1))
            }
        }

        return results
    }

    private fun matchChemicalId(formula: String, candidates: Set<String>): String? {
        val clean = formula.replace(" ", "").trim()
        val norm = ChemicalCatalog.normalizeReactant(clean)

        return candidates.find { id ->
            val normId = ChemicalCatalog.normalizeReactant(id)
            id.equals(clean, ignoreCase = true) ||
            normId.equals(norm, ignoreCase = true) ||
            id.replace(" ", "").equals(clean, ignoreCase = true) ||
            clean.startsWith(id, ignoreCase = true)
        }
    }

    /**
     * Executes stoichiometric analysis for the active reaction and given dispensed reagents.
     */
    fun calculateStoichiometry(
        reaction: Reaction,
        initialDispensed: Map<String, DispensedChemical>
    ): StoichiometryResult {
        val (reactantCoeffs, productCoeffs) = parseCoefficients(
            equation = reaction.equation,
            reactantIds = reaction.reactantIds,
            productIds = reaction.productIds
        )

        // Ensure all reactants have a quantity (if missing, synthesize a standard quantity)
        val fullInitial = initialDispensed.toMutableMap()
        reaction.reactantIds.forEach { rId ->
            if (rId !in fullInitial) {
                val chem = ChemicalCatalog.getChemical(rId)
                if (chem != null) {
                    val app = when {
                        chem.category == com.example.data.model.ChemicalCategory.GAS -> DispenserApparatusType.GAS_SYRINGE
                        chem.physicalState.contains("Liquid") || chem.physicalState.contains("Aqueous") || chem.category == com.example.data.model.ChemicalCategory.ACID || chem.category == com.example.data.model.ChemicalCategory.BASE -> DispenserApparatusType.GRADUATED_CYLINDER
                        else -> DispenserApparatusType.ANALYTICAL_BALANCE
                    }
                    val amount = when (app) {
                        DispenserApparatusType.ANALYTICAL_BALANCE -> 10.0
                        DispenserApparatusType.GRADUATED_CYLINDER -> 50.0
                        DispenserApparatusType.GAS_SYRINGE -> 1.0
                    }
                    val moles = calculateMoles(chem, app, amount, if (app == DispenserApparatusType.GRADUATED_CYLINDER) 1.0 else null)
                    fullInitial[rId] = DispensedChemical(
                        chemical = chem,
                        apparatusType = app,
                        amountValue = amount,
                        molarity = if (app == DispenserApparatusType.GRADUATED_CYLINDER) 1.0 else null,
                        moles = moles
                    )
                }
            }
        }

        // 1. Calculate molar ratio (n / coeff) for each reactant
        val ratios = mutableMapOf<String, Double>()
        reaction.reactantIds.forEach { rId ->
            val dispensed = fullInitial[rId]
            val moles = dispensed?.moles ?: 0.05
            val coeff = max(1, reactantCoeffs[rId] ?: 1)
            ratios[rId] = moles / coeff
        }

        // 2. Identify Limiting Reagent (minimum ratio)
        var minRatio = Double.MAX_VALUE
        var limitingId = reaction.reactantIds.firstOrNull() ?: ""
        ratios.forEach { (rId, ratio) ->
            if (ratio < minRatio) {
                minRatio = ratio
                limitingId = rId
            }
        }

        val extentOfReaction = max(0.0, minRatio)
        val maxRatio = ratios.values.maxOrNull() ?: minRatio
        val isExactRatio = abs(maxRatio - minRatio) < 0.0005

        // 3. Compute Consumed Moles & Remaining Reagent Quantities
        val consumedMolesMap = mutableMapOf<String, Double>()
        val remainingQuantitiesMap = mutableMapOf<String, DispensedChemical>()
        val unreactedExcessDescriptions = mutableListOf<String>()

        reaction.reactantIds.forEach { rId ->
            val initial = fullInitial[rId] ?: return@forEach
            val coeff = max(1, reactantCoeffs[rId] ?: 1)
            val consumed = coeff * extentOfReaction
            consumedMolesMap[rId] = consumed

            val remainingMoles = max(0.0, initial.moles - consumed)
            val (remVal, remM) = convertMolesToUnit(
                chemical = initial.chemical,
                apparatusType = initial.apparatusType,
                moles = remainingMoles,
                originalMolarity = initial.molarity,
                solutionVolumeMl = if (initial.apparatusType == DispenserApparatusType.GRADUATED_CYLINDER) initial.amountValue else null
            )

            val remDispensed = initial.copy(
                amountValue = remVal,
                molarity = remM,
                moles = remainingMoles
            )
            remainingQuantitiesMap[rId] = remDispensed

            if (rId != limitingId && remainingMoles > 0.0001) {
                val chemName = initial.chemical.name
                val desc = when (initial.apparatusType) {
                    DispenserApparatusType.ANALYTICAL_BALANCE ->
                        String.format("%.2f g of unreacted %s powder remaining (%.4f mol)", remVal, chemName, remainingMoles)
                    DispenserApparatusType.GRADUATED_CYLINDER -> {
                        if (remM != null) {
                            String.format("%.2f M %s remaining in solution (%.1f mL, %.4f mol)", remM, chemName, remVal, remainingMoles)
                        } else {
                            String.format("%.1f mL of unreacted %s remaining (%.4f mol)", remVal, chemName, remainingMoles)
                        }
                    }
                    DispenserApparatusType.GAS_SYRINGE ->
                        String.format("%.2f L of unreacted %s gas remaining (%.4f mol)", remVal, chemName, remainingMoles)
                }
                unreactedExcessDescriptions.add(desc)
            }
        }

        // 4. Calculate Theoretical Yields of Products
        val productYieldsMap = mutableMapOf<String, ProductYield>()
        reaction.productIds.forEach { pId ->
            val chem = ChemicalCatalog.getChemical(pId)
            if (chem != null) {
                val coeff = max(1, productCoeffs[pId] ?: 1)
                val prodMoles = coeff * extentOfReaction
                val massGrams = prodMoles * chem.molarMass
                val isSolid = chem.physicalState.contains("Solid", ignoreCase = true) ||
                              chem.physicalState.contains("Precipitate", ignoreCase = true)

                val volumeMl = if (!isSolid && (chem.physicalState.contains("Liquid") || chem.physicalState.contains("Aqueous"))) {
                    massGrams // approx 1 g/mL
                } else null

                productYieldsMap[pId] = ProductYield(
                    chemical = chem,
                    molesProduced = prodMoles,
                    massGrams = massGrams,
                    volumeMl = volumeMl,
                    isSolidOrPrecipitate = isSolid
                )
            }
        }

        // 5. Summary Message
        val limitingChem = ChemicalCatalog.getChemical(limitingId)
        val limitingName = limitingChem?.name ?: limitingId
        val summaryMessage = if (isExactRatio) {
            "Exact stoichiometric proportions: 100% of all reactants converted into products with zero leftover excess."
        } else {
            "Limiting reagent was $limitingName (${String.format("%.4f", fullInitial[limitingId]?.moles ?: 0.0)} mol). Reaction ceased when $limitingName was completely consumed, leaving ${unreactedExcessDescriptions.size} excess reagent(s) unreacted."
        }

        return StoichiometryResult(
            reaction = reaction,
            limitingReagentId = limitingId,
            initialQuantities = fullInitial,
            consumedMoles = consumedMolesMap,
            remainingQuantities = remainingQuantitiesMap,
            productYields = productYieldsMap,
            extentOfReaction = extentOfReaction,
            summaryMessage = summaryMessage,
            unreactedExcessDescriptions = unreactedExcessDescriptions,
            isExactStoichiometricRatio = isExactRatio
        )
    }

    /**
     * Calculates dynamic blended solution color, transparency alpha, and pH level
     * as a stoichiometric weighted average of the formed products and remaining unreacted reagents.
     */
    fun calculateBlendedSolution(
        reaction: Reaction,
        stoich: StoichiometryResult
    ): BlendedSolutionProperties {
        // 1. Identify settled precipitates and excess unreacted solid reagents
        val unreactedSolids = stoich.remainingQuantities.values.filter { rem ->
            rem.moles > 0.0001 && (
                rem.chemical.physicalState.contains("Solid", ignoreCase = true) ||
                rem.chemical.physicalState.contains("Precipitate", ignoreCase = true) ||
                rem.apparatusType == DispenserApparatusType.ANALYTICAL_BALANCE ||
                rem.chemical.category == com.example.data.model.ChemicalCategory.SALT ||
                rem.chemical.elementSeries?.contains("Metal") == true
            )
        }
        val hasUnreactedSolid = unreactedSolids.isNotEmpty()
        val unreactedSolidChem = unreactedSolids.maxByOrNull { it.moles }?.chemical
        val unreactedSolidColorHex = unreactedSolidChem?.colorHex ?: 0xFF94A3B8L
        val unreactedSolidName = unreactedSolidChem?.name

        val hasProductPrecipitate = stoich.productYields.values.any { it.isSolidOrPrecipitate }
        val hasSettledPrecipitate = hasProductPrecipitate || hasUnreactedSolid

        // 2. Identify all liquid/solution components contributing to solution color and pH
        // Products that stay dissolved in solution
        val solubleProducts = stoich.productYields.values.filter { !it.isSolidOrPrecipitate }
        // Excess unreacted liquid/aqueous reagents
        val unreactedLiquids = stoich.remainingQuantities.values.filter { rem ->
            rem.moles > 0.0001 && (
                rem.apparatusType == DispenserApparatusType.GRADUATED_CYLINDER ||
                rem.chemical.physicalState.contains("Liquid", ignoreCase = true) ||
                rem.chemical.physicalState.contains("Aqueous", ignoreCase = true) ||
                rem.chemical.category == com.example.data.model.ChemicalCategory.ACID ||
                rem.chemical.category == com.example.data.model.ChemicalCategory.BASE
            )
        }

        // 3. Dynamic Color Blending
        // If ideal stoichiometric ratio or no excess liquids, resulting color is reaction.resultingColor
        val blendedColorHex: Long
        val transparencyAlpha: Float

        if (unreactedLiquids.isEmpty() || stoich.isExactStoichiometricRatio) {
            blendedColorHex = reaction.resultingColor
            transparencyAlpha = if (hasSettledPrecipitate) 0.92f else 0.72f
        } else {
            // Weighted average of resulting product color + each excess liquid reagent's color
            val prodMoles = max(0.01, stoich.extentOfReaction)
            val rxColor = reaction.resultingColor
            val rxR = ((rxColor shr 16) and 0xFF).toDouble()
            val rxG = ((rxColor shr 8) and 0xFF).toDouble()
            val rxB = (rxColor and 0xFF).toDouble()

            var weightedR = rxR * prodMoles
            var weightedG = rxG * prodMoles
            var weightedB = rxB * prodMoles
            var totalLiquidMoles = prodMoles

            unreactedLiquids.forEach { unreacted ->
                val uMoles = unreacted.moles
                val uColor = unreacted.chemical.colorHex
                val uR = ((uColor shr 16) and 0xFF).toDouble()
                val uG = ((uColor shr 8) and 0xFF).toDouble()
                val uB = (uColor and 0xFF).toDouble()

                weightedR += uR * uMoles
                weightedG += uG * uMoles
                weightedB += uB * uMoles
                totalLiquidMoles += uMoles
            }

            val finalR = (weightedR / totalLiquidMoles).toInt().coerceIn(0, 255)
            val finalG = (weightedG / totalLiquidMoles).toInt().coerceIn(0, 255)
            val finalB = (weightedB / totalLiquidMoles).toInt().coerceIn(0, 255)

            // Reconstruct ARGB (with alpha channel)
            blendedColorHex = (0xFFL shl 24) or (finalR.toLong() shl 16) or (finalG.toLong() shl 8) or finalB.toLong()

            // Dynamic transparency: denser/opaque if cloudy precipitate is present or higher concentration
            transparencyAlpha = when {
                hasSettledPrecipitate -> 0.94f // turbid precipitate suspension
                totalLiquidMoles > 0.2 -> 0.85f // concentrated solution
                else -> 0.75f // clear dilute solution
            }
        }

        // 4. Dynamic Weighted pH Calculation
        val blendedPh: Double = if (unreactedLiquids.isEmpty() || stoich.isExactStoichiometricRatio) {
            reaction.resultingPh
        } else {
            // Check for strong acid / strong base remainders
            val excessAcid = unreactedLiquids.find { it.chemical.category == com.example.data.model.ChemicalCategory.ACID }
            val excessBase = unreactedLiquids.find { it.chemical.category == com.example.data.model.ChemicalCategory.BASE }

            when {
                excessAcid != null && excessAcid.moles > 0.001 -> {
                    // Excess acid dictates acidic pH (e.g. 0.5 - 4.5 based on remaining molarity/moles)
                    val estM = excessAcid.molarity ?: (excessAcid.moles / 0.05).coerceAtMost(6.0)
                    val phVal = kotlin.math.max(0.5, -kotlin.math.log10(kotlin.math.max(0.0001, estM)))
                    (phVal * 10).roundToInt() / 10.0
                }
                excessBase != null && excessBase.moles > 0.001 -> {
                    // Excess base dictates alkaline pH (e.g. 9.5 - 13.8)
                    val estM = excessBase.molarity ?: (excessBase.moles / 0.05).coerceAtMost(6.0)
                    val pOh = kotlin.math.max(0.2, -kotlin.math.log10(kotlin.math.max(0.0001, estM)))
                    val phVal = kotlin.math.min(13.8, 14.0 - pOh)
                    (phVal * 10).roundToInt() / 10.0
                }
                else -> {
                    // Weighted average between reaction resulting pH and unreacted reagents
                    val prodWeight = max(0.01, stoich.extentOfReaction)
                    var weightedPhSum = reaction.resultingPh * prodWeight
                    var totalMoles = prodWeight

                    unreactedLiquids.forEach { unreacted ->
                        weightedPhSum += unreacted.chemical.ph * unreacted.moles
                        totalMoles += unreacted.moles
                    }

                    val rawPh = weightedPhSum / totalMoles
                    ((rawPh * 10).roundToInt() / 10.0).coerceIn(0.0, 14.0)
                }
            }
        }

        return BlendedSolutionProperties(
            blendedColorHex = blendedColorHex,
            transparencyAlpha = transparencyAlpha,
            blendedPh = blendedPh,
            hasSettledPrecipitate = hasSettledPrecipitate,
            hasUnreactedSolid = hasUnreactedSolid,
            unreactedSolidColorHex = unreactedSolidColorHex,
            unreactedSolidName = unreactedSolidName
        )
    }
}

data class BlendedSolutionProperties(
    val blendedColorHex: Long,
    val transparencyAlpha: Float,
    val blendedPh: Double,
    val hasSettledPrecipitate: Boolean,
    val hasUnreactedSolid: Boolean,
    val unreactedSolidColorHex: Long,
    val unreactedSolidName: String?
)
