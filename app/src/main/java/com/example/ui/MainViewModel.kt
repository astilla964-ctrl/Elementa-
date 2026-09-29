package com.example.ui

import android.app.Application
import android.content.Context
import android.content.pm.ActivityInfo
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.db.DiscoveredCompoundEntity
import com.example.data.db.ReactionLogEntity
import com.example.data.model.Chemical
import com.example.data.model.ChemicalCatalog
import com.example.data.model.ChemicalCategory
import com.example.data.model.LabToolType
import com.example.data.model.Reaction
import com.example.data.repository.LabRepository
import com.example.engine.ChemistryEngine
import com.example.engine.model.ChemistryEngineState
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class AppScreen {
    LAB,
    TOOLS,
    COMPOUNDS,
    SETTINGS
}

enum class OrientationSetting(val title: String) {
    AUTO("Auto (Gyroscope / Sensor)"),
    PORTRAIT("Force Portrait"),
    LANDSCAPE("Force Landscape")
}

data class ReactionAlert(
    val equation: String,
    val observation: String,
    val newlyDiscoveredNames: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LabRepository

    // Active Chemicals in container (id -> amount)
    private val _activeChemicals = MutableStateFlow<Map<String, Int>>(
        mapOf("H2O" to 100) // Start with water in beaker
    )
    val activeChemicals: StateFlow<Map<String, Int>> = _activeChemicals.asStateFlow()

    val chemistryEngine = ChemistryEngine()
    val engineState: StateFlow<ChemistryEngineState> = chemistryEngine.engineState

    init {
        val db = AppDatabase.getInstance(application)
        repository = LabRepository(db.labDao())
        viewModelScope.launch {
            repository.seedInitialCompounds()
        }
        chemistryEngine.syncFromChemicalMap(_activeChemicals.value)
    }

    // Navigation
    private val _currentScreen = MutableStateFlow(AppScreen.LAB)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    // Theme & Orientation
    private val _themeMode = MutableStateFlow(AppThemeMode.DARK)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
    }

    private val _orientationSetting = MutableStateFlow(OrientationSetting.AUTO)
    val orientationSetting: StateFlow<OrientationSetting> = _orientationSetting.asStateFlow()

    fun setOrientation(setting: OrientationSetting) {
        _orientationSetting.value = setting
    }

    // Workbench State
    private val _activeTool = MutableStateFlow(LabToolType.BEAKER)
    val activeTool: StateFlow<LabToolType> = _activeTool.asStateFlow()

    fun selectTool(tool: LabToolType) {
        _activeTool.value = tool
        recalculateLiquidProperties()
    }

    // Selected Tool Detail for Tools Screen Modal
    private val _selectedToolDetail = MutableStateFlow<LabToolType?>(null)
    val selectedToolDetail: StateFlow<LabToolType?> = _selectedToolDetail.asStateFlow()

    fun openToolDetail(tool: LabToolType?) {
        _selectedToolDetail.value = tool
    }

    fun addChemical(chemicalId: String, amount: Int = 25) {
        val current = _activeChemicals.value.toMutableMap()
        current[chemicalId] = (current[chemicalId] ?: 0) + amount
        _activeChemicals.value = current
        recalculateLiquidProperties()
    }

    fun removeChemical(chemicalId: String) {
        val current = _activeChemicals.value.toMutableMap()
        current.remove(chemicalId)
        _activeChemicals.value = current
        recalculateLiquidProperties()
    }

    fun clearWorkbench() {
        _activeChemicals.value = emptyMap()
        _currentTemperature.value = 25.0
        _currentPh.value = 7.0
        _isHeating.value = false
        _isElectricityActive.value = false
        _isCentrifuging.value = false
        _hasPrecipitate.value = false
        _reactionAlert.value = null
        _liquidColorHex.value = 0xAA38BDF8
        chemistryEngine.clear()
        chemistryEngine.updateEnvironment(_activeTool.value, 25.0, false, false, false)
    }

    fun loadReactionReactants(reaction: Reaction) {
        val map = reaction.reactantIds.associateWith { 50 }
        _activeChemicals.value = map
        reaction.requiredTool?.let { _activeTool.value = it }
        if (reaction.minTemp > 30.0) {
            _currentTemperature.value = reaction.minTemp
            _isHeating.value = true
        } else {
            _currentTemperature.value = 25.0
            _isHeating.value = false
        }
        _isElectricityActive.value = reaction.requiresElectricity
        _isCentrifuging.value = reaction.requiresCentrifuge
        _reactionAlert.value = null
        recalculateLiquidProperties()
    }

    // Temperature & Pressure
    private val _currentTemperature = MutableStateFlow(25.0)
    val currentTemperature: StateFlow<Double> = _currentTemperature.asStateFlow()

    fun setTemperature(temp: Double) {
        _currentTemperature.value = temp
        if (temp > 50.0 && !_isHeating.value) {
            _isHeating.value = true
        }
        recalculateLiquidProperties()
    }

    private val _currentPressure = MutableStateFlow(1.0)
    val currentPressure: StateFlow<Double> = _currentPressure.asStateFlow()

    fun setPressure(atm: Double) {
        _currentPressure.value = atm
    }

    private val _isHeating = MutableStateFlow(false)
    val isHeating: StateFlow<Boolean> = _isHeating.asStateFlow()

    fun toggleHeating() {
        val newState = !_isHeating.value
        _isHeating.value = newState
        if (newState && _currentTemperature.value < 100.0) {
            _currentTemperature.value = 115.0
        } else if (!newState && _currentTemperature.value > 60.0) {
            _currentTemperature.value = 25.0
        }
        recalculateLiquidProperties()
    }

    private val _isElectricityActive = MutableStateFlow(false)
    val isElectricityActive: StateFlow<Boolean> = _isElectricityActive.asStateFlow()

    fun toggleElectricity() {
        _isElectricityActive.value = !_isElectricityActive.value
        recalculateLiquidProperties()
    }

    private val _isCentrifuging = MutableStateFlow(false)
    val isCentrifuging: StateFlow<Boolean> = _isCentrifuging.asStateFlow()

    fun toggleCentrifuge() {
        _isCentrifuging.value = !_isCentrifuging.value
        recalculateLiquidProperties()
    }

    private val _currentPh = MutableStateFlow(7.0)
    val currentPh: StateFlow<Double> = _currentPh.asStateFlow()

    private val _liquidColorHex = MutableStateFlow(0xAA38BDF8L)
    val liquidColorHex: StateFlow<Long> = _liquidColorHex.asStateFlow()

    private val _hasPrecipitate = MutableStateFlow(false)
    val hasPrecipitate: StateFlow<Boolean> = _hasPrecipitate.asStateFlow()

    private val _isReacting = MutableStateFlow(false)
    val isReacting: StateFlow<Boolean> = _isReacting.asStateFlow()

    private val _reactionAlert = MutableStateFlow<ReactionAlert?>(null)
    val reactionAlert: StateFlow<ReactionAlert?> = _reactionAlert.asStateFlow()

    fun dismissReactionAlert() {
        _reactionAlert.value = null
    }

    // Room DB Discovered Compounds
    val discoveredEntities: StateFlow<List<DiscoveredCompoundEntity>> =
        repository.discoveredEntities.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val reactionLogs: StateFlow<List<ReactionLogEntity>> =
        repository.reactionLogs.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // GitHub Release Check
    private val _githubReleaseStatus = MutableStateFlow("v1.0.0 (Latest)")
    val githubReleaseStatus: StateFlow<String> = _githubReleaseStatus.asStateFlow()

    private val _isCheckingRelease = MutableStateFlow(false)
    val isCheckingRelease: StateFlow<Boolean> = _isCheckingRelease.asStateFlow()

    fun checkGitHubRelease() {
        viewModelScope.launch {
            _isCheckingRelease.value = true
            val result = repository.checkLatestRelease()
            _isCheckingRelease.value = false
            result.onSuccess { tag ->
                _githubReleaseStatus.value = "Release: $tag"
            }.onFailure {
                _githubReleaseStatus.value = "v1.0.0 (Up to date)"
            }
        }
    }

    fun resetAllDiscoveries() {
        viewModelScope.launch {
            repository.resetDiscoveries()
            clearWorkbench()
        }
    }

    fun clearHistoryLogs() {
        viewModelScope.launch {
            repository.clearLogs()
        }
    }

    // Reaction Trigger
    fun triggerReaction() {
        val reactants = _activeChemicals.value.keys
        if (reactants.isEmpty()) return

        val matchingReaction = ChemicalCatalog.findMatchingReaction(
            reactantIds = reactants,
            currentTemp = _currentTemperature.value,
            isElectricityActive = _isElectricityActive.value,
            isCentrifugeActive = _isCentrifuging.value,
            activeTool = _activeTool.value
        )

        if (matchingReaction != null) {
            executeReaction(matchingReaction)
        } else {
            // No matching reaction under current conditions
            val temp = _currentTemperature.value.roundToInt()
            val hint = if (temp < 100 && ("C" in reactants || "Fe" in reactants || "S" in reactants)) {
                "Consider increasing temperature with the Bunsen Burner to overcome activation energy."
            } else if ("H2O" in reactants && !_isElectricityActive.value && _activeTool.value == LabToolType.ELECTRODES) {
                "Try enabling the electric power supply to initiate water electrolysis."
            } else {
                "Reactants remain kinetically stable. Check stoichiometric compatibility or adjust apparatus."
            }
            _reactionAlert.value = ReactionAlert(
                equation = "No Reaction Occurred",
                observation = "Temperature: ${temp}°C | pH: ${String.format("%.1f", _currentPh.value)}. $hint"
            )
        }
    }

    private fun executeReaction(rx: Reaction) {
        viewModelScope.launch {
            _isReacting.value = true

            // Temperature adjustment
            val newTemp = (_currentTemperature.value + rx.tempChange).coerceIn(-10.0, 1500.0)
            _currentTemperature.value = newTemp

            // pH adjustment
            _currentPh.value = rx.resultingPh

            // Liquid color & precipitate
            _liquidColorHex.value = rx.resultingColor
            val productsContainSolid = rx.productIds.any { id ->
                val chem = ChemicalCatalog.getChemical(id)
                chem?.physicalState?.contains("Solid") == true || chem?.physicalState?.contains("Precipitate") == true
            }
            _hasPrecipitate.value = productsContainSolid

            // Replace reactants with products in workbench
            val updated = mutableMapOf<String, Int>()
            for (prodId in rx.productIds) {
                updated[prodId] = 50
            }
            _activeChemicals.value = updated

            // Save to Room DB and check for newly discovered compounds
            val alreadyDiscovered = discoveredEntities.value.map { it.id }.toSet()
            val newlyDiscoveredIds = repository.recordReaction(
                reaction = rx,
                currentTemp = newTemp,
                currentPh = rx.resultingPh,
                alreadyDiscoveredIds = alreadyDiscovered
            )

            val newlyDiscoveredNames = newlyDiscoveredIds.mapNotNull { id ->
                ChemicalCatalog.getChemical(id)?.name
            }

            _reactionAlert.value = ReactionAlert(
                equation = rx.equation,
                observation = rx.observation,
                newlyDiscoveredNames = newlyDiscoveredNames
            )

            _isReacting.value = false
        }
    }

    fun stepSimulation(dt: Float = 0.016f) {
        chemistryEngine.step(dt)
    }

    private fun recalculateLiquidProperties() {
        chemistryEngine.updateEnvironment(
            tool = _activeTool.value,
            temperature = _currentTemperature.value,
            heating = _isHeating.value,
            electricity = _isElectricityActive.value,
            centrifuging = _isCentrifuging.value
        )
        chemistryEngine.syncFromChemicalMap(_activeChemicals.value)

        val chems = _activeChemicals.value.keys.mapNotNull { ChemicalCatalog.getChemical(it) }
        if (chems.isEmpty()) {
            _currentPh.value = 7.0
            _liquidColorHex.value = 0xAA38BDF8L
            _hasPrecipitate.value = false
            return
        }

        // Weighted pH approximation
        val avgPh = chems.map { it.ph }.average()
        _currentPh.value = (avgPh * 10).roundToInt() / 10.0

        // Dominant liquid color
        val nonWater = chems.find { it.id != "H2O" }
        _liquidColorHex.value = nonWater?.colorHex ?: 0xAA38BDF8L

        _hasPrecipitate.value = chems.any {
            it.physicalState.contains("Solid") || it.physicalState.contains("Precipitate")
        }
    }
}
