package com.example.ui.lab

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import com.example.data.model.DispensedChemical
import com.example.ui.components.QuantitativeDispenserModal
import com.example.ui.components.QuantitativeLogDrawer
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import com.example.data.model.ChemicalCategory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ChemicalCatalog
import com.example.data.model.LabToolType
import com.example.ui.MainViewModel
import com.example.ui.components.LabCanvas
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LabWorkbenchScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    isLandscape: Boolean = false
) {
    val activeTool by viewModel.activeTool.collectAsStateWithLifecycle()
    val activeChemicals by viewModel.activeChemicals.collectAsStateWithLifecycle()
    val dispensedChemicals by viewModel.dispensedChemicals.collectAsStateWithLifecycle()
    val latestStoichiometryResult by viewModel.latestStoichiometryResult.collectAsStateWithLifecycle()
    val temperature by viewModel.currentTemperature.collectAsStateWithLifecycle()
    val pressure by viewModel.currentPressure.collectAsStateWithLifecycle()
    val isHeating by viewModel.isHeating.collectAsStateWithLifecycle()
    val isElectricityActive by viewModel.isElectricityActive.collectAsStateWithLifecycle()
    val isCentrifuging by viewModel.isCentrifuging.collectAsStateWithLifecycle()
    val currentPh by viewModel.currentPh.collectAsStateWithLifecycle()
    val liquidColorHex by viewModel.liquidColorHex.collectAsStateWithLifecycle()
    val hasPrecipitate by viewModel.hasPrecipitate.collectAsStateWithLifecycle()
    val hasUnreactedSolid by viewModel.hasUnreactedSolid.collectAsStateWithLifecycle()
    val unreactedSolidColorHex by viewModel.unreactedSolidColorHex.collectAsStateWithLifecycle()
    val liquidAlpha by viewModel.liquidAlpha.collectAsStateWithLifecycle()
    val isReacting by viewModel.isReacting.collectAsStateWithLifecycle()
    val reactionAlert by viewModel.reactionAlert.collectAsStateWithLifecycle()
    val engineState by viewModel.engineState.collectAsStateWithLifecycle()

    androidx.compose.runtime.LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(16)
            viewModel.stepSimulation(0.016f)
        }
    }

    var showChemicalSelector by remember { mutableStateOf(false) }
    var showReactionGuide by remember { mutableStateOf(false) }
    var pendingDispenseChemical by remember { mutableStateOf<com.example.data.model.Chemical?>(null) }

    val liquidFill = if (activeChemicals.isEmpty()) 0.0f else (activeChemicals.values.sum() / 250f).coerceIn(0.18f, 0.85f)

    if (isLandscape) {
        // LANDSCAPE: Dual Pane Layout (Canvas on Left, Controls on Right)
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Left Pane: Lab Canvas + Tool Selector
            Card(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxHeight(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    WorkbenchHeader(
                        activeTool = activeTool,
                        onSelectTool = { viewModel.selectTool(it) },
                        onClear = { viewModel.clearWorkbench() },
                        onOpenGuide = { showReactionGuide = true }
                    )

                    StatusTelemetryRow(
                        temperature = temperature,
                        ph = currentPh,
                        pressure = pressure
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        LabCanvas(
                            activeTool = activeTool,
                            liquidColor = Color(liquidColorHex),
                            liquidFillPercent = liquidFill,
                            temperature = temperature,
                            isHeating = isHeating,
                            isElectricityActive = isElectricityActive,
                            isCentrifuging = isCentrifuging,
                            hasPrecipitate = hasPrecipitate,
                            hasUnreactedSolid = hasUnreactedSolid,
                            unreactedSolidColor = Color(unreactedSolidColorHex),
                            transparencyAlpha = liquidAlpha,
                            isReacting = isReacting,
                            particles = engineState.particles,
                            potentialInteractions = engineState.potentialInteractions
                        )
                    }

                    if (latestStoichiometryResult != null) {
                        QuantitativeLogDrawer(
                            stoichiometryResult = latestStoichiometryResult,
                            onDismiss = { viewModel.dismissStoichiometryResult() },
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            // Right Pane: Chemical Dispenser & Physical Controls
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    ReactionBanner(
                        alert = reactionAlert,
                        onDismiss = { viewModel.dismissReactionAlert() }
                    )

                    ActiveContentsSection(
                        chemicals = activeChemicals,
                        dispensedChemicals = dispensedChemicals,
                        onRemove = { viewModel.removeChemical(it) },
                        onOpenAdd = { showChemicalSelector = true },
                        onEditDispense = { pendingDispenseChemical = it }
                    )

                    ReactionControlsSection(
                        temperature = temperature,
                        onTempChange = { viewModel.setTemperature(it) },
                        isHeating = isHeating,
                        onToggleHeating = { viewModel.toggleHeating() },
                        isElectricity = isElectricityActive,
                        onToggleElectricity = { viewModel.toggleElectricity() },
                        isCentrifuging = isCentrifuging,
                        onToggleCentrifuge = { viewModel.toggleCentrifuge() },
                        activeTool = activeTool,
                        onReact = { viewModel.triggerReaction() }
                    )
                }
            }
        }
    } else {
        // PORTRAIT: Vertical Scrollable Stack
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WorkbenchHeader(
                activeTool = activeTool,
                onSelectTool = { viewModel.selectTool(it) },
                onClear = { viewModel.clearWorkbench() },
                onOpenGuide = { showReactionGuide = true }
            )

            // Canvas Display Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    StatusTelemetryRow(
                        temperature = temperature,
                        ph = currentPh,
                        pressure = pressure
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        LabCanvas(
                            activeTool = activeTool,
                            liquidColor = Color(liquidColorHex),
                            liquidFillPercent = liquidFill,
                            temperature = temperature,
                            isHeating = isHeating,
                            isElectricityActive = isElectricityActive,
                            isCentrifuging = isCentrifuging,
                            hasPrecipitate = hasPrecipitate,
                            hasUnreactedSolid = hasUnreactedSolid,
                            unreactedSolidColor = Color(unreactedSolidColorHex),
                            transparencyAlpha = liquidAlpha,
                            isReacting = isReacting,
                            particles = engineState.particles,
                            potentialInteractions = engineState.potentialInteractions
                        )
                    }
                }
            }

            if (latestStoichiometryResult != null) {
                QuantitativeLogDrawer(
                    stoichiometryResult = latestStoichiometryResult,
                    onDismiss = { viewModel.dismissStoichiometryResult() }
                )
            }

            ReactionBanner(
                alert = reactionAlert,
                onDismiss = { viewModel.dismissReactionAlert() }
            )

            ActiveContentsSection(
                chemicals = activeChemicals,
                dispensedChemicals = dispensedChemicals,
                onRemove = { viewModel.removeChemical(it) },
                onOpenAdd = { showChemicalSelector = true },
                onEditDispense = { pendingDispenseChemical = it }
            )

            ReactionControlsSection(
                temperature = temperature,
                onTempChange = { viewModel.setTemperature(it) },
                isHeating = isHeating,
                onToggleHeating = { viewModel.toggleHeating() },
                isElectricity = isElectricityActive,
                onToggleElectricity = { viewModel.toggleElectricity() },
                isCentrifuging = isCentrifuging,
                onToggleCentrifuge = { viewModel.toggleCentrifuge() },
                activeTool = activeTool,
                onReact = { viewModel.triggerReaction() }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Chemical Selector Modal Bottom Sheet
    if (showChemicalSelector) {
        ChemicalDispenserSheet(
            onDismiss = { showChemicalSelector = false },
            onSelectChemical = { chem ->
                showChemicalSelector = false
                pendingDispenseChemical = chem
            }
        )
    }

    // Quantitative Measurement & Dispenser Modal (Apparatuses: Balance, Cylinder, Syringe)
    if (pendingDispenseChemical != null) {
        QuantitativeDispenserModal(
            chemical = pendingDispenseChemical!!,
            onDismiss = { pendingDispenseChemical = null },
            onConfirmDispense = { dispensed ->
                viewModel.dispenseChemical(dispensed)
                pendingDispenseChemical = null
            }
        )
    }

    // Reaction Guide Modal Bottom Sheet
    if (showReactionGuide) {
        ReactionNotebookSheet(
            onDismiss = { showReactionGuide = false },
            onLoadReaction = { rx ->
                viewModel.loadReactionReactants(rx)
                showReactionGuide = false
            }
        )
    }
}

@Composable
private fun WorkbenchHeader(
    activeTool: LabToolType,
    onSelectTool: (LabToolType) -> Unit,
    onClear: () -> Unit,
    onOpenGuide: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Workbench Canvas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Equipped: ${activeTool.title}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(
                    onClick = onOpenGuide,
                    modifier = Modifier.testTag("reaction_guide_button")
                ) {
                    Text("📖 Reactions", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onClear,
                    modifier = Modifier.testTag("clear_workbench_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Wash", fontSize = 12.sp)
                }
            }
        }

        // Horizontal chips to quickly change glassware
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LabToolType.values().forEach { tool ->
                FilterChip(
                    selected = activeTool == tool,
                    onClick = { onSelectTool(tool) },
                    label = { Text(tool.title, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    }
}

@Composable
private fun StatusTelemetryRow(
    temperature: Double,
    ph: Double,
    pressure: Double
) {
    val phColor = when {
        ph < 6.0 -> Color(0xFFEF4444) // Acidic Red
        ph > 8.0 -> Color(0xFF3B82F6) // Alkaline Blue
        else -> Color(0xFF10B981) // Neutral Green
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Temperature Gauge
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "🌡️ ${temperature.roundToInt()}°C",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace,
                color = if (temperature > 100.0) Color(0xFFF97316) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // pH Indicator
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(phColor)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "pH ${String.format("%.1f", ph)}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = phColor
            )
        }

        // Pressure
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "⚡ ${String.format("%.1f", pressure)} atm",
                style = MaterialTheme.typography.labelMedium,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ActiveContentsSection(
    chemicals: Map<String, Int>,
    dispensedChemicals: Map<String, DispensedChemical> = emptyMap(),
    onRemove: (String) -> Unit,
    onOpenAdd: () -> Unit,
    onEditDispense: (com.example.data.model.Chemical) -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Vessel Contents (Measured)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (chemicals.isEmpty()) "Empty" else "${chemicals.size} reagent(s) in container",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Button(
                    onClick = onOpenAdd,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.testTag("add_chemical_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Reagent", fontSize = 12.sp)
                }
            }

            if (chemicals.isEmpty()) {
                Text(
                    text = "Apparatus is empty. Tap '+ Add Reagent' to measure and dispense reactants.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    chemicals.forEach { (chemId, amount) ->
                        val chem = ChemicalCatalog.getChemical(chemId)
                        val name = chem?.formula ?: chemId
                        val dispensed = dispensedChemicals[chemId]

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.clickable {
                                if (chem != null) onEditDispense(chem)
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color(chem?.colorHex ?: 0xFF0284C7L))
                                )
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = name,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (dispensed != null) {
                                            Text(
                                                text = dispensed.apparatusType.iconEmoji,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                    Text(
                                        text = dispensed?.displayCompact ?: "${amount}mL",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(
                                    onClick = { onRemove(chemId) },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReactionControlsSection(
    temperature: Double,
    onTempChange: (Double) -> Unit,
    isHeating: Boolean,
    onToggleHeating: () -> Unit,
    isElectricity: Boolean,
    onToggleElectricity: () -> Unit,
    isCentrifuging: Boolean,
    onToggleCentrifuge: () -> Unit,
    activeTool: LabToolType,
    onReact: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Environmental Parameters",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            // Temperature Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Thermal Energy: ${temperature.roundToInt()}°C",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (temperature > 100) "Boiling/Combustion" else "Room Temperature",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Slider(
                    value = temperature.toFloat(),
                    onValueChange = { onTempChange(it.toDouble()) },
                    valueRange = 0f..1000f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            // Quick Energy Toggles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Bunsen Burner Flame Toggle
                OutlinedButton(
                    onClick = onToggleHeating,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("toggle_heat_button"),
                    colors = if (isHeating) {
                        ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xFFF97316).copy(alpha = 0.2f),
                            contentColor = Color(0xFFF97316)
                        )
                    } else {
                        ButtonDefaults.outlinedButtonColors()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Burner",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isHeating) "Extinguish" else "Ignite Flame", fontSize = 11.sp)
                }

                // Electricity Toggle (if tool is Electrodes or general)
                if (activeTool == LabToolType.ELECTRODES || activeTool == LabToolType.BEAKER) {
                    OutlinedButton(
                        onClick = onToggleElectricity,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("toggle_electricity_button"),
                        colors = if (isElectricity) {
                            ButtonDefaults.outlinedButtonColors(
                                containerColor = Color(0xFF38BDF8).copy(alpha = 0.2f),
                                contentColor = Color(0xFF0284C7)
                            )
                        } else {
                            ButtonDefaults.outlinedButtonColors()
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Bolt, contentDescription = "Electrify", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isElectricity) "Current: ON" else "DC Current", fontSize = 11.sp)
                    }
                }

                // Centrifuge Rotor Spin Toggle
                if (activeTool == LabToolType.CENTRIFUGE) {
                    OutlinedButton(
                        onClick = onToggleCentrifuge,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("toggle_centrifuge_button"),
                        colors = if (isCentrifuging) {
                            ButtonDefaults.outlinedButtonColors(
                                containerColor = Color(0xFF10B981).copy(alpha = 0.2f),
                                contentColor = Color(0xFF10B981)
                            )
                        } else {
                            ButtonDefaults.outlinedButtonColors()
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Sync, contentDescription = "Centrifuge", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isCentrifuging) "Spinning..." else "4000 RPM", fontSize = 11.sp)
                    }
                }
            }

            // PRIMARY ACTION BUTTON: REACT / MIX
            Button(
                onClick = onReact,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("react_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Science,
                    contentDescription = "Mix Reagents",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "React / Mix Reagents",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ReactionBanner(
    alert: com.example.ui.ReactionAlert?,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = alert != null,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut()
    ) {
        if (alert != null) {
            val isCelebration = alert.newlyDiscoveredNames.isNotEmpty()
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reaction_alert_card"),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCelebration) {
                        Color(0xFF047857).copy(alpha = 0.95f) // Glowing Emerald
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isCelebration) "✨ NEW COMPOUND DISCOVERED!" else "Chemical Reaction Log",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isCelebration) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = if (isCelebration) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Text(
                        text = alert.equation,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = if (isCelebration) Color(0xFFD1FAE5) else MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = alert.observation,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isCelebration) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (isCelebration) {
                        Text(
                            text = "Unlocked in Compounds Pokedex: ${alert.newlyDiscoveredNames.joinToString(", ")}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFDE047)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun ChemicalDispenserSheet(
    onDismiss: () -> Unit,
    onSelectChemical: (com.example.data.model.Chemical) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All Elements (118)") }

    androidx.compose.material3.ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Chemical & Element Dispenser",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            // Search bar for dispenser
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search 118 elements & compounds (e.g. Au, Uranium, H₂O)...", fontSize = 12.sp) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Science, contentDescription = "Search", modifier = Modifier.size(16.dp))
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Category filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("All Elements (118)", "Common Reagents", "Metals", "Nonmetals", "Gases", "Acids & Bases").forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            val filtered = remember(searchQuery, selectedCategory) {
                ChemicalCatalog.ALL_CHEMICALS.filter { chem ->
                    val matchesSearch = chem.name.contains(searchQuery, ignoreCase = true) ||
                            chem.formula.contains(searchQuery, ignoreCase = true) ||
                            (chem.symbol?.contains(searchQuery, ignoreCase = true) == true)

                    val matchesCat = when (selectedCategory) {
                        "All Elements (118)" -> chem.category == ChemicalCategory.ELEMENT
                        "Common Reagents" -> chem.isPreUnlocked
                        "Metals" -> chem.elementSeries?.contains("Metal") == true
                        "Nonmetals" -> chem.elementSeries?.contains("Nonmetal") == true || chem.elementSeries == "Halogen"
                        "Gases" -> chem.category == ChemicalCategory.GAS || chem.elementSeries == "Noble Gas"
                        "Acids & Bases" -> chem.category == ChemicalCategory.ACID || chem.category == ChemicalCategory.BASE
                        else -> true
                    }
                    matchesSearch && matchesCat
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 24.dp)
            ) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filtered.forEach { chem ->
                        Surface(
                            modifier = Modifier
                                .clickable { onSelectChemical(chem) }
                                .testTag("chemical_chip_${chem.id}"),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(Color(chem.colorHex))
                                )
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = chem.symbol ?: chem.formula,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        if (chem.atomicNumber != null) {
                                            Text(
                                                text = "${chem.atomicNumber}",
                                                fontSize = 9.sp,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Text(
                                        text = chem.name,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun ReactionNotebookSheet(
    onDismiss: () -> Unit,
    onLoadReaction: (com.example.data.model.Reaction) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val allReactions = ChemicalCatalog.REACTIONS

    val filtered = remember(searchQuery) {
        if (searchQuery.isBlank()) allReactions
        else allReactions.filter { rx ->
            rx.equation.contains(searchQuery, ignoreCase = true) ||
            rx.observation.contains(searchQuery, ignoreCase = true) ||
            rx.reactantIds.any { it.contains(searchQuery, ignoreCase = true) }
        }
    }

    androidx.compose.material3.ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Reaction Notebook & Guide",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${allReactions.size} verified synthesis, redox & precipitation reactions",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search reactions (e.g. Thermite, Golden Rain, Silver Tree)...", fontSize = 12.sp) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 24.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    filtered.forEach { rx ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = rx.equation,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = rx.observation,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Req: ${rx.minTemp.toInt()}°C" + if (rx.requiresElectricity) " | ⚡ DC" else "",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Button(
                                        onClick = { onLoadReaction(rx) },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        modifier = Modifier.testTag("load_reaction_${rx.id}")
                                    ) {
                                        Text("Load into Vessel", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
