package com.example.engine.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import com.example.data.model.Chemical
import com.example.data.model.LabToolType
import com.example.data.model.Reaction
import java.util.UUID

enum class ParticlePhase {
    SOLID,
    LIQUID,
    GAS,
    PRECIPITATE,
    DISSOLVED
}

data class ElementParticle2D(
    val instanceId: String = UUID.randomUUID().toString(),
    val chemical: Chemical,
    var x: Float,
    var y: Float,
    var vx: Float = 0f,
    var vy: Float = 0f,
    var radius: Float = 14f,
    var mass: Double = chemical.molarMass,
    var phase: ParticlePhase = when {
        chemical.physicalState.contains("Gas", ignoreCase = true) -> ParticlePhase.GAS
        chemical.physicalState.contains("Solid", ignoreCase = true) || chemical.physicalState.contains("Precipitate", ignoreCase = true) -> ParticlePhase.SOLID
        else -> ParticlePhase.LIQUID
    },
    var localizedTemp: Double = 25.0,
    var interactionRadius: Float = 36f,
    var opacity: Float = 1.0f,
    val color: Color = Color(chemical.colorHex)
) {
    val position: Offset get() = Offset(x, y)
    val velocity: Offset get() = Offset(vx, vy)
}

data class PotentialInteraction(
    val id: String = UUID.randomUUID().toString(),
    val reaction: Reaction,
    val participatingParticles: List<ElementParticle2D>,
    val centerPosition: Offset,
    val readinessPercentage: Float, // 0.0 to 1.0 based on temp, apparatus, activation energy
    val isActivationEnergyMet: Boolean,
    val conditionSummary: String
)

data class ContainerBounds2D(
    val toolType: LabToolType,
    val left: Float,
    val right: Float,
    val top: Float,
    val bottom: Float,
    val liquidTopY: Float
) {
    val width: Float get() = (right - left).coerceAtLeast(1f)
    val height: Float get() = (bottom - top).coerceAtLeast(1f)
    val rect: Rect get() = Rect(left, top, right, bottom)

    fun contains(x: Float, y: Float): Boolean {
        return x in left..right && y in top..bottom
    }
}

data class ChemistryEngineState(
    val particles: List<ElementParticle2D> = emptyList(),
    val potentialInteractions: List<PotentialInteraction> = emptyList(),
    val totalMoles: Double = 0.0,
    val averageTemperature: Double = 25.0,
    val averagePh: Double = 7.0,
    val dominantColorHex: Long = 0xAA38BDF8L,
    val hasPrecipitate: Boolean = false,
    val activeReactionCount: Int = 0
)
