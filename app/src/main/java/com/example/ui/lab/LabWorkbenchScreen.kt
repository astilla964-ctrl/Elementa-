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
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
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
import com.example.data.model.ContainerHazardState
import com.example.data.model.ThermalApparatus
import com.example.ui.components.ThermalCurveChart
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
    val targetTemperature by viewModel.targetTemperature.collectAsStateWithLifecycle()
    val thermalApparatus by viewModel.thermalApparatus.collectAsStateWithLifecycle()
    val pressure by viewModel.currentPressure.collectAsStateWithLifecycle()
    val isStopperSealed by viewModel.isStopperSealed.collectAsStateWithLifecycle()
    val isPressureReliefOpen by viewModel.isPressureReliefOpen.collectAsStateWithLifecycle()
    val vacuumPumpActive by viewModel.vacuumPumpActive.collectAsStateWithLifecycle()
    val compressorActive by viewModel.compressorActive.collectAsStateWithLifecycle()
    val containerHazardState by viewModel.containerHazardState.collectAsStateWithLifecycle()
    val hazardMessage by viewModel.hazardMessage.collectAsStateWithLifecycle()
    val tempUnitCelsius by viewModel.tempUnitCelsius.collectAsStateWithLifecycle()
    val pressureUnitAtm by viewModel.pressureUnitAtm.collectAsStateWithLifecycle()
    val thermalHistory by viewModel.thermalHistory.collectAsStateWithLifecycle()
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
    var showThermalLogsSheet by remember { mutableStateOf(false) }
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
                        pressure = pressure,
                        tempUnitCelsius = tempUnitCelsius,
                        pressureUnitAtm = pressureUnitAtm,
                        isStopperSealed = isStopperSealed,
                        containerHazardState = containerHazardState,
                        onToggleTempUnit = { viewModel.toggleTempUnit() },
                        onTogglePressureUnit = { viewModel.togglePressureUnit() },
                        onOpenThermalLogs = { showThermalLogsSheet = true }
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
                            potentialInteractions = engineState.potentialInteractions,
                            thermalApparatus = thermalApparatus,
                            pressure = pressure,
                            isStopperSealed = isStopperSealed,
                            isPressureReliefOpen = isPressureReliefOpen,
                            containerHazardState = containerHazardState,
                            currentPh = currentPh,
                            tempUnitCelsius = tempUnitCelsius,
                            pressureUnitAtm = pressureUnitAtm,
                            onToggleTempUnit = { viewModel.toggleTempUnit() },
                            onTogglePressureUnit = { viewModel.togglePressureUnit() }
                        )
                    }

                    if (latestStoichiometryResult != null) {
                        QuantitativeLogDrawer(
                            stoichiometryResult = latestStoichiometryResult,
                            onDismiss = { viewModel.dismissStoichiometryResult() },
                            readings = thermalHistory,
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
                    HazardAlertBanner(
                        hazardState = containerHazardState,
                        hazardMessage = hazardMessage,
                        onReplaceGlassware = { viewModel.replaceGlassware() }
                    )

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
                        targetTemperature = targetTemperature,
                        onTargetTempChange = { viewModel.setTargetTemperature(it) },
                        thermalApparatus = thermalApparatus,
                        onSelectApparatus = { viewModel.selectThermalApparatus(it) },
                        isHeating = isHeating,
                        onToggleHeating = { viewModel.toggleHeating() },
                        isElectricity = isElectricityActive,
                        onToggleElectricity = { viewModel.toggleElectricity() },
                        isCentrifuging = isCentrifuging,
                        onToggleCentrifuge = { viewModel.toggleCentrifuge() },
                        pressure = pressure,
                        isStopperSealed = isStopperSealed,
                        onToggleStopperSealed = { viewModel.toggleStopperSealed() },
                        isPressureReliefOpen = isPressureReliefOpen,
                        onTogglePressureRelief = { viewModel.togglePressureRelief() },
                        vacuumPumpActive = vacuumPumpActive,
                        onToggleVacuumPump = { viewModel.toggleVacuumPump() },
                        compressorActive = compressorActive,
                        onToggleCompressor = { viewModel.toggleCompressor() },
                        tempUnitCelsius = tempUnitCelsius,
                        onToggleTempUnit = { viewModel.toggleTempUnit() },
                        pressureUnitAtm = pressureUnitAtm,
                        onTogglePressureUnit = { viewModel.togglePressureUnit() },
                        activeTool = activeTool,
                        onReact = { viewModel.triggerReaction() },
                        onOpenThermalLogs = { showThermalLogsSheet = true }
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
                        pressure = pressure,
                        tempUnitCelsius = tempUnitCelsius,
                        pressureUnitAtm = pressureUnitAtm,
                        isStopperSealed = isStopperSealed,
                        containerHazardState = containerHazardState,
                        onToggleTempUnit = { viewModel.toggleTempUnit() },
                        onTogglePressureUnit = { viewModel.togglePressureUnit() },
                        onOpenThermalLogs = { showThermalLogsSheet = true }
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
                            potentialInteractions = engineState.potentialInteractions,
                            thermalApparatus = thermalApparatus,
                            pressure = pressure,
                            isStopperSealed = isStopperSealed,
                            isPressureReliefOpen = isPressureReliefOpen,
                            containerHazardState = containerHazardState,
                            currentPh = currentPh,
                            tempUnitCelsius = tempUnitCelsius,
                            pressureUnitAtm = pressureUnitAtm,
                            onToggleTempUnit = { viewModel.toggleTempUnit() },
                            onTogglePressureUnit = { viewModel.togglePressureUnit() }
                        )
                    }
                }
            }

            HazardAlertBanner(
                hazardState = containerHazardState,
                hazardMessage = hazardMessage,
                onReplaceGlassware = { viewModel.replaceGlassware() }
            )

            if (latestStoichiometryResult != null) {
                QuantitativeLogDrawer(
                    stoichiometryResult = latestStoichiometryResult,
                    onDismiss = { viewModel.dismissStoichiometryResult() },
                    readings = thermalHistory
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
                targetTemperature = targetTemperature,
                onTargetTempChange = { viewModel.setTargetTemperature(it) },
                thermalApparatus = thermalApparatus,
                onSelectApparatus = { viewModel.selectThermalApparatus(it) },
                isHeating = isHeating,
                onToggleHeating = { viewModel.toggleHeating() },
                isElectricity = isElectricityActive,
                onToggleElectricity = { viewModel.toggleElectricity() },
                isCentrifuging = isCentrifuging,
                onToggleCentrifuge = { viewModel.toggleCentrifuge() },
                pressure = pressure,
                isStopperSealed = isStopperSealed,
                onToggleStopperSealed = { viewModel.toggleStopperSealed() },
                isPressureReliefOpen = isPressureReliefOpen,
                onTogglePressureRelief = { viewModel.togglePressureRelief() },
                vacuumPumpActive = vacuumPumpActive,
                onToggleVacuumPump = { viewModel.toggleVacuumPump() },
                compressorActive = compressorActive,
                onToggleCompressor = { viewModel.toggleCompressor() },
                tempUnitCelsius = tempUnitCelsius,
                onToggleTempUnit = { viewModel.toggleTempUnit() },
                pressureUnitAtm = pressureUnitAtm,
                onTogglePressureUnit = { viewModel.togglePressureUnit() },
                activeTool = activeTool,
                onReact = { viewModel.triggerReaction() },
                onOpenThermalLogs = { showThermalLogsSheet = true }
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

    // Thermal Telemetry & Live Heat Curve Bottom Sheet
    if (showThermalLogsSheet) {
        ThermalTelemetryModalSheet(
            readings = thermalHistory,
            currentTemp = temperature,
            targetTemp = targetTemperature,
            currentPressure = pressure,
            currentPh = currentPh,
            apparatus = thermalApparatus,
            activeTool = activeTool,
            isStopperSealed = isStopperSealed,
            hazardState = containerHazardState,
            hazardMessage = hazardMessage,
            tempUnitCelsius = tempUnitCelsius,
            pressureUnitAtm = pressureUnitAtm,
            onToggleTempUnit = { viewModel.toggleTempUnit() },
            onTogglePressureUnit = { viewModel.togglePressureUnit() },
            onDismiss = { showThermalLogsSheet = false }
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
    pressure: Double,
    tempUnitCelsius: Boolean = true,
    pressureUnitAtm: Boolean = true,
    isStopperSealed: Boolean = false,
    containerHazardState: ContainerHazardState = ContainerHazardState.INTACT,
    onToggleTempUnit: () -> Unit = {},
    onTogglePressureUnit: () -> Unit = {},
    onOpenThermalLogs: () -> Unit = {}
) {
    val phColor = when {
        ph < 6.0 -> Color(0xFFEF4444) // Acidic Red
        ph > 8.0 -> Color(0xFF3B82F6) // Alkaline Blue
        else -> Color(0xFF10B981) // Neutral Green
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
        border = if (containerHazardState != ContainerHazardState.INTACT) {
            androidx.compose.foundation.BorderStroke(1.dp, Color(containerHazardState.colorHex))
        } else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth().testTag("status_telemetry_row")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Temperature (Clickable °C/K toggle)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onToggleTempUnit() }
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("🌡️", fontSize = 12.sp)
                val tStr = if (tempUnitCelsius) "${temperature.roundToInt()}°C" else "${(temperature + 273.15).roundToInt()} K"
                Text(
                    text = tStr,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = if (temperature > 100.0) Color(0xFFF97316) else if (temperature < 0.0) Color(0xFF38BDF8) else MaterialTheme.colorScheme.onSurface
                )
            }

            // Pressure (Clickable atm/kPa toggle)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onTogglePressureUnit() }
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(if (isStopperSealed) "🔒" else "⚡", fontSize = 12.sp)
                val pStr = if (pressureUnitAtm) "${String.format("%.1f", pressure)} atm" else "${String.format("%.0f", pressure * 101.3)} kPa"
                Text(
                    text = pStr,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = if (pressure > 3.0) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurface
                )
            }

            // pH dot
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(phColor)
                )
                Text(
                    text = "pH ${String.format("%.1f", ph)}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = phColor
                )
            }

            // Open Live Heat Curve Chart Sheet
            IconButton(
                onClick = onOpenThermalLogs,
                modifier = Modifier.size(24.dp).testTag("open_thermal_logs_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ShowChart,
                    contentDescription = "Thermal Logs",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ReactionControlsSection(
    temperature: Double,
    targetTemperature: Double,
    onTargetTempChange: (Double) -> Unit,
    thermalApparatus: ThermalApparatus,
    onSelectApparatus: (ThermalApparatus) -> Unit,
    isHeating: Boolean,
    onToggleHeating: () -> Unit,
    isElectricity: Boolean,
    onToggleElectricity: () -> Unit,
    isCentrifuging: Boolean,
    onToggleCentrifuge: () -> Unit,
    pressure: Double,
    isStopperSealed: Boolean,
    onToggleStopperSealed: () -> Unit,
    isPressureReliefOpen: Boolean,
    onTogglePressureRelief: () -> Unit,
    vacuumPumpActive: Boolean,
    onToggleVacuumPump: () -> Unit,
    compressorActive: Boolean,
    onToggleCompressor: () -> Unit,
    tempUnitCelsius: Boolean,
    onToggleTempUnit: () -> Unit,
    pressureUnitAtm: Boolean,
    onTogglePressureUnit: () -> Unit,
    activeTool: LabToolType,
    onReact: () -> Unit,
    onOpenThermalLogs: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("reaction_controls_card"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header with Live Heat Curve Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        imageVector = Icons.Default.Thermostat,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Thermodynamic Controls",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                OutlinedButton(
                    onClick = onOpenThermalLogs,
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp).testTag("header_thermal_logs_btn")
                ) {
                    Icon(imageVector = Icons.Default.ShowChart, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Live Curves", fontSize = 11.sp)
                }
            }

            // 1. Thermal Apparatus Selector Base
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Apparatus Base: ${thermalApparatus.title}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ThermalApparatus.values().forEach { app ->
                        FilterChip(
                            selected = thermalApparatus == app,
                            onClick = { onSelectApparatus(app) },
                            label = { Text("${app.iconEmoji} ${app.title.substringBefore(" (")}", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (app.isCooling) Color(0xFF0284C7).copy(alpha = 0.25f)
                                                           else if (app == ThermalApparatus.BUNSEN_BURNER || app == ThermalApparatus.HOT_PLATE) Color(0xFFF97316).copy(alpha = 0.25f)
                                                           else MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = if (app.isCooling) Color(0xFF0284C7)
                                                     else if (app == ThermalApparatus.BUNSEN_BURNER || app == ThermalApparatus.HOT_PLATE) Color(0xFFEA580C)
                                                     else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            // 2. Temperature Output Slider (-196°C to 1500°C)
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val tTargetStr = if (tempUnitCelsius) "${targetTemperature.roundToInt()}°C" else "${(targetTemperature + 273.15).roundToInt()} K"
                    val tCurStr = if (tempUnitCelsius) "${temperature.roundToInt()}°C" else "${(temperature + 273.15).roundToInt()} K"
                    Text(
                        text = "Target: $tTargetStr (Live: $tCurStr)",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (temperature > 500) "Incandescent Glow" else if (temperature > 100) "Boiling / Vapor" else if (temperature < 0) "Freezing" else "Ambient",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (temperature > 100) Color(0xFFF97316) else if (temperature < 0) Color(0xFF38BDF8) else Color(0xFF10B981)
                    )
                }

                Slider(
                    value = targetTemperature.toFloat(),
                    onValueChange = { onTargetTempChange(it.toDouble()) },
                    valueRange = -196f..1500f,
                    colors = SliderDefaults.colors(
                        thumbColor = if (targetTemperature > 100) Color(0xFFF97316) else if (targetTemperature < 0) Color(0xFF38BDF8) else MaterialTheme.colorScheme.primary,
                        activeTrackColor = if (targetTemperature > 100) Color(0xFFF97316) else if (targetTemperature < 0) Color(0xFF38BDF8) else MaterialTheme.colorScheme.primary
                    )
                )

                // Quick Temperature Preset Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        Pair(-196.0, "⚡ Cryo -196°"),
                        Pair(0.0, "🧊 Ice 0°"),
                        Pair(25.0, "🍃 Room 25°"),
                        Pair(100.0, "♨️ Boil 100°"),
                        Pair(450.0, "🔥 Flame 450°"),
                        Pair(1000.0, "💥 Glow 1000°")
                    ).forEach { (deg, label) ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                            modifier = Modifier.clickable { onTargetTempChange(deg) }
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // 3. Pressure & Sealed Container Equipment
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Pressure & Sealed Atmosphere Equipment",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Stopper & Pressure Gauge Toggle Button
                    OutlinedButton(
                        onClick = onToggleStopperSealed,
                        modifier = Modifier.weight(1f).testTag("toggle_stopper_sealed_button"),
                        colors = if (isStopperSealed) {
                            ButtonDefaults.outlinedButtonColors(
                                containerColor = Color(0xFFFBBF24).copy(alpha = 0.2f),
                                contentColor = Color(0xFFB45309)
                            )
                        } else ButtonDefaults.outlinedButtonColors()
                    ) {
                        Icon(
                            imageVector = if (isStopperSealed) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isStopperSealed) "Stopper: Sealed" else "Stopper: Open", fontSize = 11.sp)
                    }

                    // Pressure Relief Valve Toggle Button
                    OutlinedButton(
                        onClick = onTogglePressureRelief,
                        modifier = Modifier.weight(1f).testTag("toggle_relief_valve_button"),
                        colors = if (isPressureReliefOpen) {
                            ButtonDefaults.outlinedButtonColors(
                                containerColor = Color(0xFF10B981).copy(alpha = 0.2f),
                                contentColor = Color(0xFF047857)
                            )
                        } else ButtonDefaults.outlinedButtonColors()
                    ) {
                        Icon(imageVector = Icons.Default.Air, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isPressureReliefOpen) "Relief: Venting" else "Relief Valve", fontSize = 11.sp)
                    }
                }

                // Vacuum Pump & Compressor Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Vacuum Pump Button
                    OutlinedButton(
                        onClick = onToggleVacuumPump,
                        modifier = Modifier.weight(1f).testTag("toggle_vacuum_pump_button"),
                        colors = if (vacuumPumpActive) {
                            ButtonDefaults.outlinedButtonColors(
                                containerColor = Color(0xFF38BDF8).copy(alpha = 0.2f),
                                contentColor = Color(0xFF0284C7)
                            )
                        } else ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text(if (vacuumPumpActive) "⚡ Vacuum Active" else "Vacuum Pump", fontSize = 11.sp)
                    }

                    // Compressor Button
                    OutlinedButton(
                        onClick = onToggleCompressor,
                        modifier = Modifier.weight(1f).testTag("toggle_compressor_button"),
                        colors = if (compressorActive) {
                            ButtonDefaults.outlinedButtonColors(
                                containerColor = Color(0xFFEF4444).copy(alpha = 0.2f),
                                contentColor = Color(0xFFDC2626)
                            )
                        } else ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text(if (compressorActive) "⚡ Compressing..." else "Compressor", fontSize = 11.sp)
                    }
                }
            }

            // 4. Auxiliary Reaction Energy Toggles (Electrodes / Centrifuge)
            if (activeTool == LabToolType.ELECTRODES || activeTool == LabToolType.BEAKER || activeTool == LabToolType.CENTRIFUGE) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (activeTool == LabToolType.ELECTRODES || activeTool == LabToolType.BEAKER) {
                        OutlinedButton(
                            onClick = onToggleElectricity,
                            modifier = Modifier.weight(1f).testTag("toggle_electricity_button"),
                            colors = if (isElectricity) {
                                ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color(0xFF38BDF8).copy(alpha = 0.2f),
                                    contentColor = Color(0xFF0284C7)
                                )
                            } else ButtonDefaults.outlinedButtonColors()
                        ) {
                            Icon(imageVector = Icons.Default.Bolt, contentDescription = "Electrify", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isElectricity) "Current: ON" else "DC Current", fontSize = 11.sp)
                        }
                    }

                    if (activeTool == LabToolType.CENTRIFUGE) {
                        OutlinedButton(
                            onClick = onToggleCentrifuge,
                            modifier = Modifier.weight(1f).testTag("toggle_centrifuge_button"),
                            colors = if (isCentrifuging) {
                                ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color(0xFF10B981).copy(alpha = 0.2f),
                                    contentColor = Color(0xFF10B981)
                                )
                            } else ButtonDefaults.outlinedButtonColors()
                        ) {
                            Icon(imageVector = Icons.Default.Sync, contentDescription = "Centrifuge", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isCentrifuging) "Spinning..." else "4000 RPM", fontSize = 11.sp)
                        }
                    }
                }
            }

            // 5. PRIMARY ACTION BUTTON: REACT / MIX
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
private fun HazardAlertBanner(
    hazardState: ContainerHazardState,
    hazardMessage: String?,
    onReplaceGlassware: () -> Unit
) {
    if (hazardState == ContainerHazardState.INTACT && hazardMessage == null) return

    val isRuptured = hazardState == ContainerHazardState.RUPTURED_EXPLODED
    val color = Color(hazardState.colorHex)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hazard_alert_banner"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = if (isRuptured) 0.95f else 0.15f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (isRuptured) Color.White else color,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = hazardState.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isRuptured) Color.White else color
                )
            }

            if (hazardMessage != null) {
                Text(
                    text = hazardMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isRuptured) Color.White.copy(alpha = 0.95f) else MaterialTheme.colorScheme.onSurface
                )
            }

            if (isRuptured) {
                Button(
                    onClick = onReplaceGlassware,
                    modifier = Modifier.fillMaxWidth().testTag("replace_glassware_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF991B1B)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Clean Up & Replace Glassware", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun ThermalTelemetryModalSheet(
    readings: List<com.example.data.model.ThermodynamicReading>,
    currentTemp: Double,
    targetTemp: Double,
    currentPressure: Double,
    currentPh: Double,
    apparatus: ThermalApparatus,
    activeTool: LabToolType,
    isStopperSealed: Boolean,
    hazardState: ContainerHazardState,
    hazardMessage: String?,
    tempUnitCelsius: Boolean,
    pressureUnitAtm: Boolean,
    onToggleTempUnit: () -> Unit,
    onTogglePressureUnit: () -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.material3.ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Default.ShowChart, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Thermodynamic Sensor Telemetry", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            // Live Heat Curve Chart
            ThermalCurveChart(
                readings = readings,
                tempUnitCelsius = tempUnitCelsius,
                pressureUnitAtm = pressureUnitAtm
            )

            // Equipment Limit Specifications Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Vessel Physical Constraints (${activeTool.title}):",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text("• Safe Thermal Limit: ${activeTool.maxSafeTempC.roundToInt()}°C (Crack at ${activeTool.thermalCrackTempC.roundToInt()}°C)", fontSize = 11.sp)
                    Text("• Maximum Internal Pressure: ${String.format("%.1f", activeTool.maxSafePressureAtm)} atm (Burst at ${String.format("%.1f", activeTool.rupturePressureAtm)} atm)", fontSize = 11.sp)
                    Text("• Active Thermal Base: ${apparatus.title} (${apparatus.minTemp.toInt()}°C to ${apparatus.maxTemp.toInt()}°C)", fontSize = 11.sp)
                    Text("• Containment State: " + if (isStopperSealed) "Sealed Stopper (PV = nRT)" else "Open Mouth (1.0 atm equilibrium)", fontSize = 11.sp)
                    if (hazardMessage != null) {
                        Text("• Active Alert: $hazardMessage", fontSize = 11.sp, color = Color(hazardState.colorHex), fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
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
