package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import com.example.data.model.Chemical
import com.example.data.model.ChemicalCategory
import com.example.data.model.DispensedChemical
import com.example.data.model.DispenserApparatusType
import com.example.engine.stoichiometry.StoichiometryEngine
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuantitativeDispenserModal(
    chemical: Chemical,
    onDismiss: () -> Unit,
    onConfirmDispense: (DispensedChemical) -> Unit
) {
    // Determine default apparatus based on physical state
    val defaultApparatus = remember(chemical) {
        when {
            chemical.category == ChemicalCategory.GAS || chemical.physicalState.contains("Gas", ignoreCase = true) ->
                DispenserApparatusType.GAS_SYRINGE
            chemical.physicalState.contains("Liquid", ignoreCase = true) ||
            chemical.physicalState.contains("Aqueous", ignoreCase = true) ||
            chemical.category == ChemicalCategory.ACID ||
            chemical.category == ChemicalCategory.BASE ->
                DispenserApparatusType.GRADUATED_CYLINDER
            else ->
                DispenserApparatusType.ANALYTICAL_BALANCE
        }
    }

    var selectedApparatus by remember { mutableStateOf(defaultApparatus) }
    var massValueGrams by remember { mutableDoubleStateOf(10.0) }
    var volumeValueMl by remember { mutableDoubleStateOf(50.0) }
    var molarityValue by remember { mutableDoubleStateOf(1.0) }
    var gasVolumeLiters by remember { mutableDoubleStateOf(2.0) }

    val currentMoles = remember(selectedApparatus, massValueGrams, volumeValueMl, molarityValue, gasVolumeLiters) {
        when (selectedApparatus) {
            DispenserApparatusType.ANALYTICAL_BALANCE ->
                StoichiometryEngine.calculateMoles(chemical, selectedApparatus, massValueGrams)
            DispenserApparatusType.GRADUATED_CYLINDER ->
                StoichiometryEngine.calculateMoles(chemical, selectedApparatus, volumeValueMl, molarityValue)
            DispenserApparatusType.GAS_SYRINGE ->
                StoichiometryEngine.calculateMoles(chemical, selectedApparatus, gasVolumeLiters)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("quantitative_dispenser_modal")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color(chemical.colorHex))
                            .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                    )
                    Column {
                        Text(
                            text = chemical.formula,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "${chemical.name} • ${chemical.physicalState}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = onDismiss, modifier = Modifier.testTag("cancel_dispense_button")) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            // Chemical Specs & Molar Mass Badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Molar Mass: ${String.format("%.3f", chemical.molarMass)} g/mol",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = chemical.category.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Apparatus Selector Tabs
            TabRow(
                selectedTabIndex = selectedApparatus.ordinal,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                DispenserApparatusType.values().forEach { appType ->
                    Tab(
                        selected = selectedApparatus == appType,
                        onClick = { selectedApparatus = appType },
                        text = {
                            Text(
                                text = "${appType.iconEmoji} ${appType.defaultUnit}",
                                fontSize = 12.sp,
                                fontWeight = if (selectedApparatus == appType) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag("tab_${appType.name}")
                    )
                }
            }

            // Apparatus Dedicated Controls
            when (selectedApparatus) {
                DispenserApparatusType.ANALYTICAL_BALANCE -> {
                    AnalyticalBalanceSection(
                        mass = massValueGrams,
                        onMassChange = { massValueGrams = it },
                        moles = currentMoles
                    )
                }
                DispenserApparatusType.GRADUATED_CYLINDER -> {
                    GraduatedCylinderSection(
                        volume = volumeValueMl,
                        onVolumeChange = { volumeValueMl = it },
                        molarity = molarityValue,
                        onMolarityChange = { molarityValue = it },
                        moles = currentMoles
                    )
                }
                DispenserApparatusType.GAS_SYRINGE -> {
                    GasSyringeSection(
                        volumeLiters = gasVolumeLiters,
                        onVolumeChange = { gasVolumeLiters = it },
                        moles = currentMoles
                    )
                }
            }

            // Real-Time Stoichiometric Summary Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Stoichiometric Moles (n):",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "${String.format("%.4f", currentMoles)} mol",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = when (selectedApparatus) {
                            DispenserApparatusType.ANALYTICAL_BALANCE -> "${String.format("%.2f", massValueGrams)} g"
                            DispenserApparatusType.GRADUATED_CYLINDER -> "${volumeValueMl.roundToInt()} mL @ ${String.format("%.1f", molarityValue)}M"
                            DispenserApparatusType.GAS_SYRINGE -> "${String.format("%.2f", gasVolumeLiters)} L (STP)"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Dispense Confirm Button
            Button(
                onClick = {
                    val finalAmount = when (selectedApparatus) {
                        DispenserApparatusType.ANALYTICAL_BALANCE -> massValueGrams
                        DispenserApparatusType.GRADUATED_CYLINDER -> volumeValueMl
                        DispenserApparatusType.GAS_SYRINGE -> gasVolumeLiters
                    }
                    val finalMolarity = if (selectedApparatus == DispenserApparatusType.GRADUATED_CYLINDER) molarityValue else null

                    val dispensed = DispensedChemical(
                        chemical = chemical,
                        apparatusType = selectedApparatus,
                        amountValue = finalAmount,
                        molarity = finalMolarity,
                        moles = currentMoles
                    )
                    onConfirmDispense(dispensed)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("confirm_dispense_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(imageVector = Icons.Default.Science, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Dispense into Vessel", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AnalyticalBalanceSection(
    mass: Double,
    onMassChange: (Double) -> Unit,
    moles: Double
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚖️ Digital Analytical Balance",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                OutlinedButton(
                    onClick = { onMassChange(0.0) },
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Tare (0g)", fontSize = 11.sp)
                }
            }

            // LCD Display Simulation
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF0F172A),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "BALANCE [STABLE]",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF38BDF8)
                    )
                    Text(
                        text = "${String.format("%.2f", mass)} g",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF4ADE80)
                    )
                }
            }

            // Slider
            Text(
                text = "Adjust Mass (0.1g - 100.0g):",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Slider(
                value = mass.toFloat().coerceIn(0.1f, 100f),
                onValueChange = { onMassChange((it * 10).roundToInt() / 10.0) },
                valueRange = 0.1f..100f,
                modifier = Modifier.fillMaxWidth().testTag("mass_slider")
            )

            // Spatula Quick Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(0.5, 1.0, 5.0, 10.0, 25.0).forEach { inc ->
                    OutlinedButton(
                        onClick = { onMassChange((mass + inc).coerceAtMost(100.0)) },
                        modifier = Modifier.weight(1f),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("+${inc.toInt().takeIf { it.toDouble() == inc } ?: inc}g", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun GraduatedCylinderSection(
    volume: Double,
    onVolumeChange: (Double) -> Unit,
    molarity: Double,
    onMolarityChange: (Double) -> Unit,
    moles: Double
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "🧪 Graduated Cylinder & Precision Buret",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )

            // Volume display
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF0F172A),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "VOLUME DELIVERED",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF38BDF8)
                    )
                    Text(
                        text = "${volume.roundToInt()} mL",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF38BDF8)
                    )
                }
            }

            Slider(
                value = volume.toFloat().coerceIn(5f, 250f),
                onValueChange = { onVolumeChange(it.roundToInt().toDouble()) },
                valueRange = 5f..250f,
                modifier = Modifier.fillMaxWidth().testTag("volume_slider")
            )

            // Quick Volume presets
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(10.0, 25.0, 50.0, 100.0, 200.0).forEach { v ->
                    OutlinedButton(
                        onClick = { onVolumeChange(v) },
                        modifier = Modifier.weight(1f),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("${v.toInt()}mL", fontSize = 11.sp)
                    }
                }
            }

            // Concentration Molarity Selector
            Text(
                text = "Solution Concentration (Molarity M):",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(0.1, 0.5, 1.0, 2.0, 6.0).forEach { m ->
                    FilterChip(
                        selected = molarity == m,
                        onClick = { onMolarityChange(m) },
                        label = { Text("${m}M", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun GasSyringeSection(
    volumeLiters: Double,
    onVolumeChange: (Double) -> Unit,
    moles: Double
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "💨 Gastight Gas Syringe (STP Molar Volume: 22.414 L/mol)",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )

            // Syringe Volume LCD
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF0F172A),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "VALVE OPEN [STP]",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFFBBF24)
                    )
                    Text(
                        text = "${String.format("%.2f", volumeLiters)} L",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFFBBF24)
                    )
                }
            }

            Slider(
                value = volumeLiters.toFloat().coerceIn(0.1f, 10f),
                onValueChange = { onVolumeChange((it * 10).roundToInt() / 10.0) },
                valueRange = 0.1f..10f,
                modifier = Modifier.fillMaxWidth().testTag("gas_slider")
            )

            // Quick Gas Volume Presets
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(0.5, 1.0, 2.0, 5.0, 10.0).forEach { l ->
                    OutlinedButton(
                        onClick = { onVolumeChange(l) },
                        modifier = Modifier.weight(1f),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("${l.toInt().takeIf { it.toDouble() == l } ?: l}L", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
