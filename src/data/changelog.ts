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
