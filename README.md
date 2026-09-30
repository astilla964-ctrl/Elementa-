# 🧪 Elementa — 2D Interactive Chemistry Simulation & Laboratory Tool

[![GitHub Release](https://img.shields.io/github/v/release/astillasoftwares/elementa?color=blue&label=Latest%20Release)](https://github.com/astillasoftwares/elementa/releases)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple.svg)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Platform-Android%2014%2B-green.svg)](https://developer.android.com)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-4285F4.svg)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

> **Developed by Astilla Softwares**  
> *A high-performance 2D laboratory chemistry simulation, particle physics sandbox, and apparatus management tool.*

---

## 📖 Overview

**Elementa** brings interactive experimental chemistry to Android. Featuring a custom 2D particle simulation engine, all 118 periodic table elements, over 100 comprehensive chemical reactions, and standard laboratory apparatus, Elementa allows researchers, students, and hobbyists to safely mix reagents, trigger reactions, measure thermodynamic properties, and document syntheses in a local Room database Pokedex.

---

## ✨ Features

- **🏭 Industrial Scale & Plant Automation Module ("Chemical Works")**:
  - *Unit Operations Equipment*: Continuously Stirred Tank Reactors (CSTR with variable volume from $100\text{--}10,000\text{ L}$ and impeller agitation) and Plug Flow Reactors (PFR with tubular axial flow kinetics).
  - *Modular Transport Lines*: Centrifugal pumps with RPM and variable flow rates ($L/\text{min}$), process valves, check valves, and line pressure monitoring ($kPa$).
  - *Thermal Exchange Jackets*: External cooling and heating jackets with automated closed-loop PID, chilled water cooling, and high-pressure steam heating.
  - *Continuous Flow Physics Engine*: Real-time mass and volumetric conservation ($\text{Accumulation} = \text{Flow In} - \text{Flow Out} + \text{Generation} - \text{Consumption}$), residence time optimization ($\tau = V / Q$), and Arrhenius kinetic conversion.
  - *Environmental Safety & Scrubber Systems*: Counter-current wet absorption gas scrubbers for flue gases ($SO_2, Cl_2, NH_3$), effluent treatment basin with automated caustic/acid pH balancing ($6.5\text{--}8.5$), and overpressurization emergency relief interlocks.
  - *Interactive P&ID Schematic & Industrial Contracts*: High-level piping and instrumentation diagram with animated flow paths, digital transmitter tags ($TT, PT, FT, AT, pH$), and continuous industrial campaign contracts.
- **🔥 Environmental & Thermodynamics Simulation Engine**:
  - *Interactive Heating & Cooling Apparatuses*: Bunsen Burner with adjustable roaring gas flame (up to 1,500°C), Digital Hot Plate (up to 550°C), Ice Water Bath (0°C), Dry Ice / Acetone Bath (-78.5°C), and Cryogenic Liquid Nitrogen Dewar (-196°C).
  - *Pressure & Sealed Atmosphere Equipment*: Gastight rubber stopper with calibrated dial pressure gauge (0 - 100 atm), quick-action pressure relief valve, vacuum pump (<0.1 atm), and gas compressor.
  - *Thermodynamic Physics & Activation Logic*: Calculates thermal and pressure states dynamically before triggering reactions. Enforces Activation Energy ($E_a$) heat thresholds, dynamic phase transitions (melting, freezing, boiling, condensation), and realistic exothermic ($\Delta H < 0$) / endothermic ($\Delta H > 0$) enthalpy balance.
  - *Glassware Safety & Container Hazards*: Simulates physical limits of laboratory containers including thermal shock cracking and overpressurization container explosions ($PV = nRT > 5\text{ atm}$) with shattered glass shards, workbench liquid spills, and 1-tap glassware replacement.
  - *Visual Thermal Rendering & Live Telemetry*: High-temperature glass glow heat maps (>500°C red glow, >900°C bright incandescent orange/yellow), steam/vapor effervescence, condensation misting, floating container digital sensor badges, and live interactive heat curve graphs (Temp vs. Time) with 60-sample telemetry buffering.
- **📋 Quest & Career Mode ("Lab Assignments")**: Commercial client contracts with stoichiometric purity requirements, credit compensation, apparatus unlocks, and career progression ranks (*Apprentice Researcher* to *Chief Laboratory Director*).
- **🔬 118 Periodic Elements & 165+ Extended Compounds**: Complete physical profiles including molar mass, STP state, density, pH, GHS/NFPA hazard classifications, and boiling/melting points.
- **⚡ 238 Fully-Simulated Reactions**: Acid-base neutralizations, coordination complexes, combustion reactions, single & double displacements, organic syntheses, pyrotechnics, and electrochemical electrolysis.
- **🎨 2D Physics Particle Engine**: Real-time particle canvas rendering Brownian agitation, buoyancy, phase transitions, and visual particle archetypes (`bubble`, `vapor`, `crystal`, `fluid`).
- **🧰 Realistic Lab Apparatus**:
  - **Bunsen Burner**: Thermal heating up to 1500°C with flame color responses.
  - **Titration Buret**: Precise stoichiometric drop-by-drop titration.
  - **DC Electrodes**: Water and acid electrolysis with gas evolution.
  - **Centrifuge**: Accelerated precipitate sedimentation and separation.
  - **Beakers, Crucibles, Test Tubes & Condensers**.
- **📚 Lab Pokedex & Synthesis Dossier**: Automatically tracks discovery dates and production counts in an offline Room database.
- **🌓 Dynamic UI & Themes**: Material Design 3 with Dark, Light, Cyberpunk Neon, and Solar Flare color schemes, plus portrait/landscape orientation lock.

---

<!-- CHANGELOG_START -->
## 📜 Automatic Changelog

> *This section is automatically updated by GitHub Actions upon each new release tag.*

### [v1.7.0] — 2026-09-30
#### 🚀 Added
- **Industrial Scale & Plant Automation Module ("Chemical Works")**:
  - *Unit Operations Equipment*:
    - **CSTR (Continuously Stirred Tank Reactor)**: Automated reaction vessel with configurable volume ($100\text{--}10,000\text{ L}$) and dynamic impeller agitation ($0\text{--}600\text{ RPM}$).
    - **PFR (Plug Flow Tubular Reactor)**: Continuous tubular reactor model with axial concentration gradients and space-time conversion kinetics.
    - **Modular Transport Piping & Centrifugal Pumps**: Feed Pump A, Feed Pump B, and Reactor Discharge Pump with variable throughput ($L/\text{min}$), process valves, and line pressure sensors ($kPa$).
    - **Thermal Exchange Jackets**: Isothermal control jackets supporting closed-loop PID control, active chilled water cooling, and high-pressure steam heating.
  - *Continuous Flow Physics Engine*:
    - Mass and volumetric balance differential equations ($\text{Accumulation} = \text{Flow In} - \text{Flow Out} + \text{Generation} - \text{Consumption}$).
    - Fluid residence time optimization ($\tau = V / Q$) and Arrhenius temperature-dependent kinetic conversion ($k(T) = A \cdot e^{-E_a/(RT)}$).
    - Exothermic/endothermic thermal generation and jacket heat exchange ($Q_{hx} = UA(T_j - T_r)$).
    - Pressure modeling with Antoine vapor pressure approximations and overpressurization line rupture protection.
  - *Environmental Safety & Effluent Treatment*:
    - **Counter-Current Wet Absorption Scrubber**: Gas absorption tower with wash solvent dosing to scrub toxic/acidic emissions ($SO_2, Cl_2, NH_3$) under legal limits ($<50\text{ PPM}$).
    - **Effluent Neutralization Basin**: Wastewater treatment basin with automated caustic ($NaOH$) and acid ($H_2SO_4$) dosing to maintain regulatory neutral pH ($6.5\text{--}8.5$).
    - **Environmental Violation & Fines System**: EPA audits, credit penalties, and emergency relief alerts for non-compliant emissions or acid spills.
  - *P&ID Plant Control Schematic & Industrial Contracts*:
    - Interactive high-level piping and instrumentation diagram (P&ID) with animated fluid flow paths and live digital transmitter badges ($TT, PT, FT, AT, pH$).
    - Commercial continuous campaigns: Bulk Sulfuric Acid Campaign ($5,000\text{ L}$), Ammonium Sulfate Stream ($3,500\text{ L}$), Brine Neutralization ($4,000\text{ L}$), and Ethyl Acetate Refining ($2,000\text{ L}$).
    - Master Emergency Shutdown (ESD) trip interlock with auto-level balancing.
  - *Adaptive Navigation*: Integrated dedicated "Plant" tab into portrait bottom navigation and landscape side navigation rail.

---

### [v1.6.0] — 2026-09-30
#### 🚀 Added
- **Quest & Career Mode ("Lab Assignments")**:
  - *Contract & Quest Data Schema*: Strongly typed `LabAssignment` contract schema implemented in Kotlin and TypeScript defining client specifications, target chemical compound (`targetCompoundId`), required mass/volume (`targetAmount` in g/mL), minimum purity percentage threshold (`minPurity`), credit rewards, and optional equipment unlocks (`unlocksToolId`).
  - *Commercial Client Contracts Catalog*: Built-in corporate contracts spanning authentic chemistry challenges:
    - **PharmaCorp Synthetics**: *Synthesize Aspirin* (Clinical trial batch of Acetylsalicylic Acid `C9H8O4` @ $\ge 90\%$ purity, unlocking `CONDENSER`).
    - **City Water Authority**: *Neutralize Acid Runoff* (Effluent titration using sodium hydroxide to yield clean brine `NaCl` @ $\ge 95\%$ purity, unlocking `TITRATION_BURET`).
    - **Apex Metallurgy**: *Calcinate Iron Oxide Pigment* (Thermal oxidation of iron salts to precipitate `Fe2O3` @ $\ge 88\%$ purity, unlocking `CRUCIBLE`).
    - **GreenBio AgroChem**: *Synthesize Ammonia Precursor* (Catalytic nitrogen fixation producing `NH3` @ $\ge 90\%$ purity, unlocking `GAS_SYRINGE`).
    - **Metro Power & Grid**: *Electrolytic Copper Sulfate* (Synthesizing conductive `CuSO4` crystals @ $\ge 92\%$ purity, unlocking `ELECTRODES`).
    - **Aerospace Atmospheric Systems**: *Decompose Calcium Carbonate* (Thermal calcination yielding high-grade `CO2` gas @ $\ge 95\%$ purity, unlocking `EVAPORATING_DISH`).
    - **Photonic NanoTech**: *Silver Chloride Halide Precipitation* (Stoichiometric metathesis yielding `AgCl` @ $\ge 98\%$ purity, unlocking `CENTRIFUGE`).
  - *Room Database Career Persistence*: Offline local persistence with `completed_assignments` and `career_stats` SQLite tables tracking completed contracts, unlocked equipment, and lifetime earned credits.
  - *Interactive Career & Lab Assignments UI (`AssignmentsScreen.kt`)*:
    - Career rank ladder (*Apprentice Researcher* $\to$ *Junior Lab Associate* $\to$ *Analytical Chemist* $\to$ *Senior Synthetic Chemist* $\to$ *Chief Laboratory Director*).
    - Real-time credit balance badge, visual completion progress indicator bar, and quick status filters (*All*, *Available*, *Fulfilled*).
    - Detailed Contract Inspector bottom sheet modal allowing players to review client briefings, navigate directly to the lab workbench, and deliver batches to claim rewards.
  - *Adaptive Navigation*: Added dedicated "Quests" destination across portrait bottom navigation and landscape side navigation rail.

---

### [v1.5.0] — 2026-09-30
#### 🚀 Added
- **Environmental & Thermodynamics Simulation Engine**:
  - *Interactive Heating & Cooling Apparatuses*:
    - Digital Hot Plate with adjustable thermostatic surface heating up to 550°C.
    - Bunsen Burner with roaring blue flame output from 25°C to 1,500°C.
    - Ice Water Bath (0°C), Dry Ice / Acetone Bath (-78.5°C), and Liquid Nitrogen Dewar (-196°C) for deep thermal quenching.
    - Temperature slider adjusting heat output from -196°C to 1,500°C with quick-jump presets (`-196°C Cryo`, `0°C Ice`, `25°C Room`, `100°C Boil`, `450°C Flame`, `1000°C Glow`).
  - *Pressure & Sealed System Equipment*:
    - Gastight rubber stopper with calibrated dial pressure gauge (0 - 100 atm).
    - Pressure relief valve to quickly vent accumulated pressure down to 1.0 atm.
    - Vacuum pump to draw deep vacuums (<0.1 atm) and gas compressor to pressurize sealed systems.
  - *Dynamic Thermodynamic Physics & Activation Logic*:
    - *Activation Energy ($E_a$) Thresholds*: Reactions do not initiate until thermal energy reaches or exceeds reaction activation temperature ($T_{act}$), displaying real-time activation alerts (e.g., `Heat Required: Reached 350°C / Target 500°C (70% Activation Energy)`).
    - *Dynamic Phase Transitions*: Solutes and solvents dynamically melt at `meltingPointC` and boil into vapor at `boilingPointC`. Condensers condense vapor back into liquid.
    - *Enthalpy Balance*: Exothermic reactions ($\Delta H < 0$) automatically trigger internal container temperature spikes; endothermic reactions ($\Delta H > 0$) absorb ambient heat and rapidly cool vessels unless heated.
    - *Brownian Particle Agitation*: Particle velocity dynamically scales with $\sqrt{T_{Kelvin}}$, causing vigorous agitation at high heat and freezing at cryogenic temperatures.
  - *Glassware Safety & Container Hazard Mechanics*:
    - *Thermal Shock & Fractures*: Heating glassware above safe limits (>500°C for beakers, >450°C for test tubes) triggers thermal stress warnings and glass cracking.
    - *Overpressurization Explosions*: Reactions evolving gases inside sealed vessels without pressure relief increase internal pressure ($PV = nRT$). Exceeding container burst ratings (>5.0 atm) triggers a catastrophic glass explosion destroying the container, spilling chemicals, and requiring a 1-tap "Clean Up & Replace Glassware" action.
  - *Visual Thermal States & Live Telemetry Charting*:
    - Glass glow heat maps (>500°C dark red glow, >900°C brilliant yellow/orange incandescence).
    - Rising steam/vapor effervescence and condensation droplets on container walls.
    - Real-time floating digital overlay displaying temperature ($T$ in °C or K), pressure ($P$ in atm or kPa), and pH.
    - Interactive live heat curves (Temp vs. Time and Pressure vs. Time) displayed in a 60-sample telemetry buffer within the quantitative log drawer and container telemetry modal sheet.

---

### [v1.4.0] — 2026-09-29
#### 🚀 Added
- **Quantitative Measurement & Dispensing Apparatuses**:
  - *Analytical Balance & Spatula*: 4-decimal precision balance measuring solid mass in grams (`g`) with tare functionality.
  - *Graduated Cylinder & Precision Buret*: Calibrated volumetric glassware dispensing aliquot volumes in milliliters (`mL`) and solution concentrations in molarity (`M`).
  - *Gastight Gas Syringe & STP Pressure Valve*: Sealed calibrated syringe measuring vapor volumes at Standard Temperature & Pressure (`22.414 L/mol`).
- **Real-Time Stoichiometric Engine (Limiting & Excess Reagents)**:
  - *Mass-to-Mole & Volume-to-Mole Conversions*: $n = m / M$ or $n = M \times V$ or $n = V_{STP} / 22.414$.
  - *Balanced Coefficient Parser & Limiting Reactant Identification*: Compares reactant molar ratios against stoichiometric coefficients to strictly identify limiting and excess reagents.
  - *Product Yields & Unreacted Remainders*: Calculates exact theoretical yields of synthesized products and explicit leftover unreacted excess reagents.
- **Dynamic Container & Canvas Rendering of Unreacted Remainders**:
  - *Precipitates & Mixtures*: Renders settled precipitate sediment beds alongside granular crystalline solid particles for unreacted excess reagents at the bottom of the container.
  - *Unreacted Solutions/Liquids*: Computes real-time dynamic liquid color blending, transparency/turbidity alpha, and pH levels as a weighted average of formed products and excess reagents.
  - *Gas Off-Gassing Cessation*: Immediately ceases reaction gas bubble animations when the limiting reagent is exhausted.
- **Quantitative Reaction Log Interface**: Real-time bottom drawer displaying initial inputs, limiting reagent highlight in red/amber, theoretical product yields, and unreacted remainder descriptions.

---

### [v1.3.0] — 2026-09-29
#### 🚀 Added
- **100 New Chemical Reactions (`rxn_106` through `rxn_205`)**: Expanded the simulation engine to 238 total verified reactions ready to simulate.
  - *Coordination Chemistry*: Prussian Blue (`Fe₄[Fe(CN)₆]₃`), blood-red iron thiocyanate complex, deep royal-blue tetraamminecopper(II), and strawberry-pink nickel dimethylglyoxime (`Ni(DMG)₂`).
  - *Pyrotechnics & High-Energy Reactions*: Ammonium dichromate tabletop volcano, carbon sugar snake column, thermite welding reactions (iron, chromium, manganese), black powder, and chlorate-sugar deflagration.
  - *Chemical Gardens*: Osmotic growth of copper, cobalt, iron, and nickel silicate spires in water glass (`Na₂SiO₃`).
  - *Organic Synthesis & Diagnostics*: Fischer esterification of ethyl acetate and wintergreen oil, aspirin synthesis, yellow iodoform haloform test, Tollens' silver mirror test, and Fehling's reducing sugar test.
  - *Metal Activity Series & Single Displacement*: Silver crystal tree, lead tree, and aluminum/zinc replacement reactions.
  - *Extreme Thermodynamics & Electrochemistry*: Sub-zero endothermic freezing to -20°C, elephant's toothpaste foam eruption, chlor-alkali brine electrolysis, and copper electroplating.
- **95+ New Chemical Compounds (`MoreExtendedChemicals.kt`)**: Added full physical, thermodynamic, and hazard specifications for all newly synthesizable species.
- **1-Click Vessel Loading**: Extended Reaction Notebook & Guide to automatically configure reactants, glassware, temperature, and electrical power with one tap.

#### 🛠️ Fixed & Improved
- Verified complete catalog stoichiometry and resolution of all reactant and product species in automated unit test suites.
- Ensured smooth 2D physics simulation of newly introduced precipitates, gaseous effervescence, and solution color transitions.

---

### [v1.2.0] — 2026-09-29
#### 🚀 Added
- **Full 105 Reactions Engine Integration**: All 105 reactions from `chemistry_reactions_105.json` and seed reactions are compiled into `AllReactionsCatalog.kt` and wired into `ChemistryEngine.kt`.
- **Extended Compounds Registry**: Created `ExtendedChemicals.kt` covering 70 previously missing compound species (`AgNO₃`, `CuSO₄`, `Pb(NO₃)₂`, `KMnO₄`, `AlCl₃`, `BaSO₄`, `CH₃COOH`, `C₃H₈`, etc.) with physical properties and safety classifications.
- **Simulation Readiness Evaluation**: Introduced `Compound` data schema with `isSimulatable` validation checking physical parameter completeness.
- **Automatic Changelog Viewer**: Embedded in-app Changelog & README reader under Settings.

#### 🛠️ Fixed & Improved
- **Allotrope & Diatomic Normalization**: Reagent matching now handles `H₂` $\leftrightarrow$ `H`, `O₂` $\leftrightarrow$ `O`, `N₂` $\leftrightarrow$ `N`, `Cl₂` $\leftrightarrow$ `Cl`, `Br₂` $\leftrightarrow$ `Br`, `I₂` $\leftrightarrow$ `I`, and `P₄` $\leftrightarrow$ `P`.
- **Tool Mapping**: Connected 95 distinct lab tool names to active workbench apparatus.

---

### [v1.1.0] — 2026-09-29
#### 🚀 Added
- **100 Dynamic Chemistry Reactions**: Integrated `chemistry_reactions_100.json` with temperature triggers, catalysts, flame colors, and gas emission tracking.
- **Interactive Reaction Suggestions**: Workbench automatically prompts users when reactants in the beaker are close to reaction thresholds.
- **Compound Dossier Dialog**: Added detailed inspection modal for chemical formulas, atomic numbers, element families, and GHS handling symbols.

#### 🛠️ Fixed & Improved
- Optimized Room database queries with indexed queries for reaction logs and discovered compounds.
- Added dark and light theme contrast enhancements for chemical liquid representations.

---

### [v1.0.0] — 2026-09-28
#### 🚀 Initial Release
- **Periodic Table of Elements**: Full dataset of 118 elements categorized by family (Alkali, Halogen, Noble Gas, Transition Metals, etc.).
- **2D Particle Simulation Workbench**: Interactive canvas supporting drag-and-pour chemistry.
- **Support Developer Affordance**: Integrated GCash and Maya tipping numbers for Astilla Softwares.
- **Local Persistence**: SQLite/Room database saving discovered compounds and reaction history logs.
<!-- CHANGELOG_END -->

---

## 🔄 GitHub Automated Changelog Workflow

To keep this `README.md` automatically in sync with GitHub releases, the repository includes a GitHub Actions workflow (`.github/workflows/update-changelog.yml`):

1. **Trigger**: Runs automatically whenever a new GitHub Release is published or a version tag (`v*.*.*`) is pushed.
2. **Release Notes Extraction**: Automatically retrieves the latest release body and tag via the GitHub REST API.
3. **In-Place Update**: Updates the content between `<!-- CHANGELOG_START -->` and `<!-- CHANGELOG_END -->` in `README.md`.
4. **Git Commit & Push**: Commits the updated README back to `main` with a bot commit.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin 100%
- **Architecture**: Clean Architecture / MVVM (`MainViewModel`, `LabRepository`, `AppDatabase`)
- **UI Toolkit**: Jetpack Compose with Material Design 3 (M3)
- **Local Database**: Android Jetpack Room (SQLite)
- **Networking**: OkHttp3 & Moshi for GitHub Release API checks
- **Physics**: Custom Coroutine-driven 2D Euler particle engine

---

## 🤝 Support & Contributions

Developed with ❤️ by **Astilla Softwares**.

- **GCash**: `09273352516` (Lewis)
- **Maya**: `09273352516` (Maya)

If you enjoy Elementa, consider starring the repository and submitting issues or feature requests on GitHub!
