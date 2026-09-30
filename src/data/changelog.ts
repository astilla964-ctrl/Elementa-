export interface ChangelogEntry {
  version: string;
  date: string;
  changes: {
    type: 'feat' | 'fix' | 'refactor' | 'ui';
    description: string;
  }[];
}

export const CHANGELOG_HISTORY: ChangelogEntry[] = [
  {
    version: "Elementa v1.7.0 (Build 6)",
    date: "2026-09-30",
    changes: [
      { type: "feat", description: "Industrial Scale & Plant Automation Module ('Chemical Works') with CSTR & PFR reactors" },
      { type: "feat", description: "Continuous flow physics engine with residence time (tau = V/Q) and kinetic conversion yield" },
      { type: "feat", description: "Wet absorption gas scrubber and effluent wastewater neutralization basin with EPA compliance" },
      { type: "ui", description: "Interactive P&ID plant control schematic with live sensor transmitters (TT, PT, FT, AT, pH)" }
    ]
  },
  {
    version: "Elementa v1.6.0 (Build 5)",
    date: "2026-09-30",
    changes: [
      { type: "feat", description: "Quest & Career Mode with client contracts schema, requirements, and credit rewards" },
      { type: "feat", description: "Offline Room database persistence for completed assignments and lifetime career statistics" },
      { type: "ui", description: "Dedicated Career & Lab Assignments screen with career rank ladders and contract inspector" }
    ]
  },
  {
    version: "Elementa v1.5.0 (Build 4)",
    date: "2026-09-30",
    changes: [
      { type: "feat", description: "Environmental & thermodynamics simulation engine (Bunsen burner, hot plate, cryo dewar)" },
      { type: "feat", description: "Gastight stopper & calibrated dial pressure gauge with explosion mechanics" },
      { type: "ui", description: "Interactive live heat curves (Temp vs. Time) and incandescent glow heat maps" }
    ]
  },
  {
    version: "Elementa v1.4.0 (Build 3)",
    date: "2026-09-29",
    changes: [
      { type: "feat", description: "Quantitative dispensing apparatuses (Analytical Balance, Graduated Cylinder, Gas Syringe)" },
      { type: "feat", description: "Real-time stoichiometric engine calculating limiting reagents and exact yields" },
      { type: "ui", description: "Precipitate sediment beds, unreacted excess solids, and dynamic color blending" }
    ]
  },
  {
    version: "Elementa v1.0.0 (Build 1)",
    date: "2026-09-30",
    changes: [
      { type: "feat", description: "Initial release of Elementa by Astilla Softwares" },
      { type: "feat", description: "Stoichiometry & thermodynamics simulation engine" },
      { type: "feat", description: "Compounds Pokedex discovery view" }
    ]
  }
];
