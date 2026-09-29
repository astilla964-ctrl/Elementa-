package com.example.data.model

enum class HazardSymbol(val label: String, val iconChar: String) {
    CORROSIVE("Corrosive", "⚠️"),
    FLAMMABLE("Flammable", "🔥"),
    OXIDIZING("Oxidizing", "⚡"),
    TOXIC("Toxic", "☠️"),
    COMPRESSED_GAS("Compressed Gas", "💨"),
    IRRITANT("Irritant", "❗"),
    BIOHAZARD("Biohazard", "☣️")
}

enum class ChemicalCategory(val displayName: String) {
    ELEMENT("Element"),
    ACID("Acid"),
    BASE("Base"),
    SALT("Salt"),
    OXIDE("Oxide"),
    GAS("Gas"),
    ORGANIC("Organic")
}

enum class LabToolType(
    val title: String,
    val maxTempRating: String,
    val capacity: String,
    val compatibility: String,
    val description: String,
    val sopNotes: List<String>
) {
    BEAKER(
        title = "Beaker",
        maxTempRating = "500°C",
        capacity = "250 mL",
        compatibility = "High (Borosilicate 3.3)",
        description = "Standard flat-bottom cylindrical glass vessel with spout for pouring liquids and handling general chemical mixtures.",
        sopNotes = listOf(
            "Use wire gauze with ceramic center when heating over an open flame.",
            "Do not use for accurate volume measurement; graduations are approximate (±5%).",
            "Handle with beaker tongs when hot."
        )
    ),
    ERLENMEYER_FLASK(
        title = "Erlenmeyer Flask",
        maxTempRating = "500°C",
        capacity = "250 mL",
        compatibility = "High resistance to thermal shock and splashing",
        description = "Conical vessel with flat bottom and narrow cylindrical neck, designed for swirl mixing without splashing and supporting filtration.",
        sopNotes = listOf(
            "Ideal for titration reactions and recrystallization.",
            "Swirl horizontally using wrist motion.",
            "Never stopper a vessel during exothermic heating."
        )
    ),
    TEST_TUBE(
        title = "Test Tube",
        maxTempRating = "450°C",
        capacity = "25 mL",
        compatibility = "Borosilicate glass (Resistant to boiling acids)",
        description = "Slender glass finger-like tube used for qualitative analysis, precipitation observations, and small-scale reactions.",
        sopNotes = listOf(
            "Point the tube opening away from yourself and others when heating.",
            "Heat gently at a 45° angle, waving through the flame.",
            "Place in dedicated test tube rack when cooling."
        )
    ),
    BUNSEN_BURNER(
        title = "Bunsen Burner",
        maxTempRating = "1500°C",
        capacity = "Continuous Gas Flow",
        compatibility = "Methane/LPG & Metal Construction",
        description = "High-temperature laboratory gas burner producing a single open gas flame, with adjustable air collar to regulate flame oxidation.",
        sopNotes = listOf(
            "Check gas hose for cracks before lighting.",
            "Ignite with air collar closed (cool luminous yellow flame), then adjust to blue oxidizing flame.",
            "Never leave unattended while ignited."
        )
    ),
    CENTRIFUGE(
        title = "Benchtop Centrifuge",
        maxTempRating = "60°C",
        capacity = "6 x 15 mL Tubes (Up to 4000 RPM)",
        compatibility = "Sealed polymers & tempered glass tubes",
        description = "Rotational apparatus applying high centrifugal acceleration to rapidly separate precipitates and dense phases from liquid suspensions.",
        sopNotes = listOf(
            "Always balance tubes in opposing pairs with equal mass and volume.",
            "Never open rotor lid until rotation has come to a complete stop.",
            "Wipe clean immediately if tube leakage occurs."
        )
    ),
    TITRATION_BURET(
        title = "Titration Buret",
        maxTempRating = "100°C",
        capacity = "50 mL (0.05 mL Divisions)",
        compatibility = "PTFE Stopcock & Class-A Volumetric Glass",
        description = "Precision graduated vertical glass tube with stopcock at bottom, delivering strictly calibrated aliquot volumes for acid-base titration.",
        sopNotes = listOf(
            "Rinse buret with titrant solution prior to final filling.",
            "Ensure air bubble is removed from stopcock tip before initial volume recording.",
            "Read meniscus at eye level against white background card."
        )
    ),
    CONDENSER(
        title = "Liebig Condenser",
        maxTempRating = "400°C",
        capacity = "Continuous vapor throughput",
        compatibility = "Dual-jacketed borosilicate glass",
        description = "Laboratory cooling apparatus used in distillation and reflux, condensing hot vapors back into liquid using circulating coolant water.",
        sopNotes = listOf(
            "Connect cooling water hose: inlet at bottom, outlet at top to prevent air pockets.",
            "Ensure vapor seal joints are lubricated with silicone grease.",
            "Do not allow cooling water to run dry during distillation."
        )
    ),
    EVAPORATING_DISH(
        title = "Evaporating Dish",
        maxTempRating = "800°C",
        capacity = "100 mL",
        compatibility = "Glazed chemical porcelain",
        description = "Shallow porcelain basin with pouring spout, designed for evaporating excess solvent or moisture from solute solutions under heat.",
        sopNotes = listOf(
            "Avoid rapid cold quenching directly from high heat to prevent thermal cracking.",
            "Use watch glass cover if boiling causes splattering.",
            "Allow to cool in a desiccator for gravimetric analysis."
        )
    ),
    CRUCIBLE(
        title = "Crucible & Lid",
        maxTempRating = "1200°C",
        capacity = "30 mL",
        compatibility = "High-purity sintered porcelain / alumina",
        description = "High-temperature ceramic cup and fitted lid capable of withstanding extreme calcination and thermal decomposition reactions.",
        sopNotes = listOf(
            "Heat initially gently to drive off residual ceramic moisture.",
            "Always transfer using stainless steel crucible tongs.",
            "Leave lid slightly ajar if atmospheric oxidation is intended."
        )
    ),
    ELECTRODES(
        title = "Electrolysis Cell",
        maxTempRating = "90°C",
        capacity = "Dual inert cathode & anode poles",
        compatibility = "Platinum / High-density graphite electrodes",
        description = "Direct current electrochemical cell driving non-spontaneous redox reactions, splitting compounds into elemental gases or plating metals.",
        sopNotes = listOf(
            "Verify DC power supply polarity (Cathode = Reduction, Anode = Oxidation).",
            "Collect evolved hydrogen and oxygen gases in inverted graduated tubes.",
            "Ensure electrolyte salt or acid conductivity is present."
        )
    );

    val maxSafeTempC: Double
        get() = when (this) {
            BEAKER -> 500.0
            ERLENMEYER_FLASK -> 500.0
            TEST_TUBE -> 450.0
            CRUCIBLE -> 1500.0
            EVAPORATING_DISH -> 800.0
            BUNSEN_BURNER -> 1500.0
            CONDENSER -> 400.0
            TITRATION_BURET -> 100.0
            ELECTRODES -> 95.0
            CENTRIFUGE -> 60.0
        }

    val thermalCrackTempC: Double
        get() = when (this) {
            CRUCIBLE -> 1600.0
            else -> maxSafeTempC + 100.0
        }

    val maxSafePressureAtm: Double
        get() = when (this) {
            BEAKER -> 2.5
            ERLENMEYER_FLASK -> 3.0
            TEST_TUBE -> 3.5
            CRUCIBLE -> 35.0
            CONDENSER -> 3.0
            else -> 1.5
        }

    val rupturePressureAtm: Double
        get() = when (this) {
            CRUCIBLE -> 50.0
            else -> maxSafePressureAtm * 1.8
        }
}

data class Chemical(
    val id: String,
    val name: String,
    val formula: String,
    val molarMass: Double,
    val category: ChemicalCategory,
    val colorHex: Long,
    val physicalState: String,
    val ph: Double,
    val hazards: List<HazardSymbol>,
    val discoveryHint: String,
    val isPreUnlocked: Boolean = false,
    val description: String,
    val atomicNumber: Int? = null,
    val symbol: String? = null,
    val period: Int? = null,
    val group: Int? = null,
    val elementSeries: String? = null,
    val meltingPointC: Double = when {
        physicalState.contains("Gas", ignoreCase = true) -> -180.0
        physicalState.contains("Solid", ignoreCase = true) || physicalState.contains("Precipitate", ignoreCase = true) -> 550.0
        else -> 0.0
    },
    val boilingPointC: Double = when {
        physicalState.contains("Gas", ignoreCase = true) -> -50.0
        physicalState.contains("Solid", ignoreCase = true) || physicalState.contains("Precipitate", ignoreCase = true) -> 1200.0
        else -> 100.0
    }
)

data class Reaction(
    val id: String,
    val reactantIds: Set<String>,
    val productIds: List<String>,
    val equation: String,
    val requiredTool: LabToolType? = null,
    val minTemp: Double = 20.0,
    val maxTemp: Double = 2000.0,
    val requiresElectricity: Boolean = false,
    val requiresCentrifuge: Boolean = false,
    val isExothermic: Boolean = true,
    val tempChange: Double = 20.0,
    val resultingPh: Double = 7.0,
    val resultingColor: Long = 0xFF38BDF8,
    val observation: String
)
