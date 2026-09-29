package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import com.example.data.model.LabToolType
import com.example.engine.model.ElementParticle2D
import com.example.engine.model.PotentialInteraction
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun LabCanvas(
    modifier: Modifier = Modifier,
    activeTool: LabToolType,
    liquidColor: Color,
    liquidFillPercent: Float, // 0.0f to 1.0f
    temperature: Double,
    isHeating: Boolean,
    isElectricityActive: Boolean,
    isCentrifuging: Boolean,
    hasPrecipitate: Boolean,
    hasUnreactedSolid: Boolean = false,
    unreactedSolidColor: Color = Color(0xFF94A3B8),
    transparencyAlpha: Float = 0.78f,
    isReacting: Boolean,
    particles: List<ElementParticle2D> = emptyList(),
    potentialInteractions: List<PotentialInteraction> = emptyList()
) {
    val infiniteTransition = rememberInfiniteTransition(label = "LabAnimation")

    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavePhase"
    )

    val flameFlicker by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 180, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FlameFlicker"
    )

    val bubbleProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isReacting || temperature > 95.0) 900 else 2400,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "BubbleProgress"
    )

    // Stable random seeds for bubble particle positions
    val bubbleOffsets = remember {
        List(14) {
            Pair(Random.nextFloat(), Random.nextFloat())
        }
    }

    val steamOffsets = remember {
        List(10) {
            Pair(Random.nextFloat(), Random.nextFloat())
        }
    }

    val outlineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
    val glassHighlight = Color.White.copy(alpha = 0.35f)
    val gridTickColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // 1. Draw Burner Heat Flame (if heating or Bunsen Burner selected)
        if (isHeating || activeTool == LabToolType.BUNSEN_BURNER) {
            drawBurnerFlame(
                centerX = w * 0.5f,
                baseY = h * 0.86f,
                flicker = flameFlicker,
                flameHeight = h * 0.16f * flameFlicker
            )
        }

        // 2. Draw Specific Laboratory Tool
        when (activeTool) {
            LabToolType.BEAKER -> {
                drawBeaker(
                    w = w,
                    h = h,
                    liquidColor = liquidColor,
                    fillPercent = liquidFillPercent,
                    wavePhase = wavePhase,
                    outlineColor = outlineColor,
                    glassHighlight = glassHighlight,
                    gridTickColor = gridTickColor,
                    temperature = temperature,
                    bubbleProgress = bubbleProgress,
                    bubbleOffsets = bubbleOffsets,
                    hasPrecipitate = hasPrecipitate,
                    hasUnreactedSolid = hasUnreactedSolid,
                    unreactedSolidColor = unreactedSolidColor,
                    transparencyAlpha = transparencyAlpha,
                    isReacting = isReacting
                )
            }
            LabToolType.ERLENMEYER_FLASK -> {
                drawFlask(
                    w = w,
                    h = h,
                    liquidColor = liquidColor,
                    fillPercent = liquidFillPercent,
                    wavePhase = wavePhase,
                    outlineColor = outlineColor,
                    glassHighlight = glassHighlight,
                    gridTickColor = gridTickColor,
                    temperature = temperature,
                    bubbleProgress = bubbleProgress,
                    bubbleOffsets = bubbleOffsets,
                    hasPrecipitate = hasPrecipitate,
                    hasUnreactedSolid = hasUnreactedSolid,
                    unreactedSolidColor = unreactedSolidColor,
                    transparencyAlpha = transparencyAlpha,
                    isReacting = isReacting
                )
            }
            LabToolType.TEST_TUBE -> {
                drawTestTube(
                    w = w,
                    h = h,
                    liquidColor = liquidColor,
                    fillPercent = liquidFillPercent,
                    wavePhase = wavePhase,
                    outlineColor = outlineColor,
                    glassHighlight = glassHighlight,
                    temperature = temperature,
                    bubbleProgress = bubbleProgress,
                    bubbleOffsets = bubbleOffsets,
                    hasPrecipitate = hasPrecipitate,
                    hasUnreactedSolid = hasUnreactedSolid,
                    unreactedSolidColor = unreactedSolidColor,
                    transparencyAlpha = transparencyAlpha,
                    isReacting = isReacting
                )
            }
            LabToolType.ELECTRODES -> {
                drawElectrolysisCell(
                    w = w,
                    h = h,
                    liquidColor = liquidColor,
                    fillPercent = liquidFillPercent,
                    outlineColor = outlineColor,
                    glassHighlight = glassHighlight,
                    isElectricityActive = isElectricityActive,
                    bubbleProgress = bubbleProgress
                )
            }
            LabToolType.CENTRIFUGE -> {
                drawCentrifuge(
                    w = w,
                    h = h,
                    outlineColor = outlineColor,
                    isCentrifuging = isCentrifuging,
                    wavePhase = wavePhase
                )
            }
            LabToolType.TITRATION_BURET -> {
                drawTitrationBuret(
                    w = w,
                    h = h,
                    liquidColor = liquidColor,
                    fillPercent = liquidFillPercent,
                    outlineColor = outlineColor,
                    glassHighlight = glassHighlight,
                    bubbleProgress = bubbleProgress
                )
            }
            LabToolType.CONDENSER -> {
                drawCondenser(
                    w = w,
                    h = h,
                    outlineColor = outlineColor,
                    glassHighlight = glassHighlight,
                    wavePhase = wavePhase
                )
            }
            LabToolType.EVAPORATING_DISH -> {
                drawEvaporatingDish(
                    w = w,
                    h = h,
                    liquidColor = liquidColor,
                    fillPercent = liquidFillPercent,
                    outlineColor = outlineColor,
                    glassHighlight = glassHighlight,
                    temperature = temperature,
                    bubbleProgress = bubbleProgress
                )
            }
            LabToolType.CRUCIBLE -> {
                drawCrucible(
                    w = w,
                    h = h,
                    outlineColor = outlineColor,
                    temperature = temperature,
                    isHeating = isHeating
                )
            }
            LabToolType.BUNSEN_BURNER -> {
                drawBunsenBurnerApparatus(
                    w = w,
                    h = h,
                    outlineColor = outlineColor,
                    flameFlicker = flameFlicker
                )
            }
        }

        // 3. 2D Element Particles & Interaction Bonds
        drawElementParticles(
            w = w,
            h = h,
            particles = particles,
            potentialInteractions = potentialInteractions
        )

        // 4. Steam particles when temperature > 90°C
        if (temperature > 90.0) {
            drawSteamVapor(
                w = w,
                h = h,
                bubbleProgress = bubbleProgress,
                steamOffsets = steamOffsets
            )
        }
    }
}

// --- DRAWING IMPLEMENTATIONS ---

private fun DrawScope.drawBeaker(
    w: Float,
    h: Float,
    liquidColor: Color,
    fillPercent: Float,
    wavePhase: Float,
    outlineColor: Color,
    glassHighlight: Color,
    gridTickColor: Color,
    temperature: Double,
    bubbleProgress: Float,
    bubbleOffsets: List<Pair<Float, Float>>,
    hasPrecipitate: Boolean,
    hasUnreactedSolid: Boolean,
    unreactedSolidColor: Color,
    transparencyAlpha: Float,
    isReacting: Boolean
) {
    val left = w * 0.22f
    val right = w * 0.78f
    val top = h * 0.22f
    val bottom = h * 0.78f
    val cornerRadius = 24f

    val glassPath = Path().apply {
        // Spout on top-left
        moveTo(left - 16f, top)
        lineTo(left + 24f, top)
        lineTo(right, top)
        lineTo(right, bottom - cornerRadius)
        cubicTo(right, bottom, right - cornerRadius, bottom, right - cornerRadius * 2, bottom)
        lineTo(left + cornerRadius * 2, bottom)
        cubicTo(left + cornerRadius, bottom, left, bottom, left, bottom - cornerRadius)
        lineTo(left, top + 14f)
        close()
    }

    // Liquid fill clipped inside glass
    if (fillPercent > 0.02f) {
        clipPath(glassPath) {
            val liquidHeight = (bottom - top) * fillPercent.coerceIn(0.08f, 0.88f)
            val liquidTop = bottom - liquidHeight

            val liquidPath = Path().apply {
                moveTo(left - 10f, bottom + 10f)
                lineTo(left - 10f, liquidTop)
                // Wavy meniscus
                val steps = 20
                val span = (right - left) + 20f
                for (i in 0..steps) {
                    val px = (left - 10f) + (span * (i.toFloat() / steps))
                    val py = liquidTop + sin(wavePhase + (i * 0.45f)) * 4.5f
                    if (i == 0) lineTo(px, py) else lineTo(px, py)
                }
                lineTo(right + 10f, bottom + 10f)
                close()
            }

            drawPath(
                path = liquidPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        liquidColor.copy(alpha = (transparencyAlpha * 0.85f).coerceIn(0.2f, 1f)),
                        liquidColor.copy(alpha = transparencyAlpha.coerceIn(0.3f, 1f))
                    ),
                    startY = liquidTop,
                    endY = bottom
                )
            )

            // Precipitate & Unreacted Solid Sediment Bed at bottom
            if (hasPrecipitate || hasUnreactedSolid) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF334155).copy(alpha = 0.82f),
                            Color(0xFF0F172A).copy(alpha = 0.96f)
                        ),
                        startY = bottom - 18f,
                        endY = bottom
                    ),
                    topLeft = Offset(left, bottom - 18f),
                    size = Size(right - left, 18f)
                )

                // Granular settled particles for excess unreacted solid reagents (e.g. Zinc, Iron, Mg, Sulfur)
                if (hasUnreactedSolid) {
                    val grainCount = 14
                    for (g in 0 until grainCount) {
                        val gx = left + 14f + (right - left - 28f) * ((g * 0.173f + 0.05f) % 1f)
                        val gy = bottom - 5f - ((g * 0.317f) % 1f) * 9f
                        val grainSize = 3.5f + (g % 3) * 1.5f
                        drawCircle(
                            color = unreactedSolidColor.copy(alpha = 0.95f),
                            radius = grainSize,
                            center = Offset(gx, gy)
                        )
                        drawCircle(
                            color = Color.Black.copy(alpha = 0.45f),
                            radius = grainSize,
                            center = Offset(gx, gy),
                            style = Stroke(width = 0.8f)
                        )
                    }
                }
            }

            // Gas Off-Gassing: Bubbling ceases immediately when reaction completes (limiting reagent exhausted)
            val bubbleCount = when {
                isReacting -> bubbleOffsets.size
                temperature > 95.0 -> bubbleOffsets.size / 2
                else -> 0 // Cease bubble animation immediately when limiting reagent is exhausted!
            }
            for (i in 0 until bubbleCount) {
                val seed = bubbleOffsets[i]
                val bx = left + (right - left) * (0.15f + seed.first * 0.7f)
                val by = bottom - ((bubbleProgress + seed.second) % 1f) * liquidHeight
                val radius = 3.5f + seed.first * 4f
                drawCircle(
                    color = Color.White.copy(alpha = 0.70f),
                    radius = radius,
                    center = Offset(bx, by)
                )
            }
        }
    }

    // Glass outline
    drawPath(
        path = glassPath,
        color = outlineColor,
        style = Stroke(width = 5.5f, cap = StrokeCap.Round)
    )

    // Glass sheen / highlight line
    drawLine(
        color = glassHighlight,
        start = Offset(left + 14f, top + 30f),
        end = Offset(left + 14f, bottom - 30f),
        strokeWidth = 3f,
        cap = StrokeCap.Round
    )

    // Volume Graduations & mL labels
    val tickSteps = 5
    for (i in 1..tickSteps) {
        val ty = bottom - (bottom - top) * 0.85f * (i.toFloat() / tickSteps)
        val tickWidth = if (i % 2 == 0) 24f else 14f
        drawLine(
            color = gridTickColor,
            start = Offset(right - tickWidth, ty),
            end = Offset(right - 4f, ty),
            strokeWidth = 2.5f
        )
    }
}

private fun DrawScope.drawFlask(
    w: Float,
    h: Float,
    liquidColor: Color,
    fillPercent: Float,
    wavePhase: Float,
    outlineColor: Color,
    glassHighlight: Color,
    gridTickColor: Color,
    temperature: Double,
    bubbleProgress: Float,
    bubbleOffsets: List<Pair<Float, Float>>,
    hasPrecipitate: Boolean,
    hasUnreactedSolid: Boolean,
    unreactedSolidColor: Color,
    transparencyAlpha: Float,
    isReacting: Boolean
) {
    val neckLeft = w * 0.44f
    val neckRight = w * 0.56f
    val top = h * 0.16f
    val neckBottom = h * 0.36f
    val baseLeft = w * 0.20f
    val baseRight = w * 0.80f
    val bottom = h * 0.80f
    val cornerRadius = 26f

    val flaskPath = Path().apply {
        // Lip
        moveTo(neckLeft - 8f, top)
        lineTo(neckRight + 8f, top)
        lineTo(neckRight, top + 6f)
        lineTo(neckRight, neckBottom)
        // Sloping body to base
        lineTo(baseRight, bottom - cornerRadius)
        cubicTo(baseRight, bottom, baseRight - cornerRadius, bottom, baseRight - cornerRadius * 2, bottom)
        lineTo(baseLeft + cornerRadius * 2, bottom)
        cubicTo(baseLeft + cornerRadius, bottom, baseLeft, bottom, baseLeft, bottom - cornerRadius)
        lineTo(neckLeft, neckBottom)
        lineTo(neckLeft, top + 6f)
        close()
    }

    if (fillPercent > 0.02f) {
        clipPath(flaskPath) {
            val liquidHeight = (bottom - neckBottom) * fillPercent.coerceIn(0.1f, 0.92f)
            val liquidTop = bottom - liquidHeight

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        liquidColor.copy(alpha = (transparencyAlpha * 0.82f).coerceIn(0.2f, 1f)),
                        liquidColor.copy(alpha = transparencyAlpha.coerceIn(0.3f, 1f))
                    ),
                    startY = liquidTop,
                    endY = bottom
                ),
                topLeft = Offset(baseLeft - 20f, liquidTop),
                size = Size((baseRight - baseLeft) + 40f, liquidHeight + 30f)
            )

            // Meniscus wave
            val wavePath = Path().apply {
                moveTo(baseLeft - 20f, liquidTop)
                val steps = 20
                val span = (baseRight - baseLeft) + 40f
                for (i in 0..steps) {
                    val px = (baseLeft - 20f) + (span * (i.toFloat() / steps))
                    val py = liquidTop + sin(wavePhase + (i * 0.4f)) * 4f
                    lineTo(px, py)
                }
                lineTo(baseRight + 20f, liquidTop - 10f)
                lineTo(baseLeft - 20f, liquidTop - 10f)
                close()
            }
            drawPath(wavePath, color = liquidColor.copy(alpha = (transparencyAlpha * 0.9f).coerceIn(0.2f, 1f)))

            // Settled Precipitate & Unreacted Solid Sediment Bed at bottom
            if (hasPrecipitate || hasUnreactedSolid) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF334155).copy(alpha = 0.85f),
                            Color(0xFF0F172A).copy(alpha = 0.96f)
                        ),
                        startY = bottom - 18f,
                        endY = bottom
                    ),
                    topLeft = Offset(baseLeft, bottom - 18f),
                    size = Size(baseRight - baseLeft, 18f)
                )

                if (hasUnreactedSolid) {
                    val grainCount = 16
                    for (g in 0 until grainCount) {
                        val gx = baseLeft + 16f + (baseRight - baseLeft - 32f) * ((g * 0.173f + 0.05f) % 1f)
                        val gy = bottom - 5f - ((g * 0.317f) % 1f) * 9f
                        val grainSize = 3.5f + (g % 3) * 1.5f
                        drawCircle(
                            color = unreactedSolidColor.copy(alpha = 0.95f),
                            radius = grainSize,
                            center = Offset(gx, gy)
                        )
                        drawCircle(
                            color = Color.Black.copy(alpha = 0.45f),
                            radius = grainSize,
                            center = Offset(gx, gy),
                            style = Stroke(width = 0.8f)
                        )
                    }
                }
            }

            // Gas Off-Gassing: Bubbling ceases immediately when reaction completes
            val count = when {
                isReacting -> bubbleOffsets.size
                temperature > 95.0 -> bubbleOffsets.size / 2
                else -> 0 // Cease bubble animation immediately!
            }
            for (i in 0 until count) {
                val seed = bubbleOffsets[i]
                val bx = baseLeft + (baseRight - baseLeft) * (0.2f + seed.first * 0.6f)
                val by = bottom - ((bubbleProgress + seed.second) % 1f) * liquidHeight
                drawCircle(
                    color = Color.White.copy(alpha = 0.68f),
                    radius = 3.5f + seed.second * 3.5f,
                    center = Offset(bx, by)
                )
            }
        }
    }

    drawPath(
        path = flaskPath,
        color = outlineColor,
        style = Stroke(width = 5.5f, cap = StrokeCap.Round)
    )

    // Highlight sheen along neck and shoulder
    drawLine(
        color = glassHighlight,
        start = Offset(neckLeft + 6f, top + 14f),
        end = Offset(baseLeft + 28f, bottom - 26f),
        strokeWidth = 3f,
        cap = StrokeCap.Round
    )

    // Graduation lines
    for (i in 1..4) {
        val ty = bottom - (bottom - neckBottom) * (i.toFloat() / 5f)
        drawLine(
            color = gridTickColor,
            start = Offset(w * 0.58f, ty),
            end = Offset(w * 0.66f, ty),
            strokeWidth = 2f
        )
    }
}

private fun DrawScope.drawTestTube(
    w: Float,
    h: Float,
    liquidColor: Color,
    fillPercent: Float,
    wavePhase: Float,
    outlineColor: Color,
    glassHighlight: Color,
    temperature: Double,
    bubbleProgress: Float,
    bubbleOffsets: List<Pair<Float, Float>>,
    hasPrecipitate: Boolean,
    hasUnreactedSolid: Boolean,
    unreactedSolidColor: Color,
    transparencyAlpha: Float,
    isReacting: Boolean
) {
    val tubeLeft = w * 0.40f
    val tubeRight = w * 0.60f
    val top = h * 0.16f
    val bottom = h * 0.82f
    val radius = (tubeRight - tubeLeft) / 2f

    val tubePath = Path().apply {
        moveTo(tubeLeft - 6f, top)
        lineTo(tubeRight + 6f, top)
        lineTo(tubeRight, top + 6f)
        lineTo(tubeRight, bottom - radius)
        cubicTo(tubeRight, bottom, tubeLeft, bottom, tubeLeft, bottom - radius)
        lineTo(tubeLeft, top + 6f)
        close()
    }

    if (fillPercent > 0.02f) {
        clipPath(tubePath) {
            val liquidHeight = (bottom - top) * fillPercent.coerceIn(0.1f, 0.9f)
            val liquidTop = bottom - liquidHeight

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        liquidColor.copy(alpha = (transparencyAlpha * 0.85f).coerceIn(0.2f, 1f)),
                        liquidColor.copy(alpha = transparencyAlpha.coerceIn(0.3f, 1f))
                    ),
                    startY = liquidTop,
                    endY = bottom
                ),
                topLeft = Offset(tubeLeft - 10f, liquidTop),
                size = Size((tubeRight - tubeLeft) + 20f, liquidHeight + 20f)
            )

            // Settled precipitate rounded bottom
            if (hasPrecipitate || hasUnreactedSolid) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF334155).copy(alpha = 0.88f),
                            Color(0xFF0F172A).copy(alpha = 0.96f)
                        ),
                        center = Offset(w * 0.5f, bottom - radius),
                        radius = radius * 0.95f
                    ),
                    radius = radius * 0.95f,
                    center = Offset(w * 0.5f, bottom - radius)
                )

                if (hasUnreactedSolid) {
                    val grainCount = 10
                    for (g in 0 until grainCount) {
                        val gx = (w * 0.5f) - (radius * 0.6f) + (radius * 1.2f) * ((g * 0.231f) % 1f)
                        val gy = bottom - radius * 0.8f + (radius * 0.5f) * ((g * 0.417f) % 1f)
                        val grainSize = 2.8f + (g % 3) * 1.2f
                        drawCircle(
                            color = unreactedSolidColor.copy(alpha = 0.95f),
                            radius = grainSize,
                            center = Offset(gx, gy)
                        )
                        drawCircle(
                            color = Color.Black.copy(alpha = 0.45f),
                            radius = grainSize,
                            center = Offset(gx, gy),
                            style = Stroke(width = 0.7f)
                        )
                    }
                }
            }

            // Gas Off-Gassing: Bubbling ceases immediately when reaction completes
            val count = when {
                isReacting -> 10
                temperature > 95.0 -> 5
                else -> 0 // Cease bubble animation immediately!
            }
            for (i in 0 until count) {
                val seed = bubbleOffsets[i]
                val bx = tubeLeft + (tubeRight - tubeLeft) * (0.25f + seed.first * 0.5f)
                val by = bottom - ((bubbleProgress + seed.second) % 1f) * liquidHeight
                drawCircle(
                    color = Color.White.copy(alpha = 0.68f),
                    radius = 3f + seed.second * 3f,
                    center = Offset(bx, by)
                )
            }
        }
    }

    drawPath(
        path = tubePath,
        color = outlineColor,
        style = Stroke(width = 5.5f, cap = StrokeCap.Round)
    )

    // Glass reflection
    drawLine(
        color = glassHighlight,
        start = Offset(tubeLeft + 6f, top + 20f),
        end = Offset(tubeLeft + 6f, bottom - radius - 10f),
        strokeWidth = 3f
    )
}

private fun DrawScope.drawElectrolysisCell(
    w: Float,
    h: Float,
    liquidColor: Color,
    fillPercent: Float,
    outlineColor: Color,
    glassHighlight: Color,
    isElectricityActive: Boolean,
    bubbleProgress: Float
) {
    // Tank
    val left = w * 0.22f
    val right = w * 0.78f
    val top = h * 0.30f
    val bottom = h * 0.82f

    // Draw electrolyte solution
    drawRect(
        color = liquidColor.copy(alpha = 0.65f),
        topLeft = Offset(left, top + (bottom - top) * 0.25f),
        size = Size(right - left, (bottom - top) * 0.75f)
    )

    // Glass Tank Outline
    drawRect(
        color = outlineColor,
        topLeft = Offset(left, top),
        size = Size(right - left, bottom - top),
        style = Stroke(width = 5.5f)
    )

    // Electrode 1: Cathode (-) Red/Pink
    val cathodeX = w * 0.38f
    drawRect(
        color = Color(0xFFEF4444),
        topLeft = Offset(cathodeX - 8f, top - 30f),
        size = Size(16f, (bottom - top) * 0.85f)
    )
    // Cathode wire & terminal (-)
    drawLine(
        color = Color(0xFFEF4444),
        start = Offset(cathodeX, top - 30f),
        end = Offset(cathodeX - 30f, top - 60f),
        strokeWidth = 4f
    )

    // Electrode 2: Anode (+) Cyan/Blue
    val anodeX = w * 0.62f
    drawRect(
        color = Color(0xFF0284C7),
        topLeft = Offset(anodeX - 8f, top - 30f),
        size = Size(16f, (bottom - top) * 0.85f)
    )
    // Anode wire & terminal (+)
    drawLine(
        color = Color(0xFF0284C7),
        start = Offset(anodeX, top - 30f),
        end = Offset(anodeX + 30f, top - 60f),
        strokeWidth = 4f
    )

    // Electrolysis Bubbles if electricity is active
    if (isElectricityActive) {
        for (i in 0..12) {
            val prog = (bubbleProgress + (i * 0.08f)) % 1f
            // Cathode bubbles (H2)
            drawCircle(
                color = Color.White.copy(alpha = 0.8f),
                radius = 3.5f,
                center = Offset(cathodeX + (if (i % 2 == 0) 10f else -10f), bottom - 20f - prog * 180f)
            )
            // Anode bubbles (O2)
            drawCircle(
                color = Color.White.copy(alpha = 0.8f),
                radius = 4.5f,
                center = Offset(anodeX + (if (i % 2 == 0) 10f else -10f), bottom - 20f - prog * 180f)
            )
        }
    }
}

private fun DrawScope.drawCentrifuge(
    w: Float,
    h: Float,
    outlineColor: Color,
    isCentrifuging: Boolean,
    wavePhase: Float
) {
    val cx = w * 0.5f
    val cy = h * 0.52f
    val outerRadius = w * 0.32f

    // Outer Housing
    drawCircle(
        color = outlineColor.copy(alpha = 0.15f),
        radius = outerRadius,
        center = Offset(cx, cy)
    )
    drawCircle(
        color = outlineColor,
        radius = outerRadius,
        center = Offset(cx, cy),
        style = Stroke(width = 6f)
    )

    // Inner rotor
    val rotorRadius = outerRadius * 0.68f
    drawCircle(
        color = outlineColor.copy(alpha = 0.25f),
        radius = rotorRadius,
        center = Offset(cx, cy)
    )

    // 4 Centrifuge tube buckets
    val angleOffset = if (isCentrifuging) wavePhase * 4f else 0.785f
    for (i in 0 until 4) {
        val angle = angleOffset + (i * 1.57079f)
        val tubeX = cx + rotorRadius * 0.7f * kotlin.math.cos(angle)
        val tubeY = cy + rotorRadius * 0.7f * sin(angle)

        drawCircle(
            color = Color(0xFF0284C7),
            radius = 16f,
            center = Offset(tubeX, tubeY)
        )
        drawCircle(
            color = Color.White,
            radius = 6f,
            center = Offset(tubeX, tubeY)
        )
    }

    // RPM indicator text / center hub
    drawCircle(
        color = outlineColor,
        radius = 20f,
        center = Offset(cx, cy)
    )
}

private fun DrawScope.drawTitrationBuret(
    w: Float,
    h: Float,
    liquidColor: Color,
    fillPercent: Float,
    outlineColor: Color,
    glassHighlight: Color,
    bubbleProgress: Float
) {
    val buretLeft = w * 0.44f
    val buretRight = w * 0.56f
    val top = h * 0.08f
    val stopcockY = h * 0.72f
    val tipY = h * 0.84f

    // Liquid in column
    val fillHeight = (stopcockY - top) * fillPercent.coerceIn(0.1f, 0.95f)
    drawRect(
        color = liquidColor.copy(alpha = 0.75f),
        topLeft = Offset(buretLeft, stopcockY - fillHeight),
        size = Size(buretRight - buretLeft, fillHeight)
    )

    // Column outline
    drawLine(color = outlineColor, start = Offset(buretLeft, top), end = Offset(buretLeft, stopcockY), strokeWidth = 4.5f)
    drawLine(color = outlineColor, start = Offset(buretRight, top), end = Offset(buretRight, stopcockY), strokeWidth = 4.5f)

    // Graduations
    for (i in 0..15) {
        val gy = top + (stopcockY - top) * (i / 15f)
        drawLine(
            color = outlineColor.copy(alpha = 0.6f),
            start = Offset(buretRight - 12f, gy),
            end = Offset(buretRight, gy),
            strokeWidth = 2f
        )
    }

    // Stopcock Valve
    drawCircle(color = Color(0xFFDC2626), radius = 14f, center = Offset(w * 0.5f, stopcockY))
    drawLine(color = Color(0xFFDC2626), start = Offset(w * 0.5f - 24f, stopcockY), end = Offset(w * 0.5f + 24f, stopcockY), strokeWidth = 5f)

    // Dispensing Tip
    val tipPath = Path().apply {
        moveTo(buretLeft + 8f, stopcockY + 12f)
        lineTo(w * 0.5f, tipY)
        lineTo(buretRight - 8f, stopcockY + 12f)
        close()
    }
    drawPath(tipPath, color = outlineColor, style = Stroke(width = 4f))

    // Liquid Droplet falling from tip
    val dropY = tipY + (bubbleProgress * 45f)
    drawCircle(
        color = liquidColor.copy(alpha = 0.85f),
        radius = 4f,
        center = Offset(w * 0.5f, dropY)
    )
}

private fun DrawScope.drawCondenser(
    w: Float,
    h: Float,
    outlineColor: Color,
    glassHighlight: Color,
    wavePhase: Float
) {
    val outerLeft = w * 0.34f
    val outerRight = w * 0.66f
    val innerLeft = w * 0.44f
    val innerRight = w * 0.56f
    val top = h * 0.12f
    val bottom = h * 0.82f

    // Cooling water jacket (cyan tint)
    drawRect(
        color = Color(0xFF38BDF8).copy(alpha = 0.35f),
        topLeft = Offset(outerLeft, top + 40f),
        size = Size(outerRight - outerLeft, bottom - top - 80f)
    )

    // Outer Jacket outline
    drawRect(
        color = outlineColor,
        topLeft = Offset(outerLeft, top + 40f),
        size = Size(outerRight - outerLeft, bottom - top - 80f),
        style = Stroke(width = 5f)
    )

    // Water In/Out Nozzles
    drawRect(color = outlineColor, topLeft = Offset(outerLeft - 22f, bottom - top - 70f), size = Size(22f, 14f), style = Stroke(width = 3.5f))
    drawRect(color = outlineColor, topLeft = Offset(outerRight, top + 60f), size = Size(22f, 14f), style = Stroke(width = 3.5f))

    // Inner vapor tube
    drawLine(color = outlineColor, start = Offset(innerLeft, top), end = Offset(innerLeft, bottom), strokeWidth = 4.5f)
    drawLine(color = outlineColor, start = Offset(innerRight, top), end = Offset(innerRight, bottom), strokeWidth = 4.5f)

    // Condensation droplets inside vapor tube
    for (i in 0..6) {
        val dy = top + 80f + (i * 45f) + sin(wavePhase + i) * 6f
        drawCircle(
            color = Color(0xFFE0F2FE),
            radius = 3.5f,
            center = Offset(w * 0.5f + if (i % 2 == 0) -8f else 8f, dy)
        )
    }
}

private fun DrawScope.drawEvaporatingDish(
    w: Float,
    h: Float,
    liquidColor: Color,
    fillPercent: Float,
    outlineColor: Color,
    glassHighlight: Color,
    temperature: Double,
    bubbleProgress: Float
) {
    val left = w * 0.20f
    val right = w * 0.80f
    val top = h * 0.44f
    val bottom = h * 0.68f

    val dishPath = Path().apply {
        moveTo(left - 15f, top)
        lineTo(left, top + 6f)
        cubicTo(left + 20f, bottom, right - 20f, bottom, right, top + 6f)
        lineTo(right + 15f, top)
        close()
    }

    // Liquid shallow pool
    if (fillPercent > 0.05f) {
        clipPath(dishPath) {
            drawRect(
                color = liquidColor.copy(alpha = 0.8f),
                topLeft = Offset(left, top + 15f),
                size = Size(right - left, bottom - top)
            )
        }
    }

    drawPath(dishPath, color = outlineColor, style = Stroke(width = 6f, cap = StrokeCap.Round))
    // Porcelain white fill sheen
    drawPath(dishPath, color = Color.White.copy(alpha = 0.12f))
}

private fun DrawScope.drawCrucible(
    w: Float,
    h: Float,
    outlineColor: Color,
    temperature: Double,
    isHeating: Boolean
) {
    val left = w * 0.32f
    val right = w * 0.68f
    val top = h * 0.38f
    val bottom = h * 0.66f

    val glowColor = if (temperature > 500.0) Color(0xFFF97316).copy(alpha = 0.45f) else Color.Transparent

    val cruciblePath = Path().apply {
        moveTo(left, top)
        lineTo(right, top)
        lineTo(right - 25f, bottom)
        lineTo(left + 25f, bottom)
        close()
    }

    // Glowing thermal heat if extreme temperature
    if (temperature > 300.0) {
        drawPath(cruciblePath, color = glowColor)
    }

    drawPath(cruciblePath, color = outlineColor, style = Stroke(width = 6f))

    // Lid
    val lidPath = Path().apply {
        moveTo(left - 10f, top)
        lineTo(right + 10f, top)
        lineTo(w * 0.5f + 12f, top - 18f)
        lineTo(w * 0.5f - 12f, top - 18f)
        close()
    }
    drawPath(lidPath, color = outlineColor, style = Stroke(width = 4.5f))

    // Tripod wire support
    drawLine(color = outlineColor, start = Offset(left - 18f, bottom), end = Offset(left - 40f, bottom + 65f), strokeWidth = 5f)
    drawLine(color = outlineColor, start = Offset(right + 18f, bottom), end = Offset(right + 40f, bottom + 65f), strokeWidth = 5f)
    drawLine(color = outlineColor, start = Offset(left - 24f, bottom), end = Offset(right + 24f, bottom), strokeWidth = 4f)
}

private fun DrawScope.drawBunsenBurnerApparatus(
    w: Float,
    h: Float,
    outlineColor: Color,
    flameFlicker: Float
) {
    val cx = w * 0.5f
    val barrelTop = h * 0.45f
    val barrelBottom = h * 0.78f
    val barrelWidth = 24f

    // Heavy Metal Base
    drawRect(
        color = outlineColor,
        topLeft = Offset(cx - 70f, barrelBottom),
        size = Size(140f, 22f)
    )

    // Vertical Barrel
    drawRect(
        color = outlineColor,
        topLeft = Offset(cx - barrelWidth / 2f, barrelTop),
        size = Size(barrelWidth, barrelBottom - barrelTop)
    )

    // Air Collar holes
    drawCircle(color = Color.Black, radius = 5f, center = Offset(cx, barrelBottom - 25f))

    // Flame
    drawBurnerFlame(
        centerX = cx,
        baseY = barrelTop,
        flicker = flameFlicker,
        flameHeight = h * 0.22f * flameFlicker
    )
}

private fun DrawScope.drawBurnerFlame(
    centerX: Float,
    baseY: Float,
    flicker: Float,
    flameHeight: Float
) {
    // Outer flame (Luminous Cyan/Orange)
    val outerPath = Path().apply {
        moveTo(centerX - 24f * flicker, baseY)
        cubicTo(
            centerX - 35f * flicker, baseY - flameHeight * 0.4f,
            centerX - 10f, baseY - flameHeight,
            centerX, baseY - flameHeight
        )
        cubicTo(
            centerX + 10f, baseY - flameHeight,
            centerX + 35f * flicker, baseY - flameHeight * 0.4f,
            centerX + 24f * flicker, baseY
        )
        close()
    }

    drawPath(
        path = outerPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF38BDF8).copy(alpha = 0.2f),
                Color(0xFF0284C7).copy(alpha = 0.85f),
                Color(0xFFF97316).copy(alpha = 0.95f)
            ),
            startY = baseY - flameHeight,
            endY = baseY
        )
    )

    // Inner Cone (Hot Deep Blue)
    val innerHeight = flameHeight * 0.55f
    val innerPath = Path().apply {
        moveTo(centerX - 12f, baseY)
        lineTo(centerX, baseY - innerHeight)
        lineTo(centerX + 12f, baseY)
        close()
    }

    drawPath(
        path = innerPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF67E8F9),
                Color(0xFF1D4ED8)
            ),
            startY = baseY - innerHeight,
            endY = baseY
        )
    )
}

private fun DrawScope.drawSteamVapor(
    w: Float,
    h: Float,
    bubbleProgress: Float,
    steamOffsets: List<Pair<Float, Float>>
) {
    val top = h * 0.16f
    for (i in steamOffsets.indices) {
        val seed = steamOffsets[i]
        val prog = (bubbleProgress + seed.second) % 1f
        val sx = w * 0.5f + (seed.first - 0.5f) * 120f * (1f + prog)
        val sy = top - prog * 100f
        val alpha = ((1f - prog) * 0.45f).coerceIn(0f, 1f)
        drawCircle(
            color = Color.White.copy(alpha = alpha),
            radius = 12f + prog * 18f,
            center = Offset(sx, sy)
        )
    }
}

private fun DrawScope.drawElementParticles(
    w: Float,
    h: Float,
    particles: List<ElementParticle2D>,
    potentialInteractions: List<PotentialInteraction>
) {
    if (particles.isEmpty()) return

    // 1. Draw connection lines between interacting particles
    potentialInteractions.forEach { interaction ->
        if (interaction.participatingParticles.size >= 2) {
            val p1 = interaction.participatingParticles[0]
            val p2 = interaction.participatingParticles[1]
            val p1Pos = Offset(p1.x * w, p1.y * h)
            val p2Pos = Offset(p2.x * w, p2.y * h)
            val lineColor = if (interaction.isActivationEnergyMet) Color(0xFFFBBF24) else Color.White.copy(alpha = 0.35f)
            drawLine(
                color = lineColor,
                start = p1Pos,
                end = p2Pos,
                strokeWidth = if (interaction.isActivationEnergyMet) 3f else 1.5f
            )
        }
    }

    // 2. Draw individual element particles with halo and core
    particles.forEach { p ->
        val cx = p.x * w
        val cy = p.y * h
        val r = p.radius

        // Ambient halo
        drawCircle(
            color = p.color.copy(alpha = 0.25f),
            radius = r * 1.4f,
            center = Offset(cx, cy)
        )

        // Core particle
        drawCircle(
            color = p.color.copy(alpha = 0.85f),
            radius = r,
            center = Offset(cx, cy)
        )

        // Border outline
        drawCircle(
            color = Color.White.copy(alpha = 0.7f),
            radius = r,
            center = Offset(cx, cy),
            style = Stroke(width = 1.5f)
        )
    }
}
