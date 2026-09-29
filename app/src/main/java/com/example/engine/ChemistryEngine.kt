package com.example.engine

import androidx.compose.ui.geometry.Offset
import com.example.data.model.Chemical
import com.example.data.model.ChemicalCatalog
import com.example.data.model.LabToolType
import com.example.data.model.Reaction
import com.example.engine.model.ChemistryEngineState
import com.example.engine.model.ContainerBounds2D
import com.example.engine.model.ElementParticle2D
import com.example.engine.model.ParticlePhase
import com.example.engine.model.PotentialInteraction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * ChemistryEngine manages the state of individual chemical elements,
 * their physical/chemical properties, and their potential interactions
 * within the 2D laboratory space.
 */
class ChemistryEngine(
    initialTool: LabToolType = LabToolType.BEAKER
) {
    private val particles = mutableListOf<ElementParticle2D>()

    private var activeTool: LabToolType = initialTool
    private var containerBounds: ContainerBounds2D = ContainerBounds2D(
        toolType = initialTool,
        left = 0.15f,
        right = 0.85f,
        top = 0.35f,
        bottom = 0.88f,
        liquidTopY = 0.50f
    )

    private var systemTemperature: Double = 25.0
    private var isHeating: Boolean = false
    private var isElectricityActive: Boolean = false
    private var isCentrifuging: Boolean = false

    private val _engineState = MutableStateFlow(ChemistryEngineState())
    val engineState: StateFlow<ChemistryEngineState> = _engineState.asStateFlow()

    fun updateEnvironment(
        tool: LabToolType,
        temperature: Double,
        heating: Boolean,
        electricity: Boolean,
        centrifuging: Boolean
    ) {
        activeTool = tool
        systemTemperature = temperature
        isHeating = heating
        isElectricityActive = electricity
        isCentrifuging = centrifuging
        particles.forEach { it.localizedTemp = temperature }
        recalculateState()
    }

    fun setContainerBounds(bounds: ContainerBounds2D) {
        containerBounds = bounds
    }

    /**
     * Spawns an individual chemical element or compound particle in 2D space.
     */
    fun spawnElement(
        chemical: Chemical,
        x: Float = Random.nextFloat() * (containerBounds.right - containerBounds.left) + containerBounds.left,
        y: Float = Random.nextFloat() * (containerBounds.bottom - containerBounds.liquidTopY) + containerBounds.liquidTopY
    ): ElementParticle2D {
        val initialPhase = when {
            chemical.physicalState.contains("Gas", ignoreCase = true) -> ParticlePhase.GAS
            chemical.physicalState.contains("Solid", ignoreCase = true) || chemical.physicalState.contains("Precipitate", ignoreCase = true) -> ParticlePhase.SOLID
            else -> ParticlePhase.LIQUID
        }

        val particle = ElementParticle2D(
            chemical = chemical,
            x = x.coerceIn(containerBounds.left + 0.02f, containerBounds.right - 0.02f),
            y = y.coerceIn(containerBounds.liquidTopY, containerBounds.bottom - 0.02f),
            vx = (Random.nextFloat() - 0.5f) * 0.04f,
            vy = (Random.nextFloat() - 0.5f) * 0.04f,
            radius = (10f + (chemical.molarMass.toFloat() / 25f)).coerceIn(8f, 22f),
            phase = initialPhase,
            localizedTemp = systemTemperature
        )
        particles.add(particle)
        recalculateState()
        return particle
    }

    fun spawnElementById(chemicalId: String, count: Int = 1) {
        val chem = ChemicalCatalog.getChemical(chemicalId) ?: return
        repeat(count) {
            spawnElement(chem)
        }
    }

    fun removeParticle(instanceId: String) {
        particles.removeAll { it.instanceId == instanceId }
        recalculateState()
    }

    fun removeChemical(chemicalId: String) {
        particles.removeAll { it.chemical.id.equals(chemicalId, ignoreCase = true) || it.chemical.symbol.equals(chemicalId, ignoreCase = true) }
        recalculateState()
    }

    fun clear() {
        particles.clear()
        recalculateState()
    }

    /**
     * Synchronizes particle distribution with active chemicals map.
     */
    fun syncFromChemicalMap(activeChemicals: Map<String, Int>) {
        val currentCounts = particles.groupBy { it.chemical.id }.mapValues { it.value.size }

        activeChemicals.forEach { (chemId, targetAmount) ->
            val chem = ChemicalCatalog.getChemical(chemId) ?: return@forEach
            val targetParticleCount = (targetAmount / 15).coerceIn(2, 16)
            val currentCount = currentCounts[chemId] ?: 0

            if (currentCount < targetParticleCount) {
                repeat(targetParticleCount - currentCount) {
                    spawnElement(chem)
                }
            } else if (currentCount > targetParticleCount) {
                var toRemove = currentCount - targetParticleCount
                val iter = particles.iterator()
                while (iter.hasNext() && toRemove > 0) {
                    val p = iter.next()
                    if (p.chemical.id == chemId) {
                        iter.remove()
                        toRemove--
                    }
                }
            }
        }

        // Remove chemicals no longer present
        particles.removeAll { it.chemical.id !in activeChemicals.keys }
        recalculateState()
    }

    /**
     * Advances the 2D physics simulation step:
     * - Brownian motion / thermal agitation proportional to sqrt(T)
     * - Gravitational settling for dense solids & precipitates
     * - Buoyant ascending forces for gaseous elements
     * - Boundary constraints against the active container walls
     * - Centrifugal radial pull if centrifuge is active
     */
    fun step(dt: Float = 0.016f) {
        val thermalSpeed = (sqrt(max(1.0, systemTemperature)) * 0.003f).toFloat()

        particles.forEach { p ->
            // 1. Thermal Brownian motion
            p.vx += (Random.nextFloat() - 0.5f) * thermalSpeed
            p.vy += (Random.nextFloat() - 0.5f) * thermalSpeed

            // 2. Phase-dependent forces (Buoyancy / Gravity / Centrifuge)
            when (p.phase) {
                ParticlePhase.GAS -> {
                    // Upward buoyancy
                    p.vy -= 0.015f * dt
                }
                ParticlePhase.PRECIPITATE, ParticlePhase.SOLID -> {
                    // Downward sedimentation
                    val settlingRate = if (isCentrifuging) 0.12f else 0.035f
                    p.vy += settlingRate * dt
                }
                else -> {
                    // Gentle neutral buoyancy drift
                    p.vy += 0.005f * dt
                }
            }

            // 3. Electrical field migration (Electrolysis)
            if (isElectricityActive && activeTool == LabToolType.ELECTRODES) {
                if (p.chemical.category == com.example.data.model.ChemicalCategory.ACID || p.chemical.id == "H") {
                    // Cations drift left towards cathode
                    p.vx -= 0.04f * dt
                } else if (p.chemical.category == com.example.data.model.ChemicalCategory.BASE || p.chemical.id == "O") {
                    // Anions drift right towards anode
                    p.vx += 0.04f * dt
                }
            }

            // 4. Dampen velocity (viscous drag of liquid)
            p.vx *= 0.94f
            p.vy *= 0.94f

            // 5. Update positions
            p.x += p.vx
            p.y += p.vy

            // 6. Wall and liquid surface boundary collisions
            val leftLimit = containerBounds.left + 0.01f
            val rightLimit = containerBounds.right - 0.01f
            val topLimit = if (p.phase == ParticlePhase.GAS) containerBounds.top else containerBounds.liquidTopY
            val bottomLimit = containerBounds.bottom - 0.01f

            if (p.x < leftLimit) {
                p.x = leftLimit
                p.vx = -p.vx * 0.7f
            } else if (p.x > rightLimit) {
                p.x = rightLimit
                p.vx = -p.vx * 0.7f
            }

            if (p.y < topLimit) {
                p.y = topLimit
                if (p.phase == ParticlePhase.GAS) {
                    // Gases bubble out into atmosphere
                    p.vy = -0.01f
                } else {
                    p.vy = -p.vy * 0.7f
                }
            } else if (p.y > bottomLimit) {
                p.y = bottomLimit
                p.vy = -p.vy * 0.5f
            }
        }

        recalculateState()
    }

    /**
     * Evaluates potential interactions between elements present in the 2D space.
     */
    fun detectPotentialInteractions(): List<PotentialInteraction> {
        if (particles.isEmpty()) return emptyList()

        val presentChemicalIds = particles.map { it.chemical.id }.toSet()
        val potentialInteractions = mutableListOf<PotentialInteraction>()

        ChemicalCatalog.REACTIONS.forEach { reaction ->
            val hasReactants = reaction.reactantIds.all { rId ->
                val norm = ChemicalCatalog.normalizeReactant(rId)
                rId in presentChemicalIds || norm in presentChemicalIds ||
                particles.any {
                    it.chemical.id.equals(rId, true) ||
                    it.chemical.id.equals(norm, true) ||
                    (it.chemical.symbol != null && (it.chemical.symbol.equals(rId, true) || it.chemical.symbol.equals(norm, true))) ||
                    it.chemical.formula.equals(rId, true)
                }
            }

            if (hasReactants) {
                // Find participating particles in closest proximity
                val participants = mutableListOf<ElementParticle2D>()
                reaction.reactantIds.forEach { rId ->
                    val norm = ChemicalCatalog.normalizeReactant(rId)
                    val found = particles.find {
                        it.chemical.id.equals(rId, ignoreCase = true) ||
                        it.chemical.id.equals(norm, ignoreCase = true) ||
                        (it.chemical.symbol != null && (it.chemical.symbol.equals(rId, ignoreCase = true) || it.chemical.symbol.equals(norm, ignoreCase = true))) ||
                        it.chemical.formula.equals(rId, ignoreCase = true)
                    }
                    if (found != null) participants.add(found)
                }

                val centerX = participants.map { it.x }.average().toFloat()
                val centerY = participants.map { it.y }.average().toFloat()

                // Calculate readiness based on temperature, tool, and power
                val tempSatisfied = systemTemperature >= reaction.minTemp && systemTemperature <= reaction.maxTemp
                val electricitySatisfied = !reaction.requiresElectricity || isElectricityActive
                val centrifugeSatisfied = !reaction.requiresCentrifuge || isCentrifuging
                val toolSatisfied = reaction.requiredTool == null || reaction.requiredTool == activeTool

                var score = 0f
                if (tempSatisfied) score += 0.4f
                if (electricitySatisfied) score += 0.2f
                if (centrifugeSatisfied) score += 0.2f
                if (toolSatisfied) score += 0.2f

                val isReady = tempSatisfied && electricitySatisfied && centrifugeSatisfied && toolSatisfied

                val conditionSummary = buildString {
                    if (!tempSatisfied) append("Needs ${reaction.minTemp.toInt()}°C. ")
                    if (reaction.requiresElectricity && !isElectricityActive) append("Requires DC power. ")
                    if (reaction.requiresCentrifuge && !isCentrifuging) append("Requires centrifugation. ")
                    if (reaction.requiredTool != null && reaction.requiredTool != activeTool) append("Requires ${reaction.requiredTool.title}. ")
                    if (isReady) append("Ready to react!")
                }

                potentialInteractions.add(
                    PotentialInteraction(
                        reaction = reaction,
                        participatingParticles = participants,
                        centerPosition = Offset(centerX, centerY),
                        readinessPercentage = score,
                        isActivationEnergyMet = isReady,
                        conditionSummary = conditionSummary.trim()
                    )
                )
            }
        }

        return potentialInteractions
    }

    /**
     * Executes the most ready potential interaction, converting reactant particles
     * to product particles and releasing thermodynamic reaction energy.
     */
    fun executeBestInteraction(): PotentialInteraction? {
        val ready = detectPotentialInteractions().firstOrNull { it.isActivationEnergyMet } ?: return null
        val rx = ready.reaction

        // 1. Remove reactant particles
        rx.reactantIds.forEach { rId ->
            val norm = ChemicalCatalog.normalizeReactant(rId)
            val idx = particles.indexOfFirst {
                it.chemical.id.equals(rId, ignoreCase = true) ||
                it.chemical.id.equals(norm, ignoreCase = true) ||
                (it.chemical.symbol != null && (it.chemical.symbol.equals(rId, ignoreCase = true) || it.chemical.symbol.equals(norm, ignoreCase = true))) ||
                it.chemical.formula.equals(rId, ignoreCase = true)
            }
            if (idx >= 0) {
                particles.removeAt(idx)
            }
        }

        // 2. Spawn product particles at reaction center
        val center = ready.centerPosition
        rx.productIds.forEach { pId ->
            val productChem = ChemicalCatalog.getChemical(pId)
            if (productChem != null) {
                spawnElement(productChem, center.x, center.y)
            }
        }

        // 3. Thermodynamic energy release
        systemTemperature = (systemTemperature + rx.tempChange).coerceIn(-10.0, 1500.0)
        particles.forEach { it.localizedTemp = systemTemperature }

        recalculateState()
        return ready
    }

    private fun recalculateState() {
        val count = particles.size
        val avgTemp = if (count > 0) particles.map { it.localizedTemp }.average() else systemTemperature
        val avgPh = if (count > 0) particles.map { it.chemical.ph }.average() else 7.0

        val hasPrecipitate = particles.any { it.phase == ParticlePhase.PRECIPITATE || it.chemical.physicalState.contains("Precipitate") }

        val dominantColor = particles.firstOrNull { it.chemical.id != "H2O" }?.chemical?.colorHex
            ?: 0xAA38BDF8L

        val potentialInteractions = detectPotentialInteractions()

        _engineState.value = ChemistryEngineState(
            particles = particles.toList(),
            potentialInteractions = potentialInteractions,
            totalMoles = particles.sumOf { it.mass } / 100.0,
            averageTemperature = avgTemp,
            averagePh = (avgPh * 10).roundToInt() / 10.0,
            dominantColorHex = dominantColor,
            hasPrecipitate = hasPrecipitate,
            activeReactionCount = potentialInteractions.count { it.isActivationEnergyMet }
        )
    }
}
