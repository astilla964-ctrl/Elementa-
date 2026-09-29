package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.data.model.ChemicalCatalog
import com.example.data.model.StoichiometryResult
import com.example.data.model.ThermodynamicReading

@Composable
fun QuantitativeLogDrawer(
    stoichiometryResult: StoichiometryResult?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    readings: List<ThermodynamicReading> = emptyList()
) {
    if (stoichiometryResult == null) return

    var isExpanded by remember { mutableStateOf(true) }
    val limitingChem = remember(stoichiometryResult) {
        ChemicalCatalog.getChemical(stoichiometryResult.limitingReagentId)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("quantitative_log_drawer"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Bar with Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Column {
                        Text(
                            text = "Quantitative Stoichiometric Log",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (stoichiometryResult.isExactStoichiometricRatio) "100% Balanced • Zero Leftover Excess"
                                   else "Limiting Reagent: ${limitingChem?.formula ?: stoichiometryResult.limitingReagentId}",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (stoichiometryResult.isExactStoichiometricRatio) Color(0xFF10B981) else Color(0xFFF59E0B),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier.size(28.dp).testTag("toggle_log_expand")
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Collapse" else "Expand"
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp).testTag("dismiss_quantitative_log")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(16.dp))
                    }
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Equation Banner
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stoichiometryResult.reaction.equation,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    // 1. Limiting Reagent Banner
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (stoichiometryResult.isExactStoichiometricRatio) {
                            Color(0xFF047857).copy(alpha = 0.2f)
                        } else {
                            Color(0xFFEF4444).copy(alpha = 0.15f)
                        },
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (stoichiometryResult.isExactStoichiometricRatio) Color(0xFF10B981) else Color(0xFFEF4444)
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("limiting_reagent_banner")
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (stoichiometryResult.isExactStoichiometricRatio) Color(0xFF10B981) else Color(0xFFEF4444),
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = if (stoichiometryResult.isExactStoichiometricRatio) {
                                        "✨ Exact Stoichiometric Proportions"
                                    } else {
                                        "⚠️ Limiting Reagent: ${limitingChem?.name ?: stoichiometryResult.limitingReagentId} (${limitingChem?.formula ?: ""})"
                                    },
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (stoichiometryResult.isExactStoichiometricRatio) Color(0xFF10B981) else Color(0xFFDC2626)
                                )
                                Text(
                                    text = stoichiometryResult.summaryMessage,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // 2. Initial Reactant Inputs
                    Text(
                        text = "Inputs (Initial Quantities Added):",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        stoichiometryResult.initialQuantities.forEach { (rId, dispensed) ->
                            val isLimiting = rId == stoichiometryResult.limitingReagentId
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = if (isLimiting && !stoichiometryResult.isExactStoichiometricRatio) {
                                    androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.6f))
                                } else null,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(Color(dispensed.chemical.colorHex))
                                        )
                                        Text(
                                            text = dispensed.chemical.formula,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 12.sp
                                        )
                                        if (isLimiting && !stoichiometryResult.isExactStoichiometricRatio) {
                                            Text(
                                                text = "[LIMITING]",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFEF4444)
                                            )
                                        }
                                    }
                                    Text(
                                        text = dispensed.displayQuantity,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // 3. Products Formed (Theoretical Yield)
                    Text(
                        text = "Products Formed (Theoretical Yield):",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0284C7)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        stoichiometryResult.productYields.forEach { (_, yield) ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF0284C7).copy(alpha = 0.1f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(Color(yield.chemical.colorHex))
                                        )
                                        Text(
                                            text = yield.chemical.formula,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 12.sp
                                        )
                                        if (yield.isSolidOrPrecipitate) {
                                            Text("↓ Solid", fontSize = 9.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Text(
                                        text = yield.displayYield,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0369A1)
                                    )
                                }
                            }
                        }
                    }

                    // 4. Unreacted Remainder (Excess Reagents)
                    Text(
                        text = "Unreacted Remainder (Excess Reagents):",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (stoichiometryResult.unreactedExcessDescriptions.isNotEmpty()) Color(0xFFD97706) else Color(0xFF10B981)
                    )

                    if (stoichiometryResult.unreactedExcessDescriptions.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.1f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Zero leftover excess. All reactants were quantitatively consumed in ideal stoichiometric ratio.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF047857),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            stoichiometryResult.unreactedExcessDescriptions.forEach { desc ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFF59E0B).copy(alpha = 0.12f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.4f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "• $desc",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFFB45309),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    // 5. Interactive Thermodynamic Heat Curves (Temp vs. Time)
                    if (readings.isNotEmpty()) {
                        Text(
                            text = "Thermodynamic Heat Curve & Energy Balance:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        ThermalCurveChart(readings = readings)
                    }
                }
            }
        }
    }
}
