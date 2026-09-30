package com.example.ui.plant

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.plant.IndustrialContract
import com.example.data.model.plant.IndustrialProcessCatalog
import com.example.data.model.plant.IndustrialProcessRecipe
import com.example.data.model.plant.ReactorType
import com.example.data.model.plant.ThermalJacketMode
import com.example.engine.plant.IndustrialPlantEngine
import com.example.ui.MainViewModel
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun ChemicalWorksScreen(
    viewModel: MainViewModel,
    isLandscape: Boolean = false
) {
    // Persistent plant engine instance held in remember
    val plantEngine = remember { IndustrialPlantEngine() }
    var tickCounter by remember { mutableIntStateOf(0) }
    var selectedTab by remember { mutableIntStateOf(0) }

    // Simulation loop ticker (1 second real-time cadence)
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            plantEngine.tick(1.0)
            tickCounter++
        }
    }

    val telemetry = plantEngine.telemetry
    val activeContract = plantEngine.activeContract
    val recipe = plantEngine.activeRecipe

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .testTag("chemical_works_screen")
    ) {
        // 1. Industrial Header Bar
        PlantHeaderBar(
            plantEngine = plantEngine,
            onEmergencyShutdown = { plantEngine.triggerEmergencyShutdown(it) },
            onResetPlant = { plantEngine.resetPlantState() }
        )

        // 2. Navigation Tabs
        val tabs = listOf("P&ID Schematic", "Reactor & Jacket", "Environmental & Effluent", "Contracts")
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF1E293B),
            contentColor = Color(0xFF38BDF8),
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = if (isLandscape) 13.sp else 11.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) Color(0xFF38BDF8) else Color(0xFF94A3B8)
                        )
                    },
                    modifier = Modifier.testTag("tab_plant_$index")
                )
            }
        }

        // 3. Tab Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (selectedTab) {
                0 -> PidSchematicView(plantEngine = plantEngine, isLandscape = isLandscape)
                1 -> ReactorControlsView(plantEngine = plantEngine, isLandscape = isLandscape)
                2 -> EnvironmentalComplianceView(plantEngine = plantEngine, isLandscape = isLandscape)
                3 -> IndustrialContractsView(plantEngine = plantEngine, isLandscape = isLandscape)
            }
        }
    }
}

@Composable
private fun PlantHeaderBar(
    plantEngine: IndustrialPlantEngine,
    onEmergencyShutdown: (Boolean) -> Unit,
    onResetPlant: () -> Unit
) {
    val telemetry = plantEngine.telemetry
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🏭 CHEMICAL WORKS",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = Color(0xFF38BDF8)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                if (telemetry.isEmergencyShutdownActive) Color(0xFFEF4444)
                                else if (telemetry.isOverpressurized) Color(0xFFF59E0B)
                                else Color(0xFF10B981),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (telemetry.isEmergencyShutdownActive) "TRIPPED (ESD)"
                            else if (telemetry.isOverpressurized) "OVERPRESSURE"
                            else "RUNNING",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                Text(
                    text = "Recipe: ${plantEngine.activeRecipe.name}",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            // Key Metrics Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricChip(
                    label = "PROD",
                    value = "${telemetry.instantaneousProductionRateLpm.roundToInt()} L/m",
                    color = Color(0xFF10B981)
                )
                MetricChip(
                    label = "PURITY",
                    value = "${telemetry.productPurityPercent.roundToInt()}%",
                    color = Color(0xFF38BDF8)
                )
                MetricChip(
                    label = "FINES",
                    value = "-$${plantEngine.totalFinesAssessed}",
                    color = if (plantEngine.totalFinesAssessed > 0) Color(0xFFEF4444) else Color(0xFF64748B)
                )

                // Master ESD Toggle
                Button(
                    onClick = { onEmergencyShutdown(!telemetry.isEmergencyShutdownActive) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (telemetry.isEmergencyShutdownActive) Color(0xFF10B981) else Color(0xFFEF4444)
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("btn_esd_trip")
                ) {
                    Text(
                        text = if (telemetry.isEmergencyShutdownActive) "RESET ESD" else "ESD TRIP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricChip(label: String, value: String, color: Color) {
    Column(
        modifier = Modifier
            .background(Color(0xFF0F172A), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, fontSize = 8.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
        Text(text = value, fontSize = 11.sp, color = color, fontWeight = FontWeight.Black)
    }
}

// -------------------------------------------------------------------------
// 1. P&ID Schematic View (Piping & Instrumentation Diagram)
// -------------------------------------------------------------------------
@Composable
private fun PidSchematicView(
    plantEngine: IndustrialPlantEngine,
    isLandscape: Boolean
) {
    val telemetry = plantEngine.telemetry
    val infiniteTransition = rememberInfiniteTransition(label = "pid_flow")
    val flowPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 24f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flow_dash"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // P&ID Canvas View
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isLandscape) 340.dp else 280.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF090D16)),
            shape = RoundedCornerShape(12.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Color(0xFF1E293B), Color(0xFF334155))))
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                val w = size.width
                val h = size.height

                // Draw background grid lines (Blueprint style)
                val gridColor = Color(0xFF131D31)
                var gx = 0f
                while (gx < w) {
                    drawLine(gridColor, Offset(gx, 0f), Offset(gx, h), strokeWidth = 1f)
                    gx += 30f
                }
                var gy = 0f
                while (gy < h) {
                    drawLine(gridColor, Offset(0f, gy), Offset(w, gy), strokeWidth = 1f)
                    gy += 30f
                }

                // Node positions
                val tankAY = h * 0.22f
                val tankBY = h * 0.42f
                val pumpX = w * 0.22f
                val reactorX = w * 0.52f
                val reactorY = h * 0.35f
                val scrubberX = w * 0.52f
                val scrubberY = h * 0.08f
                val basinX = w * 0.82f
                val basinY = h * 0.65f

                val flowStroke = Stroke(
                    width = 4f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), flowPhase)
                )
                val pipeColor = Color(0xFF38BDF8)
                val gasPipeColor = Color(0xFFFBBF24)
                val drainPipeColor = Color(0xFFA78BFA)

                // 1. Pipes from Tanks to Reactor
                val pathFeedA = Path().apply {
                    moveTo(w * 0.08f, tankAY)
                    lineTo(pumpX, tankAY)
                    lineTo(reactorX - 35f, tankAY)
                    lineTo(reactorX - 35f, reactorY - 30f)
                }
                drawPath(pathFeedA, pipeColor, style = flowStroke)

                val pathFeedB = Path().apply {
                    moveTo(w * 0.08f, tankBY)
                    lineTo(pumpX, tankBY)
                    lineTo(reactorX - 35f, tankBY)
                    lineTo(reactorX - 35f, reactorY + 20f)
                }
                drawPath(pathFeedB, pipeColor, style = flowStroke)

                // 2. Gas vent line from Reactor to Scrubber
                val pathGas = Path().apply {
                    moveTo(reactorX, reactorY - 45f)
                    lineTo(scrubberX, scrubberY + 30f)
                }
                drawPath(pathGas, gasPipeColor, style = flowStroke)

                // 3. Liquid discharge line from Reactor to Basin
                val pathDischarge = Path().apply {
                    moveTo(reactorX, reactorY + 45f)
                    lineTo(reactorX, basinY)
                    lineTo(basinX - 40f, basinY)
                }
                drawPath(pathDischarge, drainPipeColor, style = flowStroke)

                // 4. Equipment Icons & Nodes
                // Feed Tank A
                drawRoundRect(
                    color = Color(0xFF1E293B),
                    topLeft = Offset(w * 0.02f, tankAY - 20f),
                    size = Size(w * 0.12f, 40f),
                    cornerRadius = CornerRadius(6f, 6f)
                )
                // Feed Tank B
                drawRoundRect(
                    color = Color(0xFF1E293B),
                    topLeft = Offset(w * 0.02f, tankBY - 20f),
                    size = Size(w * 0.12f, 40f),
                    cornerRadius = CornerRadius(6f, 6f)
                )

                // Pumps
                drawCircle(Color(0xFF0EA5E9), radius = 12f, center = Offset(pumpX, tankAY))
                drawCircle(Color(0xFF0EA5E9), radius = 12f, center = Offset(pumpX, tankBY))

                // Reactor Vessel (Large circle or cylinder with thermal jacket glow)
                val jacketColor = when (plantEngine.thermalJacket.mode) {
                    ThermalJacketMode.COOLING -> Color(0xFF0284C7)
                    ThermalJacketMode.HEATING -> Color(0xFFEA580C)
                    ThermalJacketMode.AUTO_PID -> Color(0xFF10B981)
                    ThermalJacketMode.OFF -> Color(0xFF64748B)
                }
                // Thermal Jacket ring
                drawCircle(jacketColor.copy(alpha = 0.35f), radius = 56f, center = Offset(reactorX, reactorY))
                drawCircle(Color(0xFF1E293B), radius = 46f, center = Offset(reactorX, reactorY))
                drawCircle(Color(0xFF475569), radius = 46f, center = Offset(reactorX, reactorY), style = Stroke(width = 3f))

                // Gas Scrubber Tower
                drawRoundRect(
                    color = if (plantEngine.gasScrubber.isActive) Color(0xFF065F46) else Color(0xFF7F1D1D),
                    topLeft = Offset(scrubberX - 25f, scrubberY - 15f),
                    size = Size(50f, 45f),
                    cornerRadius = CornerRadius(6f, 6f)
                )

                // Effluent Neutralization Basin
                drawRoundRect(
                    color = if (telemetry.isCompliantEffluent) Color(0xFF14532D) else Color(0xFF7C2D12),
                    topLeft = Offset(basinX - 35f, basinY - 25f),
                    size = Size(70f, 50f),
                    cornerRadius = CornerRadius(8f, 8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Live Instrumentation Badges (P&ID Transmitter Tags)
        Text(
            text = "LIVE SENSOR TELEMETRY & TRANSMITTERS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF94A3B8)
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SensorTagCard(
                tag = "TT-101",
                label = "Reactor Temp",
                value = "${telemetry.reactorTempC.roundToInt()}°C",
                statusColor = if (telemetry.reactorTempC > 120.0) Color(0xFFEF4444) else Color(0xFF10B981)
            )
            SensorTagCard(
                tag = "PT-102",
                label = "Vessel Pressure",
                value = "${telemetry.linePressureKpa.roundToInt()} kPa",
                statusColor = if (telemetry.isOverpressurized) Color(0xFFEF4444) else Color(0xFF38BDF8)
            )
            SensorTagCard(
                tag = "FT-103",
                label = "Outlet Flow",
                value = "${plantEngine.dischargePump.currentFlowRateLpm.roundToInt()} L/m",
                statusColor = Color(0xFF0EA5E9)
            )
            SensorTagCard(
                tag = "AT-104",
                label = "Product Purity",
                value = "${telemetry.productPurityPercent.roundToInt()}%",
                statusColor = Color(0xFFA855F7)
            )
            SensorTagCard(
                tag = "ST-105",
                label = "Vent Emissions",
                value = "${telemetry.ventedGasPpm.roundToInt()} PPM",
                statusColor = if (telemetry.isCompliantEmissions) Color(0xFF10B981) else Color(0xFFEF4444)
            )
            SensorTagCard(
                tag = "pH-106",
                label = "Effluent Basin",
                value = "pH ${String.format("%.1f", telemetry.effluentPh)}",
                statusColor = if (telemetry.isCompliantEffluent) Color(0xFF10B981) else Color(0xFFF59E0B)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Controls Card: Pumps and Valves
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "TRANSPORTS, PUMPS & CHECK VALVES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE2E8F0)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Auto-Level Balancing (${plantEngine.reactor.currentVolumeL.roundToInt()} / ${plantEngine.reactor.maxVolumeL.roundToInt()} L)",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1)
                        )
                        LinearProgressIndicator(
                            progress = { (telemetry.reactorLevelPercent / 100f).toFloat() },
                            modifier = Modifier
                                .width(180.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFF38BDF8),
                            trackColor = Color(0xFF0F172A)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (plantEngine.autoLevelControl) "AUTO PID" else "MANUAL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (plantEngine.autoLevelControl) Color(0xFF10B981) else Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Switch(
                            checked = plantEngine.autoLevelControl,
                            onCheckedChange = { plantEngine.toggleAutoLevelControl() },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF38BDF8))
                        )
                    }
                }

                Divider(color = Color(0xFF334155), modifier = Modifier.padding(vertical = 8.dp))

                // Pump Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PumpControlMini(
                        title = "Feed Pump A",
                        pump = plantEngine.feedPumpA,
                        onToggle = { plantEngine.togglePumpPower(plantEngine.feedPumpA.id) },
                        onRateChange = { plantEngine.setPumpFlowRate(plantEngine.feedPumpA.id, it) },
                        modifier = Modifier.weight(1f)
                    )
                    PumpControlMini(
                        title = "Feed Pump B",
                        pump = plantEngine.feedPumpB,
                        onToggle = { plantEngine.togglePumpPower(plantEngine.feedPumpB.id) },
                        onRateChange = { plantEngine.setPumpFlowRate(plantEngine.feedPumpB.id, it) },
                        modifier = Modifier.weight(1f)
                    )
                    PumpControlMini(
                        title = "Discharge",
                        pump = plantEngine.dischargePump,
                        onToggle = { plantEngine.togglePumpPower(plantEngine.dischargePump.id) },
                        onRateChange = { plantEngine.setPumpFlowRate(plantEngine.dischargePump.id, it) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SensorTagCard(tag: String, label: String, value: String, statusColor: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        shape = RoundedCornerShape(8.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(statusColor.copy(alpha = 0.5f), Color.Transparent)))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(statusColor, CircleShape)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = tag, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
            }
            Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Black, color = statusColor)
            Text(text = label, fontSize = 9.sp, color = Color(0xFF64748B))
        }
    }
}

@Composable
private fun PumpControlMini(
    title: String,
    pump: com.example.data.model.plant.CentrifugalPump,
    onToggle: () -> Unit,
    onRateChange: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE2E8F0))
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(if (pump.isRunning) Color(0xFF10B981) else Color(0xFFEF4444))
                    .clickable { onToggle() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (pump.isRunning) "ON" else "OFF",
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
        Text(
            text = "${pump.currentFlowRateLpm.roundToInt()} L/min",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF38BDF8)
        )
        Slider(
            value = pump.targetFlowRateLpm.toFloat(),
            onValueChange = { onRateChange(it.toDouble()) },
            valueRange = 0f..150f,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF38BDF8),
                activeTrackColor = Color(0xFF0284C7),
                inactiveTrackColor = Color(0xFF334155)
            )
        )
    }
}

// -------------------------------------------------------------------------
// 2. Reactor & Thermal Jacket Controls Panel
// -------------------------------------------------------------------------
@Composable
private fun ReactorControlsView(
    plantEngine: IndustrialPlantEngine,
    isLandscape: Boolean
) {
    val reactor = plantEngine.reactor
    val jacket = plantEngine.thermalJacket
    val telemetry = plantEngine.telemetry

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Equipment Configuration
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "UNIT OPERATION REACTOR SPECIFICATION",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF38BDF8)
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilterChip(
                        selected = reactor.type == ReactorType.CSTR,
                        onClick = { plantEngine.setReactorType(ReactorType.CSTR) },
                        label = { Text("CSTR (Continuously Stirred)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF0284C7),
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("chip_cstr")
                    )
                    FilterChip(
                        selected = reactor.type == ReactorType.PFR,
                        onClick = { plantEngine.setReactorType(ReactorType.PFR) },
                        label = { Text("PFR (Plug Flow Tubular)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF0284C7),
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("chip_pfr")
                    )
                }

                Text(
                    text = reactor.type.description,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                Divider(color = Color(0xFF334155), modifier = Modifier.padding(vertical = 8.dp))

                // Volume and Agitator Sliders
                Text(
                    text = "Configurable Vessel Capacity: ${reactor.maxVolumeL.roundToInt()} L (Current: ${reactor.currentVolumeL.roundToInt()} L)",
                    fontSize = 11.sp,
                    color = Color(0xFFCBD5E1)
                )
                Slider(
                    value = reactor.maxVolumeL.toFloat(),
                    onValueChange = { plantEngine.configureReactor(it.toDouble(), reactor.agitatorRpm) },
                    valueRange = 500f..10000f,
                    steps = 19,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF38BDF8), activeTrackColor = Color(0xFF0284C7))
                )

                if (reactor.type == ReactorType.CSTR) {
                    Text(
                        text = "Impeller Agitator Speed: ${reactor.agitatorRpm.roundToInt()} RPM",
                        fontSize = 11.sp,
                        color = Color(0xFFCBD5E1)
                    )
                    Slider(
                        value = reactor.agitatorRpm.toFloat(),
                        onValueChange = { plantEngine.configureReactor(reactor.maxVolumeL, it.toDouble()) },
                        valueRange = 0f..600f,
                        colors = SliderDefaults.colors(thumbColor = Color(0xFFA855F7), activeTrackColor = Color(0xFF9333EA))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Thermal Jacket Controls
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "COOLING & HEATING JACKET (ISOTHERMAL CONTROL)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF59E0B)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Mode Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThermalJacketMode.entries.forEach { mode ->
                        FilterChip(
                            selected = jacket.mode == mode,
                            onClick = { plantEngine.setThermalJacket(mode, jacket.setpointTempC, jacket.coolantFlowLpm) },
                            label = { Text(mode.displayName, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFD97706),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Reactor Temp", fontSize = 10.sp, color = Color(0xFF94A3B8))
                        Text(
                            text = "${telemetry.reactorTempC.roundToInt()}°C",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFF43F5E)
                        )
                    }
                    Column {
                        Text(text = "Jacket Temp", fontSize = 10.sp, color = Color(0xFF94A3B8))
                        Text(
                            text = "${telemetry.jacketTempC.roundToInt()}°C",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF38BDF8)
                        )
                    }
                    Column {
                        Text(text = "Target Setpoint", fontSize = 10.sp, color = Color(0xFF94A3B8))
                        Text(
                            text = "${jacket.setpointTempC.roundToInt()}°C",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF10B981)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Jacket Temperature Setpoint: ${jacket.setpointTempC.roundToInt()}°C",
                    fontSize = 11.sp,
                    color = Color(0xFFCBD5E1)
                )
                Slider(
                    value = jacket.setpointTempC.toFloat(),
                    onValueChange = { plantEngine.setThermalJacket(jacket.mode, it.toDouble(), jacket.coolantFlowLpm) },
                    valueRange = 20f..180f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF10B981), activeTrackColor = Color(0xFF059669))
                )

                Text(
                    text = "Coolant Utility Flow Rate: ${jacket.coolantFlowLpm.roundToInt()} L/min",
                    fontSize = 11.sp,
                    color = Color(0xFFCBD5E1)
                )
                Slider(
                    value = jacket.coolantFlowLpm.toFloat(),
                    onValueChange = { plantEngine.setThermalJacket(jacket.mode, jacket.setpointTempC, it.toDouble()) },
                    valueRange = 0f..250f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF0EA5E9), activeTrackColor = Color(0xFF0284C7))
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Continuous Flow Kinetics Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "RESIDENCE TIME (τ) & KINETIC YIELD OPTIMIZATION",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFA855F7)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "τ = V / Q = ${plantEngine.reactor.currentVolumeL.roundToInt()} L / ${plantEngine.dischargePump.currentFlowRateLpm.roundToInt()} L/min = ${String.format("%.1f", telemetry.residenceTimeMinutes)} minutes",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = Color(0xFFCBD5E1)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Kinetic Conversion Yield:",
                        fontSize = 12.sp,
                        color = Color(0xFFE2E8F0)
                    )
                    Text(
                        text = "${String.format("%.1f", telemetry.conversionPercent)}%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF10B981)
                    )
                }
                LinearProgressIndicator(
                    progress = { (telemetry.conversionPercent / 100f).toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF10B981),
                    trackColor = Color(0xFF334155)
                )
            }
        }
    }
}

// -------------------------------------------------------------------------
// 3. Environmental Safety & Effluent Neutralization View
// -------------------------------------------------------------------------
@Composable
private fun EnvironmentalComplianceView(
    plantEngine: IndustrialPlantEngine,
    isLandscape: Boolean
) {
    val scrubber = plantEngine.gasScrubber
    val basin = plantEngine.neutralizationBasin
    val telemetry = plantEngine.telemetry

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Scrubber Tower Controls
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "COUNTER-CURRENT WET GAS SCRUBBER",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                        Text(
                            text = "Target: ${scrubber.targetedGasType}",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    Switch(
                        checked = scrubber.isActive,
                        onCheckedChange = { plantEngine.setScrubberControls(it, scrubber.washFlowLpm) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF10B981))
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Absorption Efficiency", fontSize = 10.sp, color = Color(0xFF94A3B8))
                        Text(
                            text = "${String.format("%.1f", scrubber.efficiencyPercent)}%",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF10B981)
                        )
                    }
                    Column {
                        Text(text = "Stack Emission", fontSize = 10.sp, color = Color(0xFF94A3B8))
                        Text(
                            text = "${telemetry.ventedGasPpm.roundToInt()} PPM",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = if (telemetry.isCompliantEmissions) Color(0xFF10B981) else Color(0xFFEF4444)
                        )
                    }
                    Column {
                        Text(text = "Legal Limit", fontSize = 10.sp, color = Color(0xFF94A3B8))
                        Text(
                            text = "${scrubber.complianceThresholdPpm.roundToInt()} PPM",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Caustic Wash Solution Flow: ${scrubber.washFlowLpm.roundToInt()} L/min",
                    fontSize = 11.sp,
                    color = Color(0xFFCBD5E1)
                )
                Slider(
                    value = scrubber.washFlowLpm.toFloat(),
                    onValueChange = { plantEngine.setScrubberControls(scrubber.isActive, it.toDouble()) },
                    valueRange = 0f..150f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF10B981), activeTrackColor = Color(0xFF059669))
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Effluent Basin Controls
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "EFFLUENT NEUTRALIZATION & WATER COMPLIANCE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF38BDF8)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Basin Wastewater pH", fontSize = 10.sp, color = Color(0xFF94A3B8))
                        Text(
                            text = String.format("%.2f", basin.currentPh),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = if (telemetry.isCompliantEffluent) Color(0xFF10B981) else Color(0xFFEF4444)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(
                                if (telemetry.isCompliantEffluent) Color(0xFF065F46) else Color(0xFF7F1D1D),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (telemetry.isCompliantEffluent) "COMPLIANT (pH 6.5 - 8.5)" else "VIOLATION (FINE RISK)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Caustic (NaOH) Neutralizer Dosing: ${String.format("%.1f", basin.causticDosingFlowLpm)} L/min",
                    fontSize = 11.sp,
                    color = Color(0xFFCBD5E1)
                )
                Slider(
                    value = basin.causticDosingFlowLpm.toFloat(),
                    onValueChange = { plantEngine.setEffluentDosing(it.toDouble(), basin.acidDosingFlowLpm) },
                    valueRange = 0f..5f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF38BDF8), activeTrackColor = Color(0xFF0284C7))
                )

                Text(
                    text = "Acid (H2SO4) Neutralizer Dosing: ${String.format("%.1f", basin.acidDosingFlowLpm)} L/min",
                    fontSize = 11.sp,
                    color = Color(0xFFCBD5E1)
                )
                Slider(
                    value = basin.acidDosingFlowLpm.toFloat(),
                    onValueChange = { plantEngine.setEffluentDosing(basin.causticDosingFlowLpm, it.toDouble()) },
                    valueRange = 0f..5f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFFF59E0B), activeTrackColor = Color(0xFFD97706))
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Violation & Audit Alert Log
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SAFETY & REGULATORY AUDIT LOG",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8)
                    )
                    if (plantEngine.recentAlerts.isNotEmpty()) {
                        Text(
                            text = "Clear All",
                            fontSize = 10.sp,
                            color = Color(0xFF38BDF8),
                            modifier = Modifier.clickable { plantEngine.clearViolations() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (plantEngine.recentAlerts.isEmpty()) {
                    Text(
                        text = "No environmental infractions or vessel rupture events recorded. Plant operates within full regulatory safety compliance.",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                } else {
                    plantEngine.recentAlerts.take(6).forEach { alert ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (alert.isCritical) Icons.Filled.Warning else Icons.Filled.Info,
                                contentDescription = null,
                                tint = if (alert.isCritical) Color(0xFFEF4444) else Color(0xFFF59E0B),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = alert.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (alert.isCritical) Color(0xFFEF4444) else Color(0xFFF59E0B)
                                )
                                Text(
                                    text = alert.description,
                                    fontSize = 10.sp,
                                    color = Color(0xFFCBD5E1)
                                )
                            }
                            if (alert.finePenalty > 0) {
                                Text(
                                    text = "-$${alert.finePenalty}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFEF4444)
                                )
                            }
                        }
                        Divider(color = Color(0xFF1E293B), modifier = Modifier.padding(vertical = 2.dp))
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// 4. Industrial Contracts & Production Campaign View
// -------------------------------------------------------------------------
@Composable
private fun IndustrialContractsView(
    plantEngine: IndustrialPlantEngine,
    isLandscape: Boolean
) {
    val contracts = IndustrialProcessCatalog.DEFAULT_CONTRACTS
    val activeContract = plantEngine.activeContract

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "INDUSTRIAL PRODUCTION ORDERS & CAMPAIGNS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF38BDF8)
        )
        Text(
            text = "Supply commercial orders continuously. Maintain required stoichiometric purity and avoid EPA environmental fines to collect bonuses.",
            fontSize = 11.sp,
            color = Color(0xFF94A3B8)
        )
        Spacer(modifier = Modifier.height(10.dp))

        contracts.forEach { contract ->
            val isCurrent = activeContract?.id == contract.id
            val liveContract = if (isCurrent) activeContract else contract
            IndustrialContractCard(
                contract = liveContract,
                isSelected = isCurrent,
                onSelect = { plantEngine.selectContract(contract) }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun IndustrialContractCard(
    contract: IndustrialContract,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF1E293B) else Color(0xFF0F172A)
        ),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                if (isSelected) listOf(Color(0xFF38BDF8), Color(0xFF0284C7))
                else listOf(Color(0xFF334155), Color.Transparent)
            )
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = contract.clientName,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = contract.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                if (contract.isCompleted) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF10B981), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "FULFILLED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                } else if (isSelected) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF0284C7), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "ACTIVE LINE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = contract.description,
                fontSize = 11.sp,
                color = Color(0xFFCBD5E1)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar
            val progress = (contract.deliveredVolumeL / contract.targetVolumeL).coerceIn(0.0, 1.0)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Delivery: ${contract.deliveredVolumeL.roundToInt()} / ${contract.targetVolumeL.roundToInt()} L",
                    fontSize = 11.sp,
                    color = Color(0xFF38BDF8)
                )
                Text(
                    text = "Min Purity: ${contract.minPurityPercent.roundToInt()}%",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }
            LinearProgressIndicator(
                progress = { progress.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Color(0xFF38BDF8),
                trackColor = Color(0xFF334155)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Payout: +${contract.rewardCredits} Credits (+$${contract.environmentalBonus} Eco-Bonus)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10B981)
                )

                if (!isSelected && !contract.isCompleted) {
                    Button(
                        onClick = onSelect,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("Configure Line", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
