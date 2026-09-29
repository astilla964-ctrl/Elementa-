package com.example.data.model

object ChemicalCatalog {

    val BASE_COMPOUNDS: List<Chemical> = listOf(
        Chemical(
            id = "H2O",
            name = "Water",
            formula = "H₂O",
            molarMass = 18.015,
            category = ChemicalCategory.GAS,
            colorHex = 0xAA38BDF8,
            physicalState = "Liquid",
            ph = 7.0,
            hazards = emptyList(),
            discoveryHint = "The universal solvent, available at your fingertips.",
            isPreUnlocked = true,
            description = "Fundamental polar solvent essential for organic life and aqueous chemistry solutions."
        ),
        Chemical(
            id = "HCl",
            name = "Hydrochloric Acid",
            formula = "HCl",
            molarMass = 36.461,
            category = ChemicalCategory.ACID,
            colorHex = 0xCCFDE047,
            physicalState = "Aqueous Solution",
            ph = 1.0,
            hazards = listOf(HazardSymbol.CORROSIVE, HazardSymbol.TOXIC),
            discoveryHint = "Pungent, highly acidic aqueous solution of hydrogen chloride gas.",
            isPreUnlocked = true,
            description = "Pungent strong monoprotic mineral acid with immense proton donor capacity."
        ),
        Chemical(
            id = "NaOH",
            name = "Sodium Hydroxide",
            formula = "NaOH",
            molarMass = 39.997,
            category = ChemicalCategory.BASE,
            colorHex = 0xCC93C5FD,
            physicalState = "Aqueous Solution",
            ph = 13.5,
            hazards = listOf(HazardSymbol.CORROSIVE),
            discoveryHint = "Caustic soda; powerful alkaline base capable of dissolving fats and amphoteric metals.",
            isPreUnlocked = true,
            description = "Strong caustic base that readily dissociates into hydroxide ions, raising pH to near 14."
        ),
        Chemical(
            id = "CaCO3",
            name = "Calcium Carbonate",
            formula = "CaCO₃",
            molarMass = 100.086,
            category = ChemicalCategory.SALT,
            colorHex = 0xFFF1F5F9,
            physicalState = "Solid (Marble Chips)",
            ph = 8.5,
            hazards = emptyList(),
            discoveryHint = "White mineral precipitate found in limestone, eggshells, and marble.",
            isPreUnlocked = true,
            description = "Insoluble calcium salt that effervesces briskly upon exposure to acidic proton sources."
        ),
        Chemical(
            id = "CO2",
            name = "Carbon Dioxide",
            formula = "CO₂",
            molarMass = 44.010,
            category = ChemicalCategory.GAS,
            colorHex = 0x22F8FAFC,
            physicalState = "Gas",
            ph = 5.5,
            hazards = listOf(HazardSymbol.COMPRESSED_GAS),
            discoveryHint = "Formed by complete combustion of Carbon in the presence of Oxygen.",
            isPreUnlocked = false,
            description = "Dense non-flammable gas responsible for greenhouse effects and carbonated fizzy effervescence."
        ),
        Chemical(
            id = "FeS",
            name = "Iron(II) Sulfide",
            formula = "FeS",
            molarMass = 87.910,
            category = ChemicalCategory.SALT,
            colorHex = 0xFF1E293B,
            physicalState = "Solid (Precipitate)",
            ph = 7.0,
            hazards = listOf(HazardSymbol.IRRITANT),
            discoveryHint = "Formed by strongly heating metallic Iron filings mixed with powdered Sulfur.",
            isPreUnlocked = false,
            description = "Black inorganic crystalline compound formed in an intensely exothermic direct combination synthesis."
        ),
        Chemical(
            id = "NaCl",
            name = "Sodium Chloride",
            formula = "NaCl",
            molarMass = 58.443,
            category = ChemicalCategory.SALT,
            colorHex = 0xEEF8FAFC,
            physicalState = "Solid / Aqueous",
            ph = 7.0,
            hazards = emptyList(),
            discoveryHint = "Synthesized by stoichiometric acid-base neutralization of Hydrochloric Acid with Sodium Hydroxide.",
            isPreUnlocked = false,
            description = "Common edible table salt; prototypical face-centered cubic ionic halite lattice."
        ),
        Chemical(
            id = "CaCl2",
            name = "Calcium Chloride",
            formula = "CaCl₂",
            molarMass = 110.984,
            category = ChemicalCategory.SALT,
            colorHex = 0xCCD1D5DB,
            physicalState = "Aqueous / Solid",
            ph = 7.2,
            hazards = listOf(HazardSymbol.IRRITANT),
            discoveryHint = "Produced by reacting Calcium Carbonate chips with Hydrochloric Acid.",
            isPreUnlocked = false,
            description = "Highly deliquescent calcium salt releasing notable heat of hydration upon dissolution."
        ),
        Chemical(
            id = "CaO",
            name = "Calcium Oxide (Quicklime)",
            formula = "CaO",
            molarMass = 56.077,
            category = ChemicalCategory.OXIDE,
            colorHex = 0xFFE2E8F0,
            physicalState = "Solid (White Powder)",
            ph = 12.5,
            hazards = listOf(HazardSymbol.CORROSIVE, HazardSymbol.IRRITANT),
            discoveryHint = "Calcined from Calcium Carbonate at extreme temperature (> 800°C) inside a Crucible.",
            isPreUnlocked = false,
            description = "Caustic alkaline white powder that slakes violently with water in a steam-releasing hydration."
        ),
        Chemical(
            id = "NH3",
            name = "Ammonia",
            formula = "NH₃",
            molarMass = 17.031,
            category = ChemicalCategory.BASE,
            colorHex = 0x44A5F3FC,
            physicalState = "Gas / Alkaline Solution",
            ph = 11.2,
            hazards = listOf(HazardSymbol.CORROSIVE, HazardSymbol.TOXIC),
            discoveryHint = "Synthesized via Haber equilibrium combining Nitrogen and Hydrogen gas over an Iron catalyst.",
            isPreUnlocked = false,
            description = "Pungent choking alkaline gas vital for agricultural fertilizers and nitrogenous derivatives."
        ),
        Chemical(
            id = "SO2",
            name = "Sulfur Dioxide",
            formula = "SO₂",
            molarMass = 64.066,
            category = ChemicalCategory.GAS,
            colorHex = 0x55FEF08A,
            physicalState = "Gas",
            ph = 3.0,
            hazards = listOf(HazardSymbol.TOXIC, HazardSymbol.CORROSIVE),
            discoveryHint = "Formed by igniting yellow Sulfur powder in an Oxygen-rich atmosphere with flame.",
            isPreUnlocked = false,
            description = "Pungent suffocating acid gas that dissolves in aqueous moisture to form sulfurous precursors."
        ),
        Chemical(
            id = "H2SO4",
            name = "Sulfuric Acid",
            formula = "H₂SO₄",
            molarMass = 98.079,
            category = ChemicalCategory.ACID,
            colorHex = 0xDDE0F2FE,
            physicalState = "Oily Liquid",
            ph = 0.5,
            hazards = listOf(HazardSymbol.CORROSIVE, HazardSymbol.OXIDIZING),
            discoveryHint = "Synthesized by combining Sulfur Dioxide, Oxygen, and Water under elevated temperature.",
            isPreUnlocked = false,
            description = "King of Chemicals; dense viscous diprotic mineral acid with tremendous dehydrating appetite."
        ),
        Chemical(
            id = "CuO",
            name = "Copper(II) Oxide",
            formula = "CuO",
            molarMass = 79.545,
            category = ChemicalCategory.OXIDE,
            colorHex = 0xFF0F172A,
            physicalState = "Solid (Black Powder)",
            ph = 7.0,
            hazards = listOf(HazardSymbol.IRRITANT),
            discoveryHint = "Formed by heating Copper turnings in direct contact with Oxygen in a Crucible or Burner.",
            isPreUnlocked = false,
            description = "Black amphoteric transition metal oxide commonly used in ceramics, glazes, and battery cathodes."
        ),
        Chemical(
            id = "Cu_OH_2",
            name = "Copper(II) Hydroxide",
            formula = "Cu(OH)₂",
            molarMass = 97.561,
            category = ChemicalCategory.BASE,
            colorHex = 0xFF0284C7,
            physicalState = "Solid (Gelatinous Precipitate)",
            ph = 9.0,
            hazards = listOf(HazardSymbol.IRRITANT),
            discoveryHint = "Precipitated when Sodium Hydroxide reacts with dissolved Copper(II) salts.",
            isPreUnlocked = false,
            description = "Striking pale-blue gelatinous precipitate that dehydrates into black CuO upon heating."
        ),
        Chemical(
            id = "CuCl2",
            name = "Copper(II) Chloride",
            formula = "CuCl₂",
            molarMass = 134.450,
            category = ChemicalCategory.SALT,
            colorHex = 0xDD0D9488,
            physicalState = "Aqueous Solution",
            ph = 4.0,
            hazards = listOf(HazardSymbol.IRRITANT, HazardSymbol.TOXIC),
            discoveryHint = "Formed by dissolving Copper(II) Oxide in concentrated Hydrochloric Acid.",
            isPreUnlocked = false,
            description = "Teal-green coordination compound imparting a brilliant turquoise hue to solutions."
        ),
        Chemical(
            id = "MgO",
            name = "Magnesium Oxide",
            formula = "MgO",
            molarMass = 40.304,
            category = ChemicalCategory.OXIDE,
            colorHex = 0xFFF8FAFC,
            physicalState = "Solid (White Ash)",
            ph = 10.3,
            hazards = listOf(HazardSymbol.IRRITANT),
            discoveryHint = "Formed by igniting Magnesium ribbon in air with a blinding white flame.",
            isPreUnlocked = false,
            description = "Refractory white mineral ash with exceptional thermal insulation and refractory properties."
        ),
        Chemical(
            id = "ZnCl2",
            name = "Zinc Chloride",
            formula = "ZnCl₂",
            molarMass = 136.315,
            category = ChemicalCategory.SALT,
            colorHex = 0xCCD1D5DB,
            physicalState = "Aqueous Solution",
            ph = 4.0,
            hazards = listOf(HazardSymbol.CORROSIVE),
            discoveryHint = "Produced by reacting metallic Zinc with Hydrochloric Acid.",
            isPreUnlocked = false,
            description = "Hygroscopic salt solution commonly used as a soldering flux and textile activator."
        ),
        Chemical(
            id = "KOH",
            name = "Potassium Hydroxide",
            formula = "KOH",
            molarMass = 56.106,
            category = ChemicalCategory.BASE,
            colorHex = 0xCCBAE6FD,
            physicalState = "Aqueous Solution",
            ph = 13.8,
            hazards = listOf(HazardSymbol.CORROSIVE),
            discoveryHint = "Formed by the reaction of Potassium metal with Water accompanied by a lilac flame.",
            isPreUnlocked = false,
            description = "Potent caustic potash base extensively utilized in alkaline batteries and soft soaps."
        ),
        Chemical(
            id = "Al2O3",
            name = "Aluminum Oxide (Alumina)",
            formula = "Al₂O₃",
            molarMass = 101.960,
            category = ChemicalCategory.OXIDE,
            colorHex = 0xFFE2E8F0,
            physicalState = "Solid (Hard Ceramic)",
            ph = 7.0,
            hazards = emptyList(),
            discoveryHint = "Formed by intense exothermic oxidation or thermite reaction of Aluminum powder.",
            isPreUnlocked = false,
            description = "Extremely hard amphoteric ceramic oxide; natural base crystal for rubies and sapphires."
        ),
        Chemical(
            id = "CuSO4",
            name = "Copper(II) Sulfate",
            formula = "CuSO₄",
            molarMass = 159.609,
            category = ChemicalCategory.SALT,
            colorHex = 0xFF0284C7,
            physicalState = "Aqueous / Blue Crystals",
            ph = 4.5,
            hazards = listOf(HazardSymbol.IRRITANT, HazardSymbol.TOXIC),
            discoveryHint = "Vibrant azure blue vitriol salt solution, classic electroplating source.",
            isPreUnlocked = true,
            description = "Standard blue vitriol crystal forming deep azure aqueous solutions with copper ions."
        ),
        Chemical(
            id = "FeSO4",
            name = "Iron(II) Sulfate",
            formula = "FeSO₄",
            molarMass = 151.908,
            category = ChemicalCategory.SALT,
            colorHex = 0xFF86EFAC,
            physicalState = "Aqueous (Pale Green)",
            ph = 4.8,
            hazards = listOf(HazardSymbol.IRRITANT),
            discoveryHint = "Formed when metallic Iron displaces copper from Copper(II) Sulfate.",
            isPreUnlocked = false,
            description = "Green vitriol salt imparting a pale emerald-green hue to aqueous solutions."
        ),
        Chemical(
            id = "AgNO3",
            name = "Silver Nitrate",
            formula = "AgNO₃",
            molarMass = 169.873,
            category = ChemicalCategory.SALT,
            colorHex = 0xCCE2E8F0,
            physicalState = "Aqueous Solution",
            ph = 6.0,
            hazards = listOf(HazardSymbol.OXIDIZING, HazardSymbol.CORROSIVE),
            discoveryHint = "Soluble silver salt reagent for halide precipitation and silver mirror reactions.",
            isPreUnlocked = true,
            description = "Light-sensitive clear silver solution that stains organic materials black with reduced metallic silver."
        ),
        Chemical(
            id = "AgCl",
            name = "Silver Chloride",
            formula = "AgCl",
            molarMass = 143.321,
            category = ChemicalCategory.SALT,
            colorHex = 0xFFF8FAFC,
            physicalState = "Solid (Curdy White Precipitate)",
            ph = 7.0,
            hazards = listOf(HazardSymbol.IRRITANT),
            discoveryHint = "Precipitates immediately when Silver Nitrate encounters soluble chlorides (NaCl, HCl).",
            isPreUnlocked = false,
            description = "Curdy white insoluble salt that darkens into purple-black metallic silver under ultraviolet photons."
        ),
        Chemical(
            id = "Pb_NO3_2",
            name = "Lead(II) Nitrate",
            formula = "Pb(NO₃)₂",
            molarMass = 331.200,
            category = ChemicalCategory.SALT,
            colorHex = 0xCCE2E8F0,
            physicalState = "Aqueous Solution",
            ph = 4.5,
            hazards = listOf(HazardSymbol.TOXIC, HazardSymbol.OXIDIZING),
            discoveryHint = "Soluble lead precursor solution famous for Golden Rain precipitation experiments.",
            isPreUnlocked = true,
            description = "Heavy metal nitrate salt providing divalent lead cations for qualitative analytical analysis."
        ),
        Chemical(
            id = "KI",
            name = "Potassium Iodide",
            formula = "KI",
            molarMass = 166.002,
            category = ChemicalCategory.SALT,
            colorHex = 0xCCF1F5F9,
            physicalState = "Aqueous Solution",
            ph = 7.0,
            hazards = listOf(HazardSymbol.IRRITANT),
            discoveryHint = "Colorless iodide donor salt reacting with lead to yield golden crystalline precipitates.",
            isPreUnlocked = true,
            description = "Essential potassium halide source used in iodometry, cloud seeding, and analytical testing."
        ),
        Chemical(
            id = "PbI2",
            name = "Lead(II) Iodide (Golden Rain)",
            formula = "PbI₂",
            molarMass = 461.010,
            category = ChemicalCategory.SALT,
            colorHex = 0xFFFACC15,
            physicalState = "Solid (Canary Yellow Flakes)",
            ph = 7.0,
            hazards = listOf(HazardSymbol.TOXIC),
            discoveryHint = "Synthesized in the legendary 'Golden Rain' reaction between Lead Nitrate and Potassium Iodide.",
            isPreUnlocked = false,
            description = "Brilliant golden canary-yellow crystalline flakes that glitter in water like a cascade of gold."
        ),
        Chemical(
            id = "H2O2",
            name = "Hydrogen Peroxide",
            formula = "H₂O₂",
            molarMass = 34.014,
            category = ChemicalCategory.OXIDE,
            colorHex = 0xCCE0F2FE,
            physicalState = "Aqueous Solution",
            ph = 5.0,
            hazards = listOf(HazardSymbol.OXIDIZING, HazardSymbol.CORROSIVE),
            discoveryHint = "Powerful reactive peroxide oxidizer decomposing into water and effervescent oxygen.",
            isPreUnlocked = true,
            description = "Unstable pale-blue liquid with single oxygen-oxygen bond, prone to vigorous catalyzed decomposition."
        ),
        Chemical(
            id = "MnO2",
            name = "Manganese Dioxide",
            formula = "MnO₂",
            molarMass = 86.936,
            category = ChemicalCategory.OXIDE,
            colorHex = 0xFF1E293B,
            physicalState = "Solid (Black Catalyst)",
            ph = 7.0,
            hazards = listOf(HazardSymbol.OXIDIZING),
            discoveryHint = "Insoluble black powder acting as an ultra-fast catalyst for peroxide breakdown.",
            isPreUnlocked = true,
            description = "Transition metal catalyst driving extreme decomposition of peroxides and chlorates."
        ),
        Chemical(
            id = "Fe2O3",
            name = "Iron(III) Oxide (Rust)",
            formula = "Fe₂O₃",
            molarMass = 159.687,
            category = ChemicalCategory.OXIDE,
            colorHex = 0xFF991B1B,
            physicalState = "Solid (Red Powder)",
            ph = 7.0,
            hazards = listOf(HazardSymbol.IRRITANT),
            discoveryHint = "Reddish-brown ferric oxide powder, active oxidant component of the Thermite mixture.",
            isPreUnlocked = true,
            description = "Ferric rust powder capable of being reduced to elemental liquid molten iron by aluminum."
        ),
        Chemical(
            id = "FeCl3",
            name = "Iron(III) Chloride",
            formula = "FeCl₃",
            molarMass = 162.204,
            category = ChemicalCategory.SALT,
            colorHex = 0xFFB45309,
            physicalState = "Aqueous (Orange-Brown)",
            ph = 2.0,
            hazards = listOf(HazardSymbol.CORROSIVE),
            discoveryHint = "Dark orange acidic solution used for PCB circuit etching and hydroxide precipitation.",
            isPreUnlocked = true,
            description = "Strong Lewis acid salt forming rich amber-brown solutions with pungent astringency."
        ),
        Chemical(
            id = "Fe_OH_3",
            name = "Iron(III) Hydroxide",
            formula = "Fe(OH)₃",
            molarMass = 106.867,
            category = ChemicalCategory.BASE,
            colorHex = 0xFF78350F,
            physicalState = "Solid (Rust-Brown Gel)",
            ph = 8.5,
            hazards = listOf(HazardSymbol.IRRITANT),
            discoveryHint = "Forms as a thick gelatinous rust-colored precipitate when Iron(III) Chloride reacts with Sodium Hydroxide.",
            isPreUnlocked = false,
            description = "Flocculent reddish-brown hydroxide precipitate separating cleanly from alkaline aqueous media."
        ),
        Chemical(
            id = "BaCl2",
            name = "Barium Chloride",
            formula = "BaCl₂",
            molarMass = 208.230,
            category = ChemicalCategory.SALT,
            colorHex = 0xCCF1F5F9,
            physicalState = "Aqueous Solution",
            ph = 6.8,
            hazards = listOf(HazardSymbol.TOXIC),
            discoveryHint = "Qualitative testing reagent identifying sulfate ions via dense white precipitate.",
            isPreUnlocked = true,
            description = "Toxic barium salt solution yielding instant milky insoluble sulfate complexes."
        ),
        Chemical(
            id = "BaSO4",
            name = "Barium Sulfate",
            formula = "BaSO₄",
            molarMass = 233.390,
            category = ChemicalCategory.SALT,
            colorHex = 0xFFFFFFFF,
            physicalState = "Solid (Milky White Precipitate)",
            ph = 7.0,
            hazards = emptyList(),
            discoveryHint = "Synthesized by combining Barium Chloride with Sulfuric Acid or soluble sulfates.",
            isPreUnlocked = false,
            description = "Remarkably insoluble radiopaque barium compound completely inert to stomach gastric acids."
        ),
        Chemical(
            id = "Ca_OH_2",
            name = "Calcium Hydroxide (Limewater)",
            formula = "Ca(OH)₂",
            molarMass = 74.093,
            category = ChemicalCategory.BASE,
            colorHex = 0xCCF1F5F9,
            physicalState = "Aqueous (Slaked Lime)",
            ph = 12.4,
            hazards = listOf(HazardSymbol.CORROSIVE),
            discoveryHint = "Produced by hydration/slaking of Quicklime (CaO) with excess Water.",
            isPreUnlocked = false,
            description = "Caustic slaked lime solution that turns turbidly milky in the presence of carbon dioxide."
        ),
        Chemical(
            id = "KClO3",
            name = "Potassium Chlorate",
            formula = "KClO₃",
            molarMass = 122.550,
            category = ChemicalCategory.SALT,
            colorHex = 0xFFF8FAFC,
            physicalState = "Solid (White Salt)",
            ph = 7.0,
            hazards = listOf(HazardSymbol.OXIDIZING, HazardSymbol.FLAMMABLE),
            discoveryHint = "Powerful thermal oxidizer decomposing into potassium chloride and gaseous oxygen.",
            isPreUnlocked = true,
            description = "High-energy chlorine oxyacid salt used in laboratory oxygen generation and match heads."
        ),
        Chemical(
            id = "KCl",
            name = "Potassium Chloride",
            formula = "KCl",
            molarMass = 74.551,
            category = ChemicalCategory.SALT,
            colorHex = 0xEEF8FAFC,
            physicalState = "Solid / Aqueous",
            ph = 7.0,
            hazards = emptyList(),
            discoveryHint = "Residual salt left after decomposing Potassium Chlorate or displacing iodide with chlorine.",
            isPreUnlocked = false,
            description = "Neutral metal halide salt common in mineral fertilizers and physiological saline solutions."
        ),
        Chemical(
            id = "H2O_GAS",
            name = "Steam (Water Vapor)",
            formula = "H₂O (g)",
            molarMass = 18.015,
            category = ChemicalCategory.GAS,
            colorHex = 0x55F1F5F9,
            physicalState = "Vapor",
            ph = 7.0,
            hazards = listOf(HazardSymbol.IRRITANT),
            discoveryHint = "Formed by heating liquid Water past its 100°C boiling point.",
            isPreUnlocked = false,
            description = "Energetic gaseous state of water capable of transferring massive latent heat of vaporization."
        ),
        Chemical(
            id = "CH4",
            name = "Methane",
            formula = "CH₄",
            molarMass = 16.043,
            category = ChemicalCategory.ORGANIC,
            colorHex = 0x22BAE6FD,
            physicalState = "Gas",
            ph = 7.0,
            hazards = listOf(HazardSymbol.FLAMMABLE, HazardSymbol.COMPRESSED_GAS),
            discoveryHint = "Simple hydrocarbon gas synthesized from Carbon and Hydrogen under high thermal pressure.",
            isPreUnlocked = false,
            description = "Primary component of natural gas; tetrahedral alkane with low boiling point (-161.5°C)."
        ),
        Chemical(
            id = "Organic_Compound_Alpha",
            name = "Organic Compound Alpha",
            formula = "C₈H₁₀N₄O₂ (Equiv)",
            molarMass = 194.190,
            category = ChemicalCategory.ORGANIC,
            colorHex = 0xCCF8FAFC,
            physicalState = "Solid (Crystalline)",
            ph = 6.9,
            hazards = listOf(HazardSymbol.IRRITANT),
            discoveryHint = "Synthesized through catalytic condensation of Carbon, Nitrogen, Hydrogen, and Oxygen.",
            isPreUnlocked = false,
            description = "Compliant educational organic heterocyclic analog for alkaloid research without restriction."
        ),
        Chemical(
            id = "Alkaloid_X",
            name = "Alkaloid X Complex",
            formula = "C₁₇H₂₁NO₄ (Equiv)",
            molarMass = 303.350,
            category = ChemicalCategory.ORGANIC,
            colorHex = 0xDDFAF5FF,
            physicalState = "Aqueous / Crystalline",
            ph = 8.1,
            hazards = listOf(HazardSymbol.TOXIC),
            discoveryHint = "Compliant bio-organic synthesis derived from complex carbon-nitrogen esterification.",
            isPreUnlocked = false,
            description = "Standardized organic tertiary amine model designed for safe classroom pharmacology experiments."
        )
    )

    // ALL 118 ELEMENTS + BASE COMPOUNDS + EXTENDED REACTION COMPOUNDS + 100 NEW REACTION COMPOUNDS
    val ALL_CHEMICALS: List<Chemical> = PeriodicTableData.ALL_118_ELEMENTS + BASE_COMPOUNDS + ExtendedChemicals.ALL_EXTENDED_COMPOUNDS + MoreExtendedChemicals.ALL_MORE_EXTENDED_COMPOUNDS

    val REACTIONS: List<Reaction> = listOf(
        // 1. Water Boiling to Steam
        Reaction(
            id = "H2O_BOIL",
            reactantIds = setOf("H2O"),
            productIds = listOf("H2O_GAS"),
            equation = "H₂O (l) + Heat (>100°C) → H₂O (g) ↑",
            minTemp = 100.0,
            isExothermic = false,
            tempChange = -5.0,
            resultingPh = 7.0,
            resultingColor = 0x33F8FAFC,
            observation = "Liquid boils vigorously with audible bubbling; plumes of dense steam vapor ascend."
        ),
        // 2. Hydrogen + Oxygen Combustion
        Reaction(
            id = "H2_O2_COMBUSTION",
            reactantIds = setOf("H", "O"),
            productIds = listOf("H2O"),
            equation = "2H₂ + O₂ → 2H₂O",
            minTemp = 80.0,
            isExothermic = true,
            tempChange = 120.0,
            resultingPh = 7.0,
            resultingColor = 0xAA38BDF8,
            observation = "Loud explosive pop ignition! Rapid exothermic fireball synthesizing distilled water droplets."
        ),
        // 3. Carbon Combustion to CO2
        Reaction(
            id = "C_O2_CO2",
            reactantIds = setOf("C", "O"),
            productIds = listOf("CO2"),
            equation = "C + O₂ + Heat → CO₂ ↑",
            minTemp = 300.0,
            isExothermic = true,
            tempChange = 85.0,
            resultingPh = 5.5,
            resultingColor = 0x22F8FAFC,
            observation = "Charcoal embers glow incandescent orange, consuming carbon and generating effervescent CO₂ gas."
        ),
        // 4. Iron + Sulfur Synthesis
        Reaction(
            id = "FE_S_FES",
            reactantIds = setOf("Fe", "S"),
            productIds = listOf("FeS"),
            equation = "Fe + S + Heat → FeS",
            minTemp = 250.0,
            isExothermic = true,
            tempChange = 90.0,
            resultingPh = 7.0,
            resultingColor = 0xFF1E293B,
            observation = "Sulfur melts into a red liquid, then ignites with iron filings in a bright orange flash, forming black FeS solid."
        ),
        // 5. Sodium + Water (Violent)
        Reaction(
            id = "NA_H2O",
            reactantIds = setOf("Na", "H2O"),
            productIds = listOf("NaOH", "H"),
            equation = "2Na + 2H₂O → 2NaOH + H₂ ↑",
            minTemp = 20.0,
            isExothermic = true,
            tempChange = 65.0,
            resultingPh = 13.8,
            resultingColor = 0xCC93C5FD,
            observation = "Sodium skitters furiously over the water meniscus, hissing and generating flammable hydrogen gas with caustic NaOH."
        ),
        // 6. Potassium + Water (Lilac Flame)
        Reaction(
            id = "K_H2O",
            reactantIds = setOf("K", "H2O"),
            productIds = listOf("KOH", "H"),
            equation = "2K + 2H₂O → 2KOH + H₂ ↑ (Lilac Flame)",
            minTemp = 20.0,
            isExothermic = true,
            tempChange = 85.0,
            resultingPh = 13.8,
            resultingColor = 0xCCBAE6FD,
            observation = "Potassium ignites instantly with a brilliant violet/lilac flame, exploding softly into caustic KOH."
        ),
        // 7. Neutralization: HCl + NaOH -> NaCl + H2O
        Reaction(
            id = "HCL_NAOH",
            reactantIds = setOf("HCl", "NaOH"),
            productIds = listOf("NaCl", "H2O"),
            equation = "HCl + NaOH → NaCl + H₂O",
            minTemp = 20.0,
            isExothermic = true,
            tempChange = 28.0,
            resultingPh = 7.0,
            resultingColor = 0xEEF8FAFC,
            observation = "Perfect stoichiometric neutralization! Strong warmth release; solution pH shifts to neutral 7.0."
        ),
        // 8. Calcium Carbonate + HCl -> CaCl2 + H2O + CO2
        Reaction(
            id = "CACO3_HCL",
            reactantIds = setOf("CaCO3", "HCl"),
            productIds = listOf("CaCl2", "H2O", "CO2"),
            equation = "CaCO₃ + 2HCl → CaCl₂ + H₂O + CO₂ ↑",
            minTemp = 20.0,
            isExothermic = false,
            tempChange = 8.0,
            resultingPh = 6.8,
            resultingColor = 0xCCD1D5DB,
            observation = "Vigorous fizzing and foaming! Marble chips dissolve rapidly releasing streams of CO₂ bubbles."
        ),
        // 9. Calcium Carbonate Calcination: CaCO3 -> CaO + CO2
        Reaction(
            id = "CACO3_CALCINATION",
            reactantIds = setOf("CaCO3"),
            productIds = listOf("CaO", "CO2"),
            equation = "CaCO₃ + Extreme Heat (>800°C) → CaO + CO₂ ↑",
            minTemp = 800.0,
            isExothermic = false,
            tempChange = -15.0,
            resultingPh = 12.0,
            resultingColor = 0xFFE2E8F0,
            observation = "Thermal decomposition in crucible! Chalky chips decompose into glowing caustic Quicklime (CaO)."
        ),
        // 10. Magnesium Ribbon Combustion: Mg + O -> MgO
        Reaction(
            id = "MG_O2_MGO",
            reactantIds = setOf("Mg", "O"),
            productIds = listOf("MgO"),
            equation = "2Mg + O₂ + Heat (>500°C) → 2MgO",
            minTemp = 450.0,
            isExothermic = true,
            tempChange = 160.0,
            resultingPh = 10.3,
            resultingColor = 0xFFF8FAFC,
            observation = "Intense, blinding white pyrotechnic flare! Magnesium ribbon burns furiously into pure white refractory MgO ash."
        ),
        // 11. Zinc + HCl Displacement: Zn + HCl -> ZnCl2 + H2
        Reaction(
            id = "ZN_HCL",
            reactantIds = setOf("Zn", "HCl"),
            productIds = listOf("ZnCl2", "H"),
            equation = "Zn + 2HCl → ZnCl₂ + H₂ ↑",
            minTemp = 20.0,
            isExothermic = true,
            tempChange = 25.0,
            resultingPh = 4.0,
            resultingColor = 0xCCD1D5DB,
            observation = "Rapid steady stream of hydrogen gas bubbles erupts from zinc granules as zinc chloride dissolves."
        ),
        // 12. Aluminum Oxidation: Al + O -> Al2O3
        Reaction(
            id = "AL_O2_AL2O3",
            reactantIds = setOf("Al", "O"),
            productIds = listOf("Al2O3"),
            equation = "4Al + 3O₂ + Heat (>600°C) → 2Al₂O₃",
            minTemp = 600.0,
            isExothermic = true,
            tempChange = 140.0,
            resultingPh = 7.0,
            resultingColor = 0xFFE2E8F0,
            observation = "Fierce incandescent glow! Aluminum oxidizes into diamond-hard crystalline Al₂O₃ alumina ceramic."
        ),
        // 13. Haber Process: N + H -> NH3
        Reaction(
            id = "HABER_NH3",
            reactantIds = setOf("N", "H", "Fe"),
            productIds = listOf("NH3", "Fe"),
            equation = "N₂ + 3H₂ [Fe Cat, >400°C] → 2NH₃",
            minTemp = 400.0,
            isExothermic = true,
            tempChange = 40.0,
            resultingPh = 11.2,
            resultingColor = 0x44A5F3FC,
            observation = "Haber catalytic equilibrium reached! Pungent alkaline ammonia fumes condense at flask outlet."
        ),
        // 14. Sulfur Combustion: S + O -> SO2
        Reaction(
            id = "S_O2_SO2",
            reactantIds = setOf("S", "O"),
            productIds = listOf("SO2"),
            equation = "S + O₂ + Heat (>200°C) → SO₂ ↑",
            minTemp = 200.0,
            isExothermic = true,
            tempChange = 55.0,
            resultingPh = 3.0,
            resultingColor = 0x55FEF08A,
            observation = "Sulfur burns with an ethereal blue flame, producing sharp, choking sulfur dioxide gas."
        ),
        // 15. Contact Precursor: SO2 + O + H2O -> H2SO4
        Reaction(
            id = "SO2_O2_H2O",
            reactantIds = setOf("SO2", "O", "H2O"),
            productIds = listOf("H2SO4"),
            equation = "2SO₂ + O₂ + 2H₂O + Heat → 2H₂SO₄",
            minTemp = 150.0,
            isExothermic = true,
            tempChange = 75.0,
            resultingPh = 0.5,
            resultingColor = 0xDDE0F2FE,
            observation = "Dense white acid mist forms and condenses into heavy oily sulfuric acid; solution turns fiercely acidic."
        ),
        // 16. Copper Oxidation: Cu + O -> CuO
        Reaction(
            id = "CU_O2_CUO",
            reactantIds = setOf("Cu", "O"),
            productIds = listOf("CuO"),
            equation = "2Cu + O₂ + Heat (>300°C) → 2CuO",
            minTemp = 300.0,
            isExothermic = true,
            tempChange = 35.0,
            resultingPh = 7.0,
            resultingColor = 0xFF0F172A,
            observation = "Lustrous reddish copper turnings darken under flame, coating into a matte velvety black layer of CuO."
        ),
        // 17. CuO + HCl -> CuCl2 + H2O
        Reaction(
            id = "CUO_HCL",
            reactantIds = setOf("CuO", "HCl"),
            productIds = listOf("CuCl2", "H2O"),
            equation = "CuO + 2HCl → CuCl₂ + H₂O",
            minTemp = 25.0,
            isExothermic = true,
            tempChange = 22.0,
            resultingPh = 4.0,
            resultingColor = 0xDD0D9488,
            observation = "Black oxide powder dissolves completely, transforming the solution into a stunning emerald-teal liquid."
        ),
        // 18. CuCl2 + NaOH -> Cu(OH)2 + NaCl
        Reaction(
            id = "CUCL2_NAOH",
            reactantIds = setOf("CuCl2", "NaOH"),
            productIds = listOf("Cu_OH_2", "NaCl"),
            equation = "CuCl₂ + 2NaOH → Cu(OH)₂ ↓ + 2NaCl",
            minTemp = 20.0,
            isExothermic = true,
            tempChange = 18.0,
            resultingPh = 8.5,
            resultingColor = 0xFF0284C7,
            observation = "Dramatic qualitative precipitation! Brilliant azure blue gelatinous Cu(OH)₂ flock instantly separates."
        ),
        // 19. Water Electrolysis: H2O -> 2H + O
        Reaction(
            id = "WATER_ELECTROLYSIS",
            reactantIds = setOf("H2O"),
            productIds = listOf("H", "O"),
            equation = "2H₂O + Electrolysis ⚡ → 2H₂ (Cathode) + O₂ (Anode)",
            requiresElectricity = true,
            minTemp = 15.0,
            isExothermic = false,
            tempChange = 4.0,
            resultingPh = 7.0,
            resultingColor = 0xAA38BDF8,
            observation = "Electric current splits water! Streams of microbubbles ascend briskly from both electrode poles."
        ),
        // 20. Methane Synthesis: C + H
        Reaction(
            id = "C_H2_CH4",
            reactantIds = setOf("C", "H"),
            productIds = listOf("CH4"),
            equation = "C + 2H₂ + Heat (>450°C) → CH₄ ↑",
            minTemp = 450.0,
            isExothermic = true,
            tempChange = 45.0,
            resultingPh = 7.0,
            resultingColor = 0x22BAE6FD,
            observation = "Carbon catalyst hydrogenated under elevated thermal pressure yielding light flammable methane."
        ),
        // 21. Compliant Organic Synthesis: Organic Compound Alpha
        Reaction(
            id = "ORGANIC_ALPHA_SYNTHESIS",
            reactantIds = setOf("C", "N", "H", "O"),
            productIds = listOf("Organic_Compound_Alpha"),
            equation = "8C + 2N₂ + 5H₂ + O₂ [Heat & Catalyst] → C₈H₁₀N₄O₂",
            minTemp = 220.0,
            isExothermic = true,
            tempChange = 30.0,
            resultingPh = 6.9,
            resultingColor = 0xCCF8FAFC,
            observation = "Multi-component organic condensation produces pure glistening white needles of Organic Compound Alpha."
        ),
        // 22. Compliant Alkaloid X Synthesis
        Reaction(
            id = "ALKALOID_X_SYNTHESIS",
            reactantIds = setOf("Organic_Compound_Alpha", "HCl"),
            productIds = listOf("Alkaloid_X"),
            equation = "Organic Compound Alpha + HCl → Alkaloid X Hydrochloride Complex",
            minTemp = 35.0,
            isExothermic = true,
            tempChange = 12.0,
            resultingPh = 5.2,
            resultingColor = 0xDDFAF5FF,
            observation = "Acidic salting-out crystallizes the Alkaloid X complex in compliant laboratory demonstration."
        ),
        // 23. Thermite Reaction (Extreme Exothermic)
        Reaction(
            id = "THERMITE_REACTION",
            reactantIds = setOf("Al", "Fe2O3"),
            productIds = listOf("Al2O3", "Fe"),
            equation = "2Al + Fe₂O₃ + Heat (>800°C) → Al₂O₃ + 2Fe (Molten Iron)",
            minTemp = 800.0,
            isExothermic = true,
            tempChange = 400.0,
            resultingPh = 7.0,
            resultingColor = 0xFFF97316,
            observation = "Blinding incandescent shower of sparks! Intensely exothermic thermite reaction generates liquid molten iron at over 2500°C."
        ),
        // 24. Golden Rain Reaction
        Reaction(
            id = "GOLDEN_RAIN",
            reactantIds = setOf("Pb_NO3_2", "KI"),
            productIds = listOf("PbI2"),
            equation = "Pb(NO₃)₂ + 2KI → PbI₂ ↓ (Golden Rain) + 2KNO₃",
            minTemp = 20.0,
            isExothermic = false,
            tempChange = 2.0,
            resultingPh = 6.0,
            resultingColor = 0xFFFACC15,
            observation = "Magnificent Golden Rain! Brilliant canary-yellow hexagonal PbI₂ crystalline flakes glitter and cascade through the solution."
        ),
        // 25. Silver Crystal Tree Displacement
        Reaction(
            id = "SILVER_TREE",
            reactantIds = setOf("Cu", "AgNO3"),
            productIds = listOf("Ag"),
            equation = "Cu + 2AgNO₃ → Cu(NO₃)₂ (Azure) + 2Ag ↓ (Silver Crystals)",
            minTemp = 20.0,
            isExothermic = true,
            tempChange = 14.0,
            resultingPh = 5.5,
            resultingColor = 0xFF0284C7,
            observation = "Delicate dendritic needles of pure metallic silver grow arbor-like on copper; solution shifts into rich azure-blue."
        ),
        // 26. Iron Displacement in Copper Sulfate
        Reaction(
            id = "FE_CUSO4_DISPLACEMENT",
            reactantIds = setOf("Fe", "CuSO4"),
            productIds = listOf("FeSO4", "Cu"),
            equation = "Fe + CuSO₄ → FeSO₄ + Cu ↓ (Salmon Metal)",
            minTemp = 20.0,
            isExothermic = true,
            tempChange = 16.0,
            resultingPh = 4.8,
            resultingColor = 0xFF86EFAC,
            observation = "Iron filings plate with a bright reddish-salmon layer of metallic copper; deep blue solution fades to pale emerald Fe²⁺."
        ),
        // 27. Silver Chloride Precipitation
        Reaction(
            id = "AGCL_PRECIPITATION",
            reactantIds = setOf("AgNO3", "NaCl"),
            productIds = listOf("AgCl"),
            equation = "AgNO₃ + NaCl → AgCl ↓ + NaNO₃",
            minTemp = 20.0,
            isExothermic = false,
            tempChange = 3.0,
            resultingPh = 7.0,
            resultingColor = 0xFFF8FAFC,
            observation = "Instant formation of thick, curdy white insoluble silver chloride precipitate."
        ),
        // 28. Catalytic Elephant Toothpaste (Peroxide Decomposition)
        Reaction(
            id = "PEROXIDE_CATALYTIC_DECOMP",
            reactantIds = setOf("H2O2", "MnO2"),
            productIds = listOf("H2O", "O"),
            equation = "2H₂O₂ [MnO₂ Cat] → 2H₂O + O₂ ↑ (Elephant Toothpaste)",
            minTemp = 20.0,
            isExothermic = true,
            tempChange = 68.0,
            resultingPh = 7.0,
            resultingColor = 0xAA38BDF8,
            observation = "Violent foaming thermal eruption! Rapid catalytic decomposition releases geysers of hot oxygen gas and billowing steam."
        ),
        // 29. Slaking of Quicklime
        Reaction(
            id = "LIME_SLAKING",
            reactantIds = setOf("CaO", "H2O"),
            productIds = listOf("Ca_OH_2"),
            equation = "CaO + H₂O → Ca(OH)₂ (Slaked Limewater)",
            minTemp = 20.0,
            isExothermic = true,
            tempChange = 80.0,
            resultingPh = 12.4,
            resultingColor = 0xCCF1F5F9,
            observation = "Furious boiling hydration! Quicklime swells, cracking and releasing clouds of steam into caustic alkaline limewater."
        ),
        // 30. Barium Sulfate Gravimetric Precipitation
        Reaction(
            id = "BASO4_PRECIPITATION",
            reactantIds = setOf("BaCl2", "H2SO4"),
            productIds = listOf("BaSO4", "HCl"),
            equation = "BaCl₂ + H₂SO₄ → BaSO₄ ↓ + 2HCl",
            minTemp = 20.0,
            isExothermic = false,
            tempChange = 4.0,
            resultingPh = 1.0,
            resultingColor = 0xFFFFFFFF,
            observation = "Heavy milky white insoluble precipitate of barium sulfate separates with dramatic opacity."
        ),
        // 31. Rust Hydroxide Precipitation
        Reaction(
            id = "FE_OH_3_RUST",
            reactantIds = setOf("FeCl3", "NaOH"),
            productIds = listOf("Fe_OH_3", "NaCl"),
            equation = "FeCl₃ + 3NaOH → Fe(OH)₃ ↓ + 3NaCl",
            minTemp = 20.0,
            isExothermic = true,
            tempChange = 15.0,
            resultingPh = 8.5,
            resultingColor = 0xFF78350F,
            observation = "Thick, flocculent rust-brown ferric hydroxide gel instantly precipitates and settles at vessel floor."
        ),
        // 32. Halogen Displacement: Chlorine displacing Iodine
        Reaction(
            id = "HALOGEN_DISPLACEMENT",
            reactantIds = setOf("Cl", "KI"),
            productIds = listOf("KCl", "I"),
            equation = "Cl₂ + 2KI → 2KCl + I₂ (Elemental Violet Iodine)",
            minTemp = 20.0,
            isExothermic = true,
            tempChange = 20.0,
            resultingPh = 6.8,
            resultingColor = 0xFF581C87,
            observation = "Electronegative chlorine oxidizes iodide! Colorless solution turns amber then deep violet-ruby as elemental iodine liberates."
        ),
        // 33. Thermal Chlorate Oxygen Preparation
        Reaction(
            id = "KCLO3_THERMAL_OXYGEN",
            reactantIds = setOf("KClO3"),
            productIds = listOf("KCl", "O"),
            equation = "2KClO₃ + Heat (>400°C) → 2KCl + 3O₂ ↑",
            minTemp = 400.0,
            isExothermic = false,
            tempChange = -10.0,
            resultingPh = 7.0,
            resultingColor = 0xEEF8FAFC,
            observation = "Thermal decomposition in crucible releases brisk vigorous streams of pure oxygen gas, leaving neutral KCl."
        )
    ) + AllReactionsCatalog.ALL_105_REACTIONS + ReactionsCatalog106To155.REACTIONS_106_TO_155 + ReactionsCatalog156To205.REACTIONS_156_TO_205

    fun normalizeReactant(id: String): String = when (id) {
        "H2" -> "H"
        "O2" -> "O"
        "N2" -> "N"
        "Cl2" -> "Cl"
        "Br2" -> "Br"
        "I2" -> "I"
        "P4" -> "P"
        else -> id
    }

    fun getChemical(id: String): Chemical? {
        val norm = normalizeReactant(id)
        return ALL_CHEMICALS.find {
            it.id.equals(id, ignoreCase = true) ||
            it.id.equals(norm, ignoreCase = true) ||
            (it.symbol != null && (it.symbol.equals(id, ignoreCase = true) || it.symbol.equals(norm, ignoreCase = true))) ||
            it.formula.equals(id, ignoreCase = true) ||
            it.formula.equals(norm, ignoreCase = true)
        }
    }

    fun findMatchingReaction(
        reactantIds: Set<String>,
        currentTemp: Double,
        isElectricityActive: Boolean,
        isCentrifugeActive: Boolean,
        activeTool: LabToolType
    ): Reaction? {
        val normalizedReactants = reactantIds.map { normalizeReactant(it) }.toSet()

        return REACTIONS.find { rx ->
            val rxReactants = rx.reactantIds.map { normalizeReactant(it) }.toSet()
            val hasAllReactants = rxReactants.all { it in normalizedReactants || it in reactantIds }
            val tempSatisfied = currentTemp >= (rx.minTemp - 15.0) && currentTemp <= rx.maxTemp
            val electricitySatisfied = !rx.requiresElectricity || isElectricityActive
            val centrifugeSatisfied = !rx.requiresCentrifuge || isCentrifugeActive
            val toolSatisfied = rx.requiredTool == null || rx.requiredTool == activeTool
            hasAllReactants && tempSatisfied && electricitySatisfied && centrifugeSatisfied && toolSatisfied
        }
    }
}
