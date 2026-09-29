package com.example.data.model

enum class ChangeType(
    val code: String,
    val title: String,
    val badgeColorHex: Long
) {
    FEAT("feat", "FEAT", 0xFF10B981L),       // Emerald Green
    FIX("fix", "FIX", 0xFFEF4444L),          // Red
    REFACTOR("refactor", "REFACTOR", 0xFF8B5CF6L), // Violet / Purple
    UI("ui", "UI", 0xFF38BDF8L)              // Sky Blue
}

data class ChangeItem(
    val type: ChangeType,
    val description: String
)

data class ChangelogEntry(
    val version: String,
    val date: String,
    val changes: List<ChangeItem>
)

/**
 * In-App Changelog Registry tracking build and release history.
 * Mirrors src/data/changelog.ts.
 */
object ChangelogRegistry {
    val CHANGELOG_HISTORY: List<ChangelogEntry> = listOf(
        ChangelogEntry(
            version = "Elementa v1.6.0 (Build 5)",
            date = "2026-09-30",
            changes = listOf(
                ChangeItem(ChangeType.FEAT, "Quest & Career Mode with client contracts schema, requirements, and credit rewards"),
                ChangeItem(ChangeType.FEAT, "Offline Room database persistence for completed assignments and lifetime career statistics"),
                ChangeItem(ChangeType.UI, "Dedicated Career & Lab Assignments screen with career rank ladders and contract inspector")
            )
        ),
        ChangelogEntry(
            version = "Elementa v1.5.0 (Build 4)",
            date = "2026-09-30",
            changes = listOf(
                ChangeItem(ChangeType.FEAT, "Environmental & thermodynamics simulation engine (Bunsen burner, hot plate, cryo dewar)"),
                ChangeItem(ChangeType.FEAT, "Gastight stopper & calibrated dial pressure gauge with explosion mechanics"),
                ChangeItem(ChangeType.UI, "Interactive live heat curves (Temp vs. Time) and incandescent glow heat maps")
            )
        ),
        ChangelogEntry(
            version = "Elementa v1.4.0 (Build 3)",
            date = "2026-09-29",
            changes = listOf(
                ChangeItem(ChangeType.FEAT, "Quantitative dispensing apparatuses (Analytical Balance, Graduated Cylinder, Gas Syringe)"),
                ChangeItem(ChangeType.FEAT, "Real-time stoichiometric engine calculating limiting reagents and exact yields"),
                ChangeItem(ChangeType.UI, "Precipitate sediment beds, unreacted excess solids, and dynamic color blending")
            )
        ),
        ChangelogEntry(
            version = "Elementa v1.0.0 (Build 1)",
            date = "2026-09-30",
            changes = listOf(
                ChangeItem(ChangeType.FEAT, "Initial release of Elementa by Astilla Softwares"),
                ChangeItem(ChangeType.FEAT, "Stoichiometry & thermodynamics simulation engine"),
                ChangeItem(ChangeType.FEAT, "Compounds Pokedex discovery view")
            )
        )
    )

    fun toTypeScriptCode(): String {
        val sb = StringBuilder()
        sb.append("export interface ChangelogEntry {\n")
        sb.append("  version: string;\n")
        sb.append("  date: string;\n")
        sb.append("  changes: {\n")
        sb.append("    type: 'feat' | 'fix' | 'refactor' | 'ui';\n")
        sb.append("    description: string;\n")
        sb.append("  }[];\n")
        sb.append("}\n\n")
        sb.append("export const CHANGELOG_HISTORY: ChangelogEntry[] = [\n")
        CHANGELOG_HISTORY.forEachIndexed { i, entry ->
            sb.append("  {\n")
            sb.append("    version: \"${entry.version}\",\n")
            sb.append("    date: \"${entry.date}\",\n")
            sb.append("    changes: [\n")
            entry.changes.forEachIndexed { j, change ->
                val comma = if (j < entry.changes.size - 1) "," else ""
                sb.append("      { type: \"${change.type.code}\", description: \"${change.description.replace("\"", "\\\"")}\" }$comma\n")
            }
            sb.append("    ]\n")
            sb.append("  }${if (i < CHANGELOG_HISTORY.size - 1) "," else ""}\n")
        }
        sb.append("];\n")
        return sb.toString()
    }
}
