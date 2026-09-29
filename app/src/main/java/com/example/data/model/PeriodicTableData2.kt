package com.example.data.model

object PeriodicTableData2 {
    private fun createElem(
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
    ): Chemical = PeriodicTableData1.createElem(z, sym, name, mass, period, group, series, state, colorHex, hazards, desc, preUnlocked)

    val ELEMENTS_57_TO_118: List<Chemical> = listOf(
        // Lanthanides (57-71)
        createElem(57, "La", "Lanthanum", 138.91, 6, 3, "Lanthanide", "Solid", 0xFFE2E8F0, emptyList(), "Silvery rare earth metal prototype for all lanthanides."),
        createElem(58, "Ce", "Cerium", 140.12, 6, 3, "Lanthanide", "Solid", 0xFFCBD5E1, emptyList(), "Most abundant rare earth element, used in catalytic converters and flint lighters."),
        createElem(59, "Pr", "Praseodymium", 140.91, 6, 3, "Lanthanide", "Solid", 0xFFE2E8F0, emptyList(), "Soft malleable metal imparting intense yellow-green hue to glass."),
        createElem(60, "Nd", "Neodymium", 144.24, 6, 3, "Lanthanide", "Solid", 0xFF94A3B8, emptyList(), "Powers world's strongest permanent magnets (NdFeB alloys)."),
        createElem(61, "Pm", "Promethium", 145.0, 6, 3, "Lanthanide", "Solid", 0xFF64748B, listOf(HazardSymbol.BIOHAZARD), "Only radioactive lanthanide, used in luminous dials and atomic batteries."),
        createElem(62, "Sm", "Samarium", 150.36, 6, 3, "Lanthanide", "Solid", 0xFFCBD5E1, emptyList(), "Used in high-temperature SmCo permanent magnets and cancer therapy."),
        createElem(63, "Eu", "Europium", 151.96, 6, 3, "Lanthanide", "Solid", 0xFFE2E8F0, emptyList(), "Most reactive rare earth; red phosphors in Euro banknotes against counterfeiting."),
        createElem(64, "Gd", "Gadolinium", 157.25, 6, 3, "Lanthanide", "Solid", 0xFFCBD5E1, emptyList(), "Ferromagnetic rare earth metal used as intravenous MRI contrast agent."),
        createElem(65, "Tb", "Terbium", 158.93, 6, 3, "Lanthanide", "Solid", 0xFF94A3B8, emptyList(), "Produces brilliant green phosphors for high-definition displays and Sonar."),
        createElem(66, "Dy", "Dysprosium", 162.50, 6, 3, "Lanthanide", "Solid", 0xFFCBD5E1, emptyList(), "High thermal neutron absorption metal for nuclear reactor control rods."),
        createElem(67, "Ho", "Holmium", 164.93, 6, 3, "Lanthanide", "Solid", 0xFFE2E8F0, emptyList(), "Possesses highest magnetic moment of any naturally occurring element."),
        createElem(68, "Er", "Erbium", 167.26, 6, 3, "Lanthanide", "Solid", 0xFFCBD5E1, emptyList(), "Pink-tinted element providing optical amplification in undersea fiber cables."),
        createElem(69, "Tm", "Thulium", 168.93, 6, 3, "Lanthanide", "Solid", 0xFF94A3B8, emptyList(), "Second least abundant lanthanide, used in portable X-ray lasers."),
        createElem(70, "Yb", "Ytterbium", 173.05, 6, 3, "Lanthanide", "Solid", 0xFFCBD5E1, emptyList(), "Soft ductile rare earth element used in atomic clocks and stainless stress gauges."),
        createElem(71, "Lu", "Lutetium", 174.97, 6, 3, "Lanthanide", "Solid", 0xFFE2E8F0, emptyList(), "Hardest and densest lanthanide; positron emission tomography (PET) detectors."),

        // Period 6 Transition Metals (72-80)
        createElem(72, "Hf", "Hafnium", 178.49, 6, 4, "Transition Metal", "Solid", 0xFF64748B, emptyList(), "Chemically identical twin to zirconium, used in modern computer transistor gates."),
        createElem(73, "Ta", "Tantalum", 180.95, 6, 5, "Transition Metal", "Solid", 0xFF475569, emptyList(), "Extremely corrosion-resistant metal used in microcapacitors of smartphones."),
        createElem(74, "W", "Tungsten", 183.84, 6, 6, "Transition Metal", "Solid", 0xFF334155, emptyList(), "Highest melting point of all metals (3422°C); incandescent lightbulb filaments.", true),
        createElem(75, "Re", "Rhenium", 186.21, 6, 7, "Transition Metal", "Solid", 0xFF64748B, emptyList(), "One of rarest crustal metals, essential for jet aircraft combustion chambers."),
        createElem(76, "Os", "Osmium", 190.23, 6, 8, "Transition Metal", "Solid", 0xFF1E293B, listOf(HazardSymbol.TOXIC), "Densest naturally occurring element on Earth (22.59 g/cm³)."),
        createElem(77, "Ir", "Iridium", 192.22, 6, 9, "Transition Metal", "Solid", 0xFFE2E8F0, emptyList(), "Most corrosion-resistant metal; geological marker for the asteroid impact on dinosaurs."),
        createElem(78, "Pt", "Platinum", 195.08, 6, 10, "Transition Metal", "Solid", 0xFFF1F5F9, emptyList(), "Dense noble metal prized for fine jewelry and heterogeneous chemical catalysis.", true),
        createElem(79, "Au", "Gold", 196.97, 6, 11, "Transition Metal", "Solid", 0xFFEAB308, emptyList(), "Noble yellow precious metal, exceptionally unreactive and corrosion-proof.", true),
        createElem(80, "Hg", "Mercury", 200.59, 6, 12, "Transition Metal", "Liquid", 0xFFCBD5E1, listOf(HazardSymbol.TOXIC), "Only metallic element liquid at standard temperature and pressure (Quicksilver).", true),

        // Period 6 Post-transition, Metalloids, Halogen, Noble Gas (81-86)
        createElem(81, "Tl", "Thallium", 204.38, 6, 13, "Post-Transition Metal", "Solid", 0xFF94A3B8, listOf(HazardSymbol.TOXIC), "Highly toxic soft metal historically nicknamed 'The Poisoner's Poison'."),
        createElem(82, "Pb", "Lead", 207.2, 6, 14, "Post-Transition Metal", "Solid", 0xFF475569, listOf(HazardSymbol.TOXIC), "Dense malleable metal used as effective shielding against ionizing radiation.", true),
        createElem(83, "Bi", "Bismuth", 208.98, 6, 15, "Post-Transition Metal", "Solid", 0xFFCBD5E1, emptyList(), "Forms iridescent rainbow oxidation crystals; active ingredient in Pepto-Bismol."),
        createElem(84, "Po", "Polonium", 209.0, 6, 16, "Post-Transition Metal", "Solid", 0xFF64748B, listOf(HazardSymbol.BIOHAZARD), "Intensely radioactive alpha emitter discovered by Marie and Pierre Curie."),
        createElem(85, "At", "Astatine", 210.0, 6, 17, "Halogen", "Solid", 0xFF1E293B, listOf(HazardSymbol.BIOHAZARD), "Rarest naturally occurring element in Earth's crust (less than 1 gram globally)."),
        createElem(86, "Rn", "Radon", 222.0, 6, 18, "Noble Gas", "Gas", 0x44CBD5E1, listOf(HazardSymbol.BIOHAZARD), "Radioactive colorless noble gas decaying from natural radium in granite soils."),

        // Period 7 Alkali & Alkaline Earth (87-88)
        createElem(87, "Fr", "Francium", 223.0, 7, 1, "Alkali Metal", "Solid", 0xFFE2E8F0, listOf(HazardSymbol.BIOHAZARD), "Second rarest crustal element; highly unstable alkali metal with 22-min half-life."),
        createElem(88, "Ra", "Radium", 226.0, 7, 2, "Alkaline Earth Metal", "Solid", 0xFFF1F5F9, listOf(HazardSymbol.BIOHAZARD), "Luminous radioactive alkaline earth metal discovered by Marie Curie in pitchblende."),

        // Actinides (89-103)
        createElem(89, "Ac", "Actinium", 227.0, 7, 3, "Actinide", "Solid", 0xFFE2E8F0, listOf(HazardSymbol.BIOHAZARD), "Radioactive element that glows with an eerie pale blue light in the dark."),
        createElem(90, "Th", "Thorium", 232.04, 7, 3, "Actinide", "Solid", 0xFF94A3B8, listOf(HazardSymbol.BIOHAZARD), "Abundant actinide nuclear fuel alternative for molten salt reactors."),
        createElem(91, "Pa", "Protactinium", 231.04, 7, 3, "Actinide", "Solid", 0xFF64748B, listOf(HazardSymbol.BIOHAZARD), "Dense radioactive actinide element, precursor of actinium in decay series."),
        createElem(92, "U", "Uranium", 238.03, 7, 3, "Actinide", "Solid", 0xFF334155, listOf(HazardSymbol.BIOHAZARD), "Dense radioactive actinide fueling nuclear power reactors and weapons.", true),
        createElem(93, "Np", "Neptunium", 237.0, 7, 3, "Actinide", "Solid", 0xFF475569, listOf(HazardSymbol.BIOHAZARD), "First transuranic actinide synthesized by McMillan and Abelson in 1940."),
        createElem(94, "Pu", "Plutonium", 244.0, 7, 3, "Actinide", "Solid", 0xFF64748B, listOf(HazardSymbol.BIOHAZARD), "Transuranic fissile element powering deep-space Voyager RTG batteries."),
        createElem(95, "Am", "Americium", 243.0, 7, 3, "Actinide", "Solid", 0xFFE2E8F0, listOf(HazardSymbol.BIOHAZARD), "Synthetic alpha emitter used in domestic ionization smoke detectors."),
        createElem(96, "Cm", "Curium", 247.0, 7, 3, "Actinide", "Solid", 0xFFCBD5E1, listOf(HazardSymbol.BIOHAZARD), "Hard radioactive transuranic metal named in honor of Marie and Pierre Curie."),
        createElem(97, "Bk", "Berkelium", 247.0, 7, 3, "Actinide", "Solid", 0xFF94A3B8, listOf(HazardSymbol.BIOHAZARD), "Transuranic actinide synthesized at University of California, Berkeley."),
        createElem(98, "Cf", "Californium", 251.0, 7, 3, "Actinide", "Solid", 0xFF64748B, listOf(HazardSymbol.BIOHAZARD), "Practical portable neutron source used in oil well logging and mineral scanning."),
        createElem(99, "Es", "Einsteinium", 252.0, 7, 3, "Actinide", "Solid", 0xFFCBD5E1, listOf(HazardSymbol.BIOHAZARD), "Discovered in debris of the 'Ivy Mike' thermonuclear test in 1952."),
        createElem(100, "Fm", "Fermium", 257.0, 7, 3, "Actinide", "Solid", 0xFF94A3B8, listOf(HazardSymbol.BIOHAZARD), "Heaviest element that can be formed by neutron bombardment of lighter elements."),
        createElem(101, "Md", "Mendelevium", 258.0, 7, 3, "Actinide", "Solid", 0xFF64748B, listOf(HazardSymbol.BIOHAZARD), "Named in honor of Dmitri Mendeleev, father of the Periodic Table."),
        createElem(102, "No", "Nobelium", 259.0, 7, 3, "Actinide", "Solid", 0xFFE2E8F0, listOf(HazardSymbol.BIOHAZARD), "Synthetic actinide metal named in memory of Alfred Nobel."),
        createElem(103, "Lr", "Lawrencium", 266.0, 7, 3, "Actinide", "Solid", 0xFFCBD5E1, listOf(HazardSymbol.BIOHAZARD), "Final actinide element, named for cyclotron inventor Ernest Lawrence."),

        // Transactinides / Superheavies (104-118)
        createElem(104, "Rf", "Rutherfordium", 267.0, 7, 4, "Transition Metal", "Solid", 0xFF64748B, listOf(HazardSymbol.BIOHAZARD), "First transactinide element, named after nuclear pioneer Ernest Rutherford."),
        createElem(105, "Db", "Dubnium", 268.0, 7, 5, "Transition Metal", "Solid", 0xFF475569, listOf(HazardSymbol.BIOHAZARD), "Superheavy synthetic element synthesized at Joint Institute for Nuclear Research, Dubna."),
        createElem(106, "Sg", "Seaborgium", 269.0, 7, 6, "Transition Metal", "Solid", 0xFF334155, listOf(HazardSymbol.BIOHAZARD), "Named after Glenn T. Seaborg, first living person honored on periodic table."),
        createElem(107, "Bh", "Bohrium", 270.0, 7, 7, "Transition Metal", "Solid", 0xFF475569, listOf(HazardSymbol.BIOHAZARD), "Superheavy element named after Danish quantum physicist Niels Bohr."),
        createElem(108, "Hs", "Hassium", 269.0, 7, 8, "Transition Metal", "Solid", 0xFF64748B, listOf(HazardSymbol.BIOHAZARD), "Named for German state of Hesse; forms volatile tetroxide similar to osmium."),
        createElem(109, "Mt", "Meitnerium", 278.0, 7, 9, "Transition Metal", "Solid", 0xFF94A3B8, listOf(HazardSymbol.BIOHAZARD), "Honors Austrian-Swedish physicist Lise Meitner, discoverer of nuclear fission."),
        createElem(110, "Ds", "Darmstadtium", 281.0, 7, 10, "Transition Metal", "Solid", 0xFF64748B, listOf(HazardSymbol.BIOHAZARD), "Synthesized at GSI Helmholtz Centre for Heavy Ion Research in Darmstadt."),
        createElem(111, "Rg", "Roentgenium", 282.0, 7, 11, "Transition Metal", "Solid", 0xFFEAB308, listOf(HazardSymbol.BIOHAZARD), "Coinage metal group homologue named for Wilhelm Röntgen, discoverer of X-rays."),
        createElem(112, "Cn", "Copernicium", 285.0, 7, 12, "Transition Metal", "Gas", 0xFFCBD5E1, listOf(HazardSymbol.BIOHAZARD), "Volatile superheavy element named after astronomer Nicolaus Copernicus."),
        createElem(113, "Nh", "Nihonium", 286.0, 7, 13, "Post-Transition Metal", "Solid", 0xFFE2E8F0, listOf(HazardSymbol.BIOHAZARD), "First element discovered in Asia, synthesized by RIKEN in Japan."),
        createElem(114, "Fl", "Flerovium", 289.0, 7, 14, "Post-Transition Metal", "Gas", 0xFF94A3B8, listOf(HazardSymbol.BIOHAZARD), "Superheavy element lying at the heart of the theoretical Island of Stability."),
        createElem(115, "Mc", "Moscovium", 290.0, 7, 15, "Post-Transition Metal", "Solid", 0xFF64748B, listOf(HazardSymbol.BIOHAZARD), "Synthetic superheavy element named in honor of the Moscow Oblast."),
        createElem(116, "Lv", "Livermorium", 293.0, 7, 16, "Post-Transition Metal", "Solid", 0xFF475569, listOf(HazardSymbol.BIOHAZARD), "Named for Lawrence Livermore National Laboratory and city of Livermore, California."),
        createElem(117, "Ts", "Tennessine", 294.0, 7, 17, "Halogen", "Solid", 0xFF334155, listOf(HazardSymbol.BIOHAZARD), "Second-heaviest known element, named for the state of Tennessee."),
        createElem(118, "Og", "Oganesson", 294.0, 7, 18, "Noble Gas", "Solid", 0xFF581C87, listOf(HazardSymbol.BIOHAZARD), "Heaviest element ever created; atomic number 118 completes Period 7 of the universe.")
    )
}
