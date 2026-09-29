package com.example.ui.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.OrientationSetting
import com.example.ui.theme.AppThemeMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    isLandscape: Boolean = false
) {
    val context = LocalContext.current
    val currentTheme by viewModel.themeMode.collectAsStateWithLifecycle()
    val orientationSetting by viewModel.orientationSetting.collectAsStateWithLifecycle()
    val githubStatus by viewModel.githubReleaseStatus.collectAsStateWithLifecycle()
    val isCheckingRelease by viewModel.isCheckingRelease.collectAsStateWithLifecycle()
    val reactionLogs by viewModel.reactionLogs.collectAsStateWithLifecycle()

    var showResetDialog by remember { mutableStateOf(false) }
    var showLogsDialog by remember { mutableStateOf(false) }
    var showReadmeChangelogDialog by remember { mutableStateOf(false) }

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copied: $text", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Title & Studio Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🧪", fontSize = 20.sp)
                    }
                    Column {
                        Text(
                            text = "Elementa",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Developed by Astilla Softwares",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Text(
                    text = "A high-performance 2D laboratory chemistry simulation and apparatus management tool.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 1. SUPPORT DEVELOPER SECTION (Tipping & Donation)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Support",
                        tint = Color(0xFFEF4444)
                    )
                    Text(
                        text = "Support Astilla Softwares",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Elementa is independently developed. If you find this chemistry simulator helpful for education or research, donations are greatly appreciated!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // GCash Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF007DFE).copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF007DFE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("G", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("GCash", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text("09193710317", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Button(
                            onClick = { copyToClipboard("GCash", "09193710317") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007DFE)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("copy_gcash_button")
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy", fontSize = 12.sp)
                        }
                    }
                }

                // Maya Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF05B667).copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF05B667)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("M", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Maya", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text("09273352516", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Button(
                            onClick = { copyToClipboard("Maya", "09273352516") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF05B667)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("copy_maya_button")
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 2. THEME SELECTOR
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Palette, contentDescription = "Theme", tint = MaterialTheme.colorScheme.primary)
                    Text(text = "Visual Color Theme", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppThemeMode.values().forEach { mode ->
                        val isSelected = currentTheme == mode
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.setThemeMode(mode) }
                                .testTag("theme_chip_${mode.name}"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(text = mode.icon, fontSize = 20.sp)
                                Text(
                                    text = mode.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. ORIENTATION MODE
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.ScreenRotation, contentDescription = "Orientation", tint = MaterialTheme.colorScheme.primary)
                    Text(text = "Orientation Setting", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                OrientationSetting.values().forEach { setting ->
                    val isSelected = orientationSetting == setting
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.setOrientation(setting) }
                            .testTag("orientation_${setting.name}"),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = setting.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. VERSION & GITHUB RELEASE SYNC
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.SystemUpdate, contentDescription = "Updates", tint = MaterialTheme.colorScheme.primary)
                    Text(text = "Version & GitHub Release", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Installed App Version", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "v1.0.0", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "GitHub Release Sync", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = githubStatus, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontFamily = FontFamily.Monospace)
                    }
                }

                OutlinedButton(
                    onClick = { viewModel.checkGitHubRelease() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("check_github_release_button"),
                    enabled = !isCheckingRelease
                ) {
                    if (isCheckingRelease) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Checking GitHub Releases...")
                    } else {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Sync", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Check for Online Updates")
                    }
                }

                Button(
                    onClick = { showReadmeChangelogDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("view_readme_changelog_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = "Readme", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Read Me & Automatic Changelog", fontWeight = FontWeight.Bold)
                }
            }
        }

        // 5. LAB DATA & REACTION HISTORY
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = "Lab Data & Persistence", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showLogsDialog = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("View Reaction Log (${reactionLogs.size})", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = { showResetDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Reset Discoveries", fontSize = 11.sp)
                    }
                }
            }
        }

        // Compliance Guardrails Notice
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = "Compliance",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "Safe Organic Standard: Sensitive chemical precursors are abstracted to compliant analogs (e.g. Organic Compound Alpha) to ensure educational safety.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                lineHeight = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

    // Confirmation Dialog for Reset
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Compound Discoveries?") },
            text = {
                Text("This will lock all discovered compounds and return to starting fundamental elements (H₂O, O₂, H₂, C, N₂, Fe, Cu, S, Na, HCl, NaOH, CaCO₃).")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetAllDiscoveries()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Reset Everything")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Reaction Logs History Dialog
    if (showLogsDialog) {
        val dateFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

        AlertDialog(
            onDismissRequest = { showLogsDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Reaction History")
                    TextButton(onClick = { viewModel.clearHistoryLogs() }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Clear", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear Logs", fontSize = 11.sp)
                    }
                }
            },
            text = {
                if (reactionLogs.isEmpty()) {
                    Text("No reactions recorded yet. Experiment in the Lab Workbench to produce chemical logs.")
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        reactionLogs.forEach { log ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = log.equation,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = dateFormat.format(Date(log.timestamp)),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = log.observation,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Conditions: ${log.temperature.toInt()}°C | pH ${String.format("%.1f", log.ph)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLogsDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Read Me & Automatic Changelog Dialog
    if (showReadmeChangelogDialog) {
        val fullChangelogText = """
# Elementa — Automatic Changelog

### [v1.3.0] — 2026-09-29
- 100 New Chemical Reactions Generated: Expanded simulation engine to 205+ total reactions (rxn_106 through rxn_205), including Prussian Blue, Elephant's Toothpaste, Thermite, Chemical Gardens, and more.
- 95+ New Chemical Compounds: Added MoreExtendedChemicals.kt providing full physical, thermodynamic, and hazard specs for all newly synthesizable species.
- Complete Reaction Guide: Reaction Notebook now catalogs over 238 total reactions ready to simulate with 1-click vessel loading.

### [v1.2.0] — 2026-09-29
- Full 105 Reactions Engine Integration: All 105 reactions from chemistry_reactions_105.json and seed reactions are compiled into AllReactionsCatalog.kt and wired into ChemistryEngine.kt.
- Extended Compounds Registry: Created ExtendedChemicals.kt covering 70 previously missing compound species (AgNO₃, CuSO₄, Pb(NO₃)₂, KMnO₄, AlCl₃, BaSO₄, CH₃COOH, C₃H₈, etc.).
- Simulation Readiness Evaluation: Introduced Compound data schema with isSimulatable validation checking physical parameter completeness.
- Automatic Changelog Workflow: Automated README changelog generator via GitHub Actions.

### [v1.1.0] — 2026-09-29
- 100 Dynamic Chemistry Reactions: Integrated chemistry_reactions_100.json with temperature triggers, catalysts, flame colors, and gas emission tracking.
- Interactive Reaction Suggestions: Workbench automatically prompts users when reactants in the beaker are close to reaction thresholds.
- Compound Dossier Dialog: Added detailed inspection modal for chemical formulas, atomic numbers, element families, and GHS handling symbols.

### [v1.0.0] — 2026-09-28
- Periodic Table of Elements: Full dataset of 118 elements categorized by family.
- 2D Particle Simulation Workbench: Interactive canvas supporting drag-and-pour chemistry.
- Support Developer Affordance: Integrated GCash and Maya tipping numbers for Astilla Softwares.
- Local Persistence: SQLite/Room database saving discovered compounds and reaction history logs.
        """.trimIndent()

        AlertDialog(
            onDismissRequest = { showReadmeChangelogDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "📖 Read Me & Changelog",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Astilla Softwares • Front of GitHub",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Badge info box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Current Release:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                Text("v1.3.0 (Latest)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            Text(
                                text = "Automated GitHub workflow syncs this changelog directly to the repository front README.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Changelog entries
                    Text(
                        text = "📜 Release History",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // v1.3.0
                    ChangelogCard(
                        version = "v1.3.0 (Current)",
                        date = "2026-09-29",
                        highlights = listOf(
                            "100 New Chemical Reactions: Expanded from 105 to 205+ reactions (rxn_106 through rxn_205) ready to simulate.",
                            "95+ New Compounds in MoreExtendedChemicals.kt with complete physical and hazard properties.",
                            "Reactions across Organic Synthesis, Thermite, Coordination Chemistry, and Electrochemistry.",
                            "Full 1-click loading into workbench reaction vessel."
                        ),
                        isLatest = true
                    )

                    // v1.2.0
                    ChangelogCard(
                        version = "v1.2.0",
                        date = "2026-09-29",
                        highlights = listOf(
                            "Full 105 Reactions Engine: All reactions compiled into AllReactionsCatalog.kt and actively simulated.",
                            "70 Extended Compounds: Added AgNO₃, CuSO₄, KMnO₄, Pb(NO₃)₂, etc., with physical and hazard profiles.",
                            "Simulation Readiness: Compound schema with isSimulatable parameter validation.",
                            "Automated GitHub Actions workflow for README changelog updating."
                        ),
                        isLatest = false
                    )

                    // v1.1.0
                    ChangelogCard(
                        version = "v1.1.0",
                        date = "2026-09-29",
                        highlights = listOf(
                            "100 Dynamic reactions JSON dataset integration.",
                            "Proximity reaction suggestion engine and activation prompt banners.",
                            "Chemical Dossier dialog with GHS safety symbols and atomic metadata."
                        ),
                        isLatest = false
                    )

                    // v1.0.0
                    ChangelogCard(
                        version = "v1.0.0",
                        date = "2026-09-28",
                        highlights = listOf(
                            "Initial release with all 118 periodic table elements.",
                            "2D particle simulation engine with phase transitions.",
                            "Astilla Softwares developer tipping support (GCash/Maya).",
                            "Room database offline Pokedex discovery tracking."
                        ),
                        isLatest = false
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        copyToClipboard("Changelog", fullChangelogText)
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy Changelog")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReadmeChangelogDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun ChangelogCard(
    version: String,
    date: String,
    highlights: List<String>,
    isLatest: Boolean
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isLatest) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = if (isLatest) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)) else null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = version, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(text = date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            highlights.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text("•", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
