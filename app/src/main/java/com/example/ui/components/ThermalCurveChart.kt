package com.example.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ContainerHazardState
import com.example.data.model.ThermodynamicReading
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Interactive Live Heat Curves (Temp vs. Time) and Pressure Telemetry Chart.
 */
@Composable
fun ThermalCurveChart(
    readings: List<ThermodynamicReading>,
    tempUnitCelsius: Boolean = true,
    pressureUnitAtm: Boolean = true,
    modifier: Modifier = Modifier
) {
    val displayReadings = if (readings.isEmpty()) {
        // Fallback placeholder reading if telemetry is fresh
        listOf(
            ThermodynamicReading(
                timestampMs = System.currentTimeMillis(),
                temperatureCelsius = 25.0,
                pressureAtm = 1.0,
                hazardState = ContainerHazardState.INTACT
            )
        )
    } else readings

    val currentReading = displayReadings.last()
    val minTemp = displayReadings.minOf { it.temperatureCelsius }
    val maxTemp = displayReadings.maxOf { it.temperatureCelsius }
    val currentPressure = currentReading.pressureAtm

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("thermal_curve_chart"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F172A) // Dark slate lab monitor
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Chart Header & Live Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShowChart,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Live Heat Curve (Temp vs. Time)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Hazard / Status Tag
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(currentReading.hazardState.colorHex).copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(currentReading.hazardState.colorHex))
                ) {
                    Text(
                        text = currentReading.hazardState.alertLevel,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(currentReading.hazardState.colorHex)
                    )
                }
            }

            // Stat Summary Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Current Temperature
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF1E293B)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                        Text("Current Temp", fontSize = 10.sp, color = Color(0xFF94A3B8))
                        val curVal = if (tempUnitCelsius) "${currentReading.temperatureCelsius.roundToInt()}°C"
                                     else "${(currentReading.temperatureCelsius + 273.15).roundToInt()} K"
                        Text(
                            text = curVal,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace,
                            color = if (currentReading.temperatureCelsius > 100.0) Color(0xFFF97316)
                                    else if (currentReading.temperatureCelsius < 0.0) Color(0xFF38BDF8)
                                    else Color(0xFF10B981)
                        )
                    }
                }

                // Min / Max Range
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF1E293B)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                        Text("Range (Min / Max)", fontSize = 10.sp, color = Color(0xFF94A3B8))
                        Text(
                            text = "${minTemp.roundToInt()}° / ${maxTemp.roundToInt()}°C",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFFE2E8F0)
                        )
                    }
                }

                // Pressure
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF1E293B)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                        Text("Internal Pressure", fontSize = 10.sp, color = Color(0xFF94A3B8))
                        val pVal = if (pressureUnitAtm) "${String.format("%.1f", currentPressure)} atm"
                                   else "${String.format("%.0f", currentPressure * 101.3)} kPa"
                        Text(
                            text = pVal,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            color = if (currentPressure > 3.0) Color(0xFFEF4444) else Color(0xFF38BDF8)
                        )
                    }
                }
            }

            // Canvas Heat Curve Graph
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF020617))
            ) {
                Canvas(modifier = Modifier.fillMaxWidth().height(130.dp)) {
                    val w = size.width
                    val h = size.height
                    val paddingLeft = 36f
                    val paddingRight = 16f
                    val paddingTop = 16f
                    val paddingBottom = 24f

                    val graphW = w - paddingLeft - paddingRight
                    val graphH = h - paddingTop - paddingBottom

                    // Horizontal Grid Lines
                    val gridSteps = 4
                    for (i in 0..gridSteps) {
                        val y = paddingTop + (graphH * (i.toFloat() / gridSteps))
                        drawLine(
                            color = Color(0xFF1E293B),
                            start = Offset(paddingLeft, y),
                            end = Offset(w - paddingRight, y),
                            strokeWidth = 1f
                        )
                    }

                    // Vertical Grid Lines
                    val vSteps = 5
                    for (i in 0..vSteps) {
                        val x = paddingLeft + (graphW * (i.toFloat() / vSteps))
                        drawLine(
                            color = Color(0xFF1E293B),
                            start = Offset(x, paddingTop),
                            end = Offset(x, h - paddingBottom),
                            strokeWidth = 1f
                        )
                    }

                    if (displayReadings.size >= 2) {
                        val tMin = min(-50.0, displayReadings.minOf { it.temperatureCelsius } - 10.0)
                        val tMax = max(200.0, displayReadings.maxOf { it.temperatureCelsius } + 30.0)
                        val tRange = max(1.0, tMax - tMin)

                        val pMin = 0.0
                        val pMax = max(5.0, displayReadings.maxOf { it.pressureAtm } * 1.25)
                        val pRange = max(1.0, pMax - pMin)

                        // 1. Temperature Curve & Shaded Area
                        val tempPath = Path()
                        val fillPath = Path()

                        displayReadings.forEachIndexed { index, pt ->
                            val progressX = index.toFloat() / (displayReadings.size - 1).coerceAtLeast(1)
                            val x = paddingLeft + (progressX * graphW)
                            val normT = ((pt.temperatureCelsius - tMin) / tRange).toFloat().coerceIn(0f, 1f)
                            val y = (paddingTop + graphH) - (normT * graphH)

                            if (index == 0) {
                                tempPath.moveTo(x, y)
                                fillPath.moveTo(x, paddingTop + graphH)
                                fillPath.lineTo(x, y)
                            } else {
                                tempPath.lineTo(x, y)
                                fillPath.lineTo(x, y)
                            }
                        }

                        val lastX = paddingLeft + graphW
                        fillPath.lineTo(lastX, paddingTop + graphH)
                        fillPath.close()

                        // Gradient fill under temperature curve
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFF97316).copy(alpha = 0.35f),
                                    Color(0xFF0284C7).copy(alpha = 0.05f)
                                )
                            )
                        )

                        // Temperature stroke line
                        drawPath(
                            path = tempPath,
                            color = Color(0xFFFB923C),
                            style = Stroke(
                                width = 3f,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )

                        // 2. Pressure dashed / dotted line
                        val pressurePath = Path()
                        displayReadings.forEachIndexed { index, pt ->
                            val progressX = index.toFloat() / (displayReadings.size - 1).coerceAtLeast(1)
                            val x = paddingLeft + (progressX * graphW)
                            val normP = ((pt.pressureAtm - pMin) / pRange).toFloat().coerceIn(0f, 1f)
                            val y = (paddingTop + graphH) - (normP * graphH)

                            if (index == 0) pressurePath.moveTo(x, y) else pressurePath.lineTo(x, y)
                        }

                        drawPath(
                            path = pressurePath,
                            color = Color(0xFF38BDF8).copy(alpha = 0.8f),
                            style = Stroke(
                                width = 1.8f,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )

                        // End point circles
                        val lastPt = displayReadings.last()
                        val lastNormT = ((lastPt.temperatureCelsius - tMin) / tRange).toFloat().coerceIn(0f, 1f)
                        val lastY = (paddingTop + graphH) - (lastNormT * graphH)
                        drawCircle(
                            color = Color(0xFFF97316),
                            radius = 4.5f,
                            center = Offset(lastX, lastY)
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 2f,
                            center = Offset(lastX, lastY)
                        )
                    }
                }
            }

            // Legend Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFB923C)))
                        Text("Temperature (°C)", fontSize = 10.sp, color = Color(0xFFCBD5E1))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF38BDF8)))
                        Text("Pressure (atm)", fontSize = 10.sp, color = Color(0xFFCBD5E1))
                    }
                }

                Text(
                    text = "Live 60-sample telemetry buffer",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}
