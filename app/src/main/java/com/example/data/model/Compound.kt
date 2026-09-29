package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Physical thermodynamic state of matter at standard ambient temperature and pressure (STP).
 */
enum class StateAtSTP(val value: String) {
    @Json(name = "solid")
    SOLID("solid"),

    @Json(name = "liquid")
    LIQUID("liquid"),

    @Json(name = "gas")
    GAS("gas");

    companion object {
        fun fromString(value: String): StateAtSTP = when (value.trim().lowercase()) {
            "solid" -> SOLID
            "gas" -> GAS
            else -> LIQUID
        }
    }
}

/**
 * 2D visual rendering particle archetype for simulation canvas rendering.
 */
enum class SvgParticleType(val value: String) {
    @Json(name = "bubble")
    BUBBLE("bubble"),

    @Json(name = "vapor")
    VAPOR("vapor"),

    @Json(name = "crystal")
    CRYSTAL("crystal"),

    @Json(name = "fluid")
    FLUID("fluid");

    companion object {
        fun fromState(state: StateAtSTP, isDissolvedGas: Boolean = false): SvgParticleType = when {
            isDissolvedGas -> BUBBLE
            state == StateAtSTP.GAS -> VAPOR
            state == StateAtSTP.SOLID -> CRYSTAL
            else -> FLUID
        }
    }
}

/**
 * Physical, chemical, and safety properties of a compound.
 */
@JsonClass(generateAdapter = true)
data class CompoundProperties(
    val stateAtSTP: StateAtSTP,
    val molarMass: Double,         // g/mol
    val density: Double,           // g/cm³ for solids/liquids or g/L for gases
    val colorHex: String,          // Visual color on canvas (e.g., "#38BDF8")
    val transparency: Float,       // 0.0 (transparent) to 1.0 (fully opaque)
    val viscosity: Double? = null, // Dynamic viscosity in mPa·s (for liquids)
    val boilingPointC: Double,     // Normal boiling point in °C
    val meltingPointC: Double,     // Normal melting point in °C
    val hazardRating: String       // NFPA 704 / GHS classification
)

/**
 * Visual particle assets and styling parameters for 2D laboratory rendering.
 */
@JsonClass(generateAdapter = true)
data class CompoundVisualAssets(
    val svgParticleType: SvgParticleType,
    val renderColor: String        // Hexadecimal color code matching visual layer
)

/**
 * Typed domain model and persistence schema for chemical compounds in Elementa.
 * Represents compound lifecycle, discovery state (Pokedex sync), and physics readiness.
 */
@JsonClass(generateAdapter = true)
data class Compound(
    val id: String,                  // Unique identifier, e.g., "comp_h2o"
    val formula: String,             // Chemical formula notation, e.g., "H2O"
    val name: String,                // Formal or common nomenclature, e.g., "Water"
    val discovered: Boolean = false, // Default: false until synthesized or found in lab
    val isSimulatable: Boolean = true, // True if all physical/render properties exist
    val properties: CompoundProperties,
    val visualAssets: CompoundVisualAssets
) {
    /**
     * Validates simulation readiness: confirms that all mandatory thermodynamic,
     * mechanical, and visual properties are strictly present and physically consistent.
     */
    fun checkSimulatable(): Boolean {
        val p = properties
        val v = visualAssets

        val validMass = p.molarMass > 0.0
        val validDensity = p.density > 0.0
        val validTransparency = p.transparency in 0.0f..1.0f
        val validColor = p.colorHex.isNotBlank() && (p.colorHex.startsWith("#") || p.colorHex.startsWith("0x"))
        val validRenderColor = v.renderColor.isNotBlank()
        val validPhaseTransition = p.boilingPointC >= p.meltingPointC
        val validLiquidViscosity = if (p.stateAtSTP == StateAtSTP.LIQUID) {
            p.viscosity != null && p.viscosity > 0.0
        } else {
            true
        }

        return validMass && validDensity && validTransparency && validColor &&
                validRenderColor && validPhaseTransition && validLiquidViscosity
    }
}

/**
 * Extension mapper converting legacy or catalog Chemical entries to typed Compound instances.
 */
fun Chemical.toCompound(discovered: Boolean = false): Compound {
    val state = when {
        physicalState.contains("Gas", ignoreCase = true) -> StateAtSTP.GAS
        physicalState.contains("Solid", ignoreCase = true) || physicalState.contains("Precipitate", ignoreCase = true) -> StateAtSTP.SOLID
        else -> StateAtSTP.LIQUID
    }

    val hexString = String.format("#%08X", colorHex)

    val densityEst = when (state) {
        StateAtSTP.GAS -> (molarMass / 22.4).coerceAtLeast(0.08) // Gas density in g/L at STP
        StateAtSTP.LIQUID -> 1.0 + (molarMass / 600.0)
        StateAtSTP.SOLID -> 2.1 + (molarMass / 80.0)
    }

    val bp = when (state) {
        StateAtSTP.GAS -> -30.0 - (100.0 / molarMass.coerceAtLeast(1.0))
        StateAtSTP.LIQUID -> 100.0 + (molarMass * 0.4)
        StateAtSTP.SOLID -> 600.0 + (molarMass * 2.5)
    }

    val mp = when (state) {
        StateAtSTP.GAS -> bp - 80.0
        StateAtSTP.LIQUID -> 0.0
        StateAtSTP.SOLID -> 150.0 + (molarMass * 1.2)
    }

    val visc = if (state == StateAtSTP.LIQUID) 1.0016 else null

    val hazardDesc = if (hazards.isEmpty()) {
        "NFPA 704: Health 0, Flammability 0, Instability 0 (Non-hazardous)"
    } else {
        hazards.joinToString(separator = "; ") { "${it.iconChar} GHS: ${it.label}" }
    }

    val particleType = when (state) {
        StateAtSTP.GAS -> SvgParticleType.VAPOR
        StateAtSTP.SOLID -> SvgParticleType.CRYSTAL
        StateAtSTP.LIQUID -> if (category == ChemicalCategory.GAS) SvgParticleType.BUBBLE else SvgParticleType.FLUID
    }

    val compId = if (id.startsWith("comp_")) id else "comp_${id.lowercase()}"

    val properties = CompoundProperties(
        stateAtSTP = state,
        molarMass = molarMass,
        density = (densityEst * 1000).toInt() / 1000.0,
        colorHex = hexString,
        transparency = if (state == StateAtSTP.GAS) 0.35f else 0.85f,
        viscosity = visc,
        boilingPointC = (bp * 10).toInt() / 10.0,
        meltingPointC = (mp * 10).toInt() / 10.0,
        hazardRating = hazardDesc
    )

    val visualAssets = CompoundVisualAssets(
        svgParticleType = particleType,
        renderColor = hexString
    )

    val compound = Compound(
        id = compId,
        formula = formula,
        name = name,
        discovered = discovered || isPreUnlocked,
        isSimulatable = true,
        properties = properties,
        visualAssets = visualAssets
    )

    return compound.copy(isSimulatable = compound.checkSimulatable())
}
