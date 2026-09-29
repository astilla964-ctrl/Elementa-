package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ContainerHazardState
import com.example.data.model.LabToolType
import com.example.data.model.ThermodynamicReading
import kotlin.math.roundToInt

@Composable
fun ThermodynamicCurvesDrawer(
    thermalHistory: List<ThermodynamicReading>,
    currentTemperature: Double,
    currentPressure: Double,
    activeTool: LabToolType,
    containerHazardState: ContainerHazardState,
    tempUnitCelsius: Boolean,
    pressureUnitAtm: Boolean,
    onToggleTempUnit: () -> Unit,
    onTogglePressureUnit: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(true) }
    var selectedMetric by remember { mutableStateOf("BOTH") } // "TEMP", "PRESSURE", "BOTH"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("thermodynamic_curves_drawer"),
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
            // Header Bar
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
                        imageVector = Icons.Default.ShowChart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "Live Thermodynamic Telemetry Log",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (tempUnitCelsius) "${currentTemperature.roundToInt()}°C" else "${(currentTemperature + 273.15).roundToInt()} K",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (currentTemperature > 100.0) Color(0xFFF97316) else if (currentTemperature < 0.0) Color(0xFF38BDF8) else MaterialTheme.colorScheme.primary
                            )
                            Text("•", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = if (pressureUnitAtm) "${String.format("%.2f", currentPressure)} atm" else "${String.format("%.1f", currentPressure * 101.325)} kPa",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (currentPressure > activeTool.maxSafePressureAtm) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier.size(28.dp).testTag("toggle_telemetry_expand")
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Collapse" else "Expand"
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp).testTag("dismiss_telemetry_drawer")
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
                    // Metric Filter & Unit Toggles Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("BOTH" to "All", "TEMP" to "Temp (T)", "PRESSURE" to "Pressure (P)").forEach { (key, label) ->
                                FilterChip(
                                    selected = selectedMetric == key,
                                    onClick = { selectedMetric = key },
                                    label = { Text(label, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                                modifier = Modifier.clickable { onToggleTempUnit() }.padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(if (tempUnitCelsius) "°C" else "K", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                                modifier = Modifier.clickable { onTogglePressureUnit() }.padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(if (pressureUnitAtm) "atm" else "kPa", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Live Interactive Canvas Chart
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0F172A),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    ) {
                        Canvas(modifier = Modifier.padding(8.dp)) {
                            val w = size.width
                            val h = size.height

                            // Draw subtle grid lines
                            val gridLines = 4
                            for (i in 0..gridLines) {
                                val gy = h * (i.toFloat() / gridLines)
                                drawLine(
                                    color = Color(0xFF1E293B),
                                    start = Offset(0f, gy),
                                    end = Offset(w, gy),
                                    strokeWidth = 1f
                                )
                            }

                            if (thermalHistory.size >= 2) {
                                val n = thermalHistory.size
                                val stepX = w / (n - 1).coerceAtLeast(1)

                                // 1. Draw Temperature Curve
                                if (selectedMetric == "TEMP" || selectedMetric == "BOTH") {
                                    val minT = -200.0
                                    val maxT = 1500.0
                                    val tempPath = Path()

                                    thermalHistory.forEachIndexed { i, reading ->
                                        val x = i * stepX
                                        val normT = ((reading.temperatureCelsius - minT) / (maxT - minT)).coerceIn(0.0, 1.0)
                                        val y = (h - (normT * h)).toFloat()
                                        if (i == 0) tempPath.moveTo(x, y) else tempPath.lineTo(x, y)
                                    }

                                    drawPath(
                                        path = tempPath,
                                        color = Color(0xFFF97316),
                                        style = Stroke(width = 2.5f, cap = StrokeCap.Round)
                                    )

                                    // Safe thermal limit line
                                    val safeNorm = ((activeTool.maxSafeTempC - minT) / (maxT - minT)).coerceIn(0.0, 1.0)
                                    val safeY = (h - (safeNorm * h)).toFloat()
                                    drawLine(
                                        color = Color(0xFFF97316).copy(alpha = 0.4f),
                                        start = Offset(0f, safeY),
                                        end = Offset(w, safeY),
                                        strokeWidth = 1f
                                    )
                                }

                                // 2. Draw Pressure Curve
                                if (selectedMetric == "PRESSURE" || selectedMetric == "BOTH") {
                                    val minP = 0.0
                                    val maxP = 25.0
                                    val pressurePath = Path()

                                    thermalHistory.forEachIndexed { i, reading ->
                                        val x = i * stepX
                                        val normP = ((reading.pressureAtm - minP) / (maxP - minP)).coerceIn(0.0, 1.0)
                                        val y = (h - (normP * h)).toFloat()
                                        if (i == 0) pressurePath.moveTo(x, y) else pressurePath.lineTo(x, y)
                                    }

                                    drawPath(
                                        path = pressurePath,
                                        color = Color(0xFF38BDF8),
                                        style = Stroke(width = 2.5f, cap = StrokeCap.Round)
                                    )

                                    // Safe pressure limit line
                                    val safePNorm = ((activeTool.maxSafePressureAtm - minP) / (maxP - minP)).coerceIn(0.0, 1.0)
                                    val safePY = (h - (safePNorm * h)).toFloat()
                                    drawLine(
                                        color = Color(0xFFEF4444).copy(alpha = 0.4f),
                                        start = Offset(0f, safePY),
                                        end = Offset(w, safePY),
                                        strokeWidth = 1f
                                    )
                                }
                            }
                        }
                    }

                    // Legend & Safety Limits Info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFF97316)))
                                Text("Temp T (Safe ≤ ${activeTool.maxSafeTempC.toInt()}°C)", fontSize = 10.sp, color = Color(0xFFF97316), fontFamily = FontFamily.Monospace)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF38BDF8)))
                                Text("Pressure P (Safe ≤ ${String.format("%.1f", activeTool.maxSafePressureAtm)} atm)", fontSize = 10.sp, color = Color(0xFF38BDF8), fontFamily = FontFamily.Monospace)
                            }
                        }

                        // Container Hazard Level Badge
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(containerHazardState.colorHex).copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(containerHazardState.colorHex))
                        ) {
                            Text(
                                text = containerHazardState.alertLevel,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(containerHazardState.colorHex),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
