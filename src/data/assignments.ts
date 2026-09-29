export interface AssignmentRequirements {
  targetCompoundId: string;  // The requested chemical product
  targetAmount: number;      // Required mass (g) or volume (mL)
  minPurity: number;         // Percentage (e.g., 90%)
}

export interface AssignmentRewards {
  credits: number;           // Currency earned upon completion
  unlocksToolId?: string;    // Specific equipment unlocked (optional)
}

export interface LabAssignment {
  id: string;
  clientName: string;          // e.g., "PharmaCorp", "City Water Authority"
  title: string;               // e.g., "Synthesize Aspirin", "Neutralize Acid"
  description: string;
  requirements: {
    targetCompoundId: string;  // The requested chemical product
    targetAmount: number;      // Required mass (g) or volume (mL)
    minPurity: number;         // Percentage (e.g., 90%)
  };
  rewards: {
    credits: number;           // Currency earned upon completion
    unlocksToolId?: string;    // Specific equipment unlocked (optional)
  };
  isCompleted: boolean;
}

export const DEFAULT_LAB_ASSIGNMENTS: LabAssignment[] = [
  {
    id: "quest_pharma_aspirin",
    clientName: "PharmaCorp Synthetics",
    title: "Synthesize Aspirin",
    description: "PharmaCorp requires an urgent clinical trial batch of pure Acetylsalicylic Acid (Aspirin) synthesized via acid-catalyzed esterification of salicylic acid and acetic anhydride.",
    requirements: {
      targetCompoundId: "C9H8O4",
      targetAmount: 25.0,
      minPurity: 92.0
    },
    rewards: {
      credits: 750,
      unlocksToolId: "CONDENSER"
    },
    isCompleted: false
  },
  {
    id: "quest_water_neutralize",
    clientName: "City Water Authority",
    title: "Neutralize Acid Runoff",
    description: "Industrial wastewater storage contains excess hydrochloric acid. Titrate and neutralize the effluent using stoichiometric sodium hydroxide to yield harmless sodium chloride brine.",
    requirements: {
      targetCompoundId: "NaCl",
      targetAmount: 50.0,
      minPurity: 95.0
    },
    rewards: {
      credits: 450,
      unlocksToolId: "TITRATION_BURET"
    },
    isCompleted: false
  },
  {
    id: "quest_metallurgy_rust",
    clientName: "Apex Metallurgy",
    title: "Thermal Oxide Roasting",
    description: "Synthesize pure Ferric Oxide (Fe2O3) pigment through controlled calcination of iron salts under oxidizing thermal atmosphere.",
    requirements: {
      targetCompoundId: "Fe2O3",
      targetAmount: 30.0,
      minPurity: 88.0
    },
    rewards: {
      credits: 600,
      unlocksToolId: "CRUCIBLE"
    },
    isCompleted: false
  },
  {
    id: "quest_agro_ammonia",
    clientName: "GreenBio AgroChem",
    title: "Haber-Bosch Nitrogen Fixation",
    description: "Produce anhydrous ammonia fertilizer precursor gas by stoichiometric catalytic reaction of nitrogen and hydrogen.",
    requirements: {
      targetCompoundId: "NH3",
      targetAmount: 15.0,
      minPurity: 90.0
    },
    rewards: {
      credits: 900,
      unlocksToolId: "GAS_SYRINGE"
    },
    isCompleted: false
  },
  {
    id: "quest_electro_copper",
    clientName: "Metro Power & Grid",
    title: "Electroplating Copper Refining",
    description: "Recover high-purity elemental copper through cathode electrochemical precipitation from copper sulfate electrolyte.",
    requirements: {
      targetCompoundId: "CuSO4",
      targetAmount: 40.0,
      minPurity: 94.0
    },
    rewards: {
      credits: 800,
      unlocksToolId: "ELECTRODES"
    },
    isCompleted: false
  }
];
