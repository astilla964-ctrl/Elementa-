package com.example.ui.compounds

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Chemical
import com.example.data.model.PeriodicTableData

@Composable
fun PeriodicTableView(
    onSelectElement: (Chemical) -> Unit,
    modifier: Modifier = Modifier
) {
    val hScroll = rememberScrollState()
    val vScroll = rememberScrollState()

    var selectedSeriesFilter by remember { mutableStateOf<String?>(null) }

    val seriesColors = remember {
        mapOf(
            "Alkali Metal" to Color(0xFFEF4444),
            "Alkaline Earth Metal" to Color(0xFFF97316),
            "Transition Metal" to Color(0xFF0284C7),
            "Post-Transition Metal" to Color(0xFF0D9488),
            "Metalloid" to Color(0xFF8B5CF6),
            "Reactive Nonmetal" to Color(0xFF10B981),
            "Halogen" to Color(0xFF84CC16),
            "Noble Gas" to Color(0xFFEC4899),
            "Lanthanide" to Color(0xFF06B6D4),
            "Actinide" to Color(0xFFF43F5E)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 4.dp)
    ) {
        // Series Legend Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            seriesColors.forEach { (series, color) ->
                val isSelected = selectedSeriesFilter == series
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            selectedSeriesFilter = if (isSelected) null else series
                        },
                    color = if (isSelected) color.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, color) else null
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(color)
                        )
                        Text(
                            text = series,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // IUPAC 18-Column Interactive Grid
        Box(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(hScroll)
                .verticalScroll(vScroll)
                .padding(8.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // Period 1 to 7
                for (period in 1..7) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (group in 1..18) {
                            val element = PeriodicTableData.ALL_118_ELEMENTS.find {
                                it.period == period && it.group == group &&
                                        it.elementSeries != "Lanthanide" && it.elementSeries != "Actinide"
                            }

                            if (element != null) {
                                val sColor = seriesColors[element.elementSeries] ?: MaterialTheme.colorScheme.primary
                                val isDimmed = selectedSeriesFilter != null && element.elementSeries != selectedSeriesFilter

                                ElementTile(
                                    element = element,
                                    seriesColor = sColor,
                                    isDimmed = isDimmed,
                                    onClick = { onSelectElement(element) }
                                )
                            } else if (period == 6 && group == 3) {
                                // Placeholder for Lanthanides marker
                                LanthanideActinideMarkerTile("57-71", "La-Lu", Color(0xFF06B6D4))
                            } else if (period == 7 && group == 3) {
                                // Placeholder for Actinides marker
                                LanthanideActinideMarkerTile("89-103", "Ac-Lr", Color(0xFFF43F5E))
                            } else {
                                // Empty spacer cell
                                Spacer(modifier = Modifier.size(52.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Lanthanides Row (57-71)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Spacer(modifier = Modifier.width(108.dp)) // Offset to align under group 3
                    val lanthanides = PeriodicTableData.ALL_118_ELEMENTS.filter { it.elementSeries == "Lanthanide" }
                    lanthanides.forEach { element ->
                        val isDimmed = selectedSeriesFilter != null && selectedSeriesFilter != "Lanthanide"
                        ElementTile(
                            element = element,
                            seriesColor = Color(0xFF06B6D4),
                            isDimmed = isDimmed,
                            onClick = { onSelectElement(element) }
                        )
                    }
                }

                // Actinides Row (89-103)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Spacer(modifier = Modifier.width(108.dp)) // Offset to align under group 3
                    val actinides = PeriodicTableData.ALL_118_ELEMENTS.filter { it.elementSeries == "Actinide" }
                    actinides.forEach { element ->
                        val isDimmed = selectedSeriesFilter != null && selectedSeriesFilter != "Actinide"
                        ElementTile(
                            element = element,
                            seriesColor = Color(0xFFF43F5E),
                            isDimmed = isDimmed,
                            onClick = { onSelectElement(element) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ElementTile(
    element: Chemical,
    seriesColor: Color,
    isDimmed: Boolean,
    onClick: () -> Unit
) {
    val alpha = if (isDimmed) 0.25f else 1.0f

    Surface(
        modifier = Modifier
            .size(52.dp)
            .clickable { onClick() }
            .testTag("element_${element.atomicNumber}"),
        shape = RoundedCornerShape(8.dp),
        color = seriesColor.copy(alpha = 0.16f * alpha),
        border = androidx.compose.foundation.BorderStroke(1.dp, seriesColor.copy(alpha = 0.6f * alpha))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Atomic number (top)
            Text(
                text = "${element.atomicNumber}",
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = seriesColor.copy(alpha = alpha),
                modifier = Modifier.align(Alignment.Start)
            )

            // Symbol (center)
            Text(
                text = element.symbol ?: element.formula,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)
            )

            // Name (bottom)
            Text(
                text = element.name,
                fontSize = 7.sp,
                maxLines = 1,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha)
            )
        }
    }
}

@Composable
private fun LanthanideActinideMarkerTile(
    range: String,
    label: String,
    color: Color
) {
    Surface(
        modifier = Modifier.size(52.dp),
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = range, fontSize = 7.sp, color = color, fontWeight = FontWeight.Bold)
            Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}
