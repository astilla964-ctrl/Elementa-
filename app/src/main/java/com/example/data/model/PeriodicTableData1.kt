package com.example.data.model

object PeriodicTableData1 {
    val ELEMENTS_1_TO_56: List<Chemical> = listOf(
        // Period 1
        createElem(1, "H", "Hydrogen", 1.008, 1, 1, "Reactive Nonmetal", "Gas", 0x33F8FAFC, listOf(HazardSymbol.FLAMMABLE, HazardSymbol.COMPRESSED_GAS), "Lightest element in universe; fuels cosmic stars.", true),
        createElem(2, "He", "Helium", 4.0026, 1, 18, "Noble Gas", "Gas", 0x22FEE2E2, listOf(HazardSymbol.COMPRESSED_GAS), "Inert noble gas, second most abundant in universe.", true),

        // Period 2
        createElem(3, "Li", "Lithium", 6.94, 2, 1, "Alkali Metal", "Solid", 0xFFE2E8F0, listOf(HazardSymbol.FLAMMABLE, HazardSymbol.CORROSIVE), "Lightest solid metal; stores electrochemical charge.", true),
        createElem(4, "Be", "Beryllium", 9.0122, 2, 2, "Alkaline Earth Metal", "Solid", 0xFF94A3B8, listOf(HazardSymbol.TOXIC), "Lightweight alkaline metal used in aerospace alloys."),
        createElem(5, "B", "Boron", 10.81, 2, 13, "Metalloid", "Solid", 0xFF78716C, listOf(HazardSymbol.IRRITANT), "Hard metalloid crucial for heat-resistant borosilicate glass."),
        createElem(6, "C", "Carbon", 12.011, 2, 14, "Reactive Nonmetal", "Solid", 0xFF27272A, listOf(HazardSymbol.IRRITANT), "Fundamental element forming backbone of all organic life.", true),
        createElem(7, "N", "Nitrogen", 14.007, 2, 15, "Reactive Nonmetal", "Gas", 0x33CBD5E1, listOf(HazardSymbol.COMPRESSED_GAS), "Diatomic triple-bonded gas making up 78% of Earth's atmosphere.", true),
        createElem(8, "O", "Oxygen", 15.999, 2, 16, "Reactive Nonmetal", "Gas", 0x44E0F2FE, listOf(HazardSymbol.OXIDIZING, HazardSymbol.COMPRESSED_GAS), "Essential oxidizing diatomic gas supporting aerobic respiration.", true),
        createElem(9, "F", "Fluorine", 18.998, 2, 17, "Halogen", "Gas", 0x66FEF08A, listOf(HazardSymbol.TOXIC, HazardSymbol.CORROSIVE), "Most electronegative and reactive of all chemical elements."),
        createElem(10, "Ne", "Neon", 20.180, 2, 18, "Noble Gas", "Gas", 0x44FCA5A5, listOf(HazardSymbol.COMPRESSED_GAS), "Inert noble gas emitting brilliant reddish-orange glow in discharge."),

        // Period 3
        createElem(11, "Na", "Sodium", 22.990, 3, 1, "Alkali Metal", "Solid", 0xFFCBD5E1, listOf(HazardSymbol.FLAMMABLE, HazardSymbol.CORROSIVE), "Soft reactive alkali metal that decomposes water violently.", true),
        createElem(12, "Mg", "Magnesium", 24.305, 3, 2, "Alkaline Earth Metal", "Solid", 0xFFE2E8F0, listOf(HazardSymbol.FLAMMABLE), "Silvery alkaline metal burning with an intense blinding white light.", true),
        createElem(13, "Al", "Aluminum", 26.982, 3, 13, "Post-Transition Metal", "Solid", 0xFFCBD5E1, listOf(HazardSymbol.FLAMMABLE), "Lightweight corrosion-resistant metal forming protective oxide skin.", true),
        createElem(14, "Si", "Silicon", 28.085, 3, 14, "Metalloid", "Solid", 0xFF475569, listOf(HazardSymbol.IRRITANT), "Semiconductor metalloid forming silicon valley microchips."),
        createElem(15, "P", "Phosphorus", 30.974, 3, 15, "Reactive Nonmetal", "Solid", 0xFFDC2626, listOf(HazardSymbol.FLAMMABLE, HazardSymbol.TOXIC), "Polymorphic nonmetal essential in biological DNA and ATP."),
        createElem(16, "S", "Sulfur", 32.06, 3, 16, "Reactive Nonmetal", "Solid", 0xFFFACC15, listOf(HazardSymbol.IRRITANT), "Bright yellow octasulfur rings with volcanic aroma.", true),
        createElem(17, "Cl", "Chlorine", 35.45, 3, 17, "Halogen", "Gas", 0x66BEF264, listOf(HazardSymbol.TOXIC, HazardSymbol.CORROSIVE), "Yellow-green pungent diatomic halogen gas and strong oxidizer.", true),
        createElem(18, "Ar", "Argon", 39.95, 3, 18, "Noble Gas", "Gas", 0x3393C5FD, listOf(HazardSymbol.COMPRESSED_GAS), "Third most abundant atmospheric gas, provides inert shielding."),

        // Period 4
        createElem(19, "K", "Potassium", 39.098, 4, 1, "Alkali Metal", "Solid", 0xFFE2E8F0, listOf(HazardSymbol.FLAMMABLE, HazardSymbol.CORROSIVE), "Alkali metal burning with a distinct lilac flame over water.", true),
        createElem(20, "Ca", "Calcium", 40.078, 4, 2, "Alkaline Earth Metal", "Solid", 0xFFCBD5E1, listOf(HazardSymbol.FLAMMABLE), "Fifth most abundant element; building block of bone and chalk.", true),
        createElem(21, "Sc", "Scandium", 44.956, 4, 3, "Transition Metal", "Solid", 0xFF94A3B8, emptyList(), "Silvery transition metal used in aerospace titanium-scandium alloys."),
        createElem(22, "Ti", "Titanium", 47.867, 4, 4, "Transition Metal", "Solid", 0xFF64748B, emptyList(), "High strength-to-density ratio metal with outstanding corrosion resistance.", true),
        createElem(23, "V", "Vanadium", 50.942, 4, 5, "Transition Metal", "Solid", 0xFF475569, listOf(HazardSymbol.TOXIC), "Hard transition metal producing vibrant colorful oxidation states."),
        createElem(24, "Cr", "Chromium", 51.996, 4, 6, "Transition Metal", "Solid", 0xFF94A3B8, listOf(HazardSymbol.TOXIC), "Steely-gray lustrous metal taking high polish for electroplating."),
        createElem(25, "Mn", "Manganese", 54.938, 4, 7, "Transition Metal", "Solid", 0xFF64748B, listOf(HazardSymbol.OXIDIZING), "Transition metal forming purple permanganate oxidizer solutions."),
        createElem(26, "Fe", "Iron", 55.845, 4, 8, "Transition Metal", "Solid", 0xFF64748B, emptyList(), "Magnetic transition metal filings forming backbone of metallurgy.", true),
        createElem(27, "Co", "Cobalt", 58.933, 4, 9, "Transition Metal", "Solid", 0xFF1E3A8A, listOf(HazardSymbol.TOXIC), "Hard ferromagnetic metal producing brilliant cobalt-blue pigments."),
        createElem(28, "Ni", "Nickel", 58.693, 4, 10, "Transition Metal", "Solid", 0xFF94A3B8, listOf(HazardSymbol.IRRITANT), "Silvery-white lustrous transition metal used in corrosion-resistant superalloys."),
        createElem(29, "Cu", "Copper", 63.546, 4, 11, "Transition Metal", "Solid", 0xFFB45309, emptyList(), "Ductile reddish transition metal prized for electrical conductivity.", true),
        createElem(30, "Zn", "Zinc", 65.38, 4, 12, "Transition Metal", "Solid", 0xFF94A3B8, listOf(HazardSymbol.IRRITANT), "Galvanizing transition metal reacting with acids to produce hydrogen.", true),
        createElem(31, "Ga", "Gallium", 69.723, 4, 13, "Post-Transition Metal", "Solid", 0xFFCBD5E1, emptyList(), "Soft metal that melts in human hand (mp 29.76°C)."),
        createElem(32, "Ge", "Germanium", 72.630, 4, 14, "Metalloid", "Solid", 0xFF64748B, emptyList(), "Lustrous gray-white metalloid used in wide-angle camera optics."),
        createElem(33, "As", "Arsenic", 74.922, 4, 15, "Metalloid", "Solid", 0xFF334155, listOf(HazardSymbol.TOXIC), "Notorious toxic metalloid forming brittle metallic allotropes."),
        createElem(34, "Se", "Selenium", 78.971, 4, 16, "Reactive Nonmetal", "Solid", 0xFF1E293B, listOf(HazardSymbol.TOXIC), "Photoconductive nonmetal used in photocells and glass decoloring."),
        createElem(35, "Br", "Bromine", 79.904, 4, 17, "Halogen", "Liquid", 0xFF7C2D12, listOf(HazardSymbol.CORROSIVE, HazardSymbol.TOXIC), "Dense fuming reddish-brown liquid halogen at standard room temperature."),
        createElem(36, "Kr", "Krypton", 83.798, 4, 18, "Noble Gas", "Gas", 0x33A5F3FC, listOf(HazardSymbol.COMPRESSED_GAS), "Noble gas with whitish-green spectral lines used in airport flash lamps."),

        // Period 5
        createElem(37, "Rb", "Rubidium", 85.468, 5, 1, "Alkali Metal", "Solid", 0xFFCBD5E1, listOf(HazardSymbol.FLAMMABLE, HazardSymbol.CORROSIVE), "Very soft pyrophoric alkali metal igniting spontaneously in air."),
        createElem(38, "Sr", "Strontium", 87.62, 5, 2, "Alkaline Earth Metal", "Solid", 0xFFE2E8F0, listOf(HazardSymbol.FLAMMABLE), "Alkaline earth metal producing brilliant crimson red fireworks."),
        createElem(39, "Y", "Yttrium", 88.906, 5, 3, "Transition Metal", "Solid", 0xFF94A3B8, emptyList(), "Transition metal named after Ytterby village in Sweden, used in YBCO superconductors."),
        createElem(40, "Zr", "Zirconium", 91.224, 5, 4, "Transition Metal", "Solid", 0xFF64748B, emptyList(), "Corrosion-proof transition metal used as fuel cladding in nuclear reactors."),
        createElem(41, "Nb", "Niobium", 92.906, 5, 5, "Transition Metal", "Solid", 0xFF475569, emptyList(), "Ductile paramagnetic metal used in MRI superconducting magnets."),
        createElem(42, "Mo", "Molybdenum", 95.95, 5, 6, "Transition Metal", "Solid", 0xFF64748B, emptyList(), "High-melting metal (mp 2623°C) critical for high-strength steel alloys."),
        createElem(43, "Tc", "Technetium", 98.0, 5, 7, "Transition Metal", "Solid", 0xFF475569, listOf(HazardSymbol.BIOHAZARD), "First artificially synthesized chemical element; radioisotope medical tracer."),
        createElem(44, "Ru", "Ruthenium", 101.07, 5, 8, "Transition Metal", "Solid", 0xFF94A3B8, emptyList(), "Rare platinum-group transition metal catalyst resistant to attack by acids."),
        createElem(45, "Rh", "Rhodium", 102.91, 5, 9, "Transition Metal", "Solid", 0xFFCBD5E1, emptyList(), "Extremely noble transition metal in automotive catalytic converters."),
        createElem(46, "Pd", "Palladium", 106.42, 5, 10, "Transition Metal", "Solid", 0xFFE2E8F0, emptyList(), "Platinum-group metal capable of absorbing 900 times its volume of hydrogen gas."),
        createElem(47, "Ag", "Silver", 107.87, 5, 11, "Transition Metal", "Solid", 0xFFF1F5F9, emptyList(), "Highest electrical conductivity, thermal conductivity, and reflectivity of all metals.", true),
        createElem(48, "Cd", "Cadmium", 112.41, 5, 12, "Transition Metal", "Solid", 0xFF94A3B8, listOf(HazardSymbol.TOXIC), "Soft bluish-white transition metal found in electroplating and NiCd batteries."),
        createElem(49, "In", "Indium", 114.82, 5, 13, "Post-Transition Metal", "Solid", 0xFFCBD5E1, emptyList(), "Malleable metal that emits an audible high-pitched 'cry' when bent."),
        createElem(50, "Sn", "Tin", 118.71, 5, 14, "Post-Transition Metal", "Solid", 0xFFE2E8F0, emptyList(), "Malleable silvery metal known since antiquity for making bronze."),
        createElem(51, "Sb", "Antimony", 121.76, 5, 15, "Metalloid", "Solid", 0xFF64748B, listOf(HazardSymbol.TOXIC), "Lustrous gray metalloid expanding slightly as it solidifies."),
        createElem(52, "Te", "Tellurium", 127.60, 5, 16, "Metalloid", "Solid", 0xFF475569, listOf(HazardSymbol.TOXIC), "Brittle mildly toxic metalloid related to sulfur and selenium."),
        createElem(53, "I", "Iodine", 126.90, 5, 17, "Halogen", "Solid", 0xFF581C87, listOf(HazardSymbol.CORROSIVE, HazardSymbol.TOXIC), "Lustrous purple-black solid halogen subliming into magnificent violet vapor.", true),
        createElem(54, "Xe", "Xenon", 131.29, 5, 18, "Noble Gas", "Gas", 0x44C084FC, listOf(HazardSymbol.COMPRESSED_GAS), "Heavy noble gas capable of forming xenon fluoride compounds."),

        // Period 6 starts
        createElem(55, "Cs", "Cesium", 132.91, 6, 1, "Alkali Metal", "Solid", 0xFFFEF08A, listOf(HazardSymbol.FLAMMABLE, HazardSymbol.CORROSIVE), "Most reactive metal; frequency of cesium-133 defines the international SI second."),
        createElem(56, "Ba", "Barium", 137.33, 6, 2, "Alkaline Earth Metal", "Solid", 0xFFE2E8F0, listOf(HazardSymbol.TOXIC), "Heavy alkaline earth metal imparting emerald-green color to pyrotechnics.")
    )

    fun createElem(
        z: Int,
        sym: String,
        name: String,
        mass: Double,
        period: Int,
        group: Int,
        series: String,
        state: String,
        colorHex: Long,
        hazards: List<HazardSymbol>,
        desc: String,
        preUnlocked: Boolean = false
    ): Chemical {
        return Chemical(
            id = sym,
            name = name,
            formula = sym,
            molarMass = mass,
            category = ChemicalCategory.ELEMENT,
            colorHex = colorHex,
            physicalState = "$state (Element)",
            ph = 7.0,
            hazards = hazards,
            discoveryHint = "Atomic number $z ($sym). $desc",
            isPreUnlocked = preUnlocked,
            description = desc,
            atomicNumber = z,
            symbol = sym,
            period = period,
            group = group,
            elementSeries = series
        )
    }
}
