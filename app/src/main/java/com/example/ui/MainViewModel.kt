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
import com.example.data.model.Compound
import com.example.data.model.ContainerHazardState
import com.example.data.model.LabToolType
import com.example.data.model.PressureMode
import com.example.data.model.Reaction
import com.example.data.model.ThermalApparatus
import com.example.data.model.ThermodynamicReading
import com.example.data.model.toCompound
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
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
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

    // Quantitative Dispensed Reagents & Stoichiometry State
    private val _dispensedChemicals = MutableStateFlow<Map<String, com.example.data.model.DispensedChemical>>(emptyMap())
    val dispensedChemicals: StateFlow<Map<String, com.example.data.model.DispensedChemical>> = _dispensedChemicals.asStateFlow()

    private val _latestStoichiometryResult = MutableStateFlow<com.example.data.model.StoichiometryResult?>(null)
    val latestStoichiometryResult: StateFlow<com.example.data.model.StoichiometryResult?> = _latestStoichiometryResult.asStateFlow()

    fun dismissStoichiometryResult() {
        _latestStoichiometryResult.value = null
    }

    fun dispenseChemical(dispensed: com.example.data.model.DispensedChemical) {
        val currentDispensed = _dispensedChemicals.value.toMutableMap()
        currentDispensed[dispensed.chemical.id] = dispensed
        _dispensedChemicals.value = currentDispensed

        val currentActive = _activeChemicals.value.toMutableMap()
        currentActive[dispensed.chemical.id] = dispensed.amountValue.toInt().coerceIn(5, 250)
        _activeChemicals.value = currentActive

        _latestStoichiometryResult.value = null
        recalculateLiquidProperties()
    }

    fun addChemical(chemicalId: String, amount: Int = 25) {
        val chem = ChemicalCatalog.getChemical(chemicalId)
        if (chem != null) {
            val appType = when {
                chem.category == com.example.data.model.ChemicalCategory.GAS -> com.example.data.model.DispenserApparatusType.GAS_SYRINGE
                chem.physicalState.contains("Liquid") || chem.physicalState.contains("Aqueous") || chem.category == com.example.data.model.ChemicalCategory.ACID || chem.category == com.example.data.model.ChemicalCategory.BASE -> com.example.data.model.DispenserApparatusType.GRADUATED_CYLINDER
                else -> com.example.data.model.DispenserApparatusType.ANALYTICAL_BALANCE
            }
            val moles = com.example.engine.stoichiometry.StoichiometryEngine.calculateMoles(
                chemical = chem,
                apparatusType = appType,
                amountValue = amount.toDouble(),
                molarity = if (appType == com.example.data.model.DispenserApparatusType.GRADUATED_CYLINDER) 1.0 else null
            )
            val dispensed = com.example.data.model.DispensedChemical(
                chemical = chem,
                apparatusType = appType,
                amountValue = amount.toDouble(),
                molarity = if (appType == com.example.data.model.DispenserApparatusType.GRADUATED_CYLINDER) 1.0 else null,
                moles = moles
            )
            dispenseChemical(dispensed)
        } else {
            val current = _activeChemicals.value.toMutableMap()
            current[chemicalId] = (current[chemicalId] ?: 0) + amount
            _activeChemicals.value = current
            recalculateLiquidProperties()
        }
    }

    fun removeChemical(chemicalId: String) {
        val current = _activeChemicals.value.toMutableMap()
        current.remove(chemicalId)
        _activeChemicals.value = current

        val currentDispensed = _dispensedChemicals.value.toMutableMap()
        currentDispensed.remove(chemicalId)
        _dispensedChemicals.value = currentDispensed

        recalculateLiquidProperties()
    }

    fun clearWorkbench() {
        _activeChemicals.value = emptyMap()
        _dispensedChemicals.value = emptyMap()
        _latestStoichiometryResult.value = null
        _currentTemperature.value = 25.0
        _targetTemperature.value = 25.0
        _thermalApparatus.value = ThermalApparatus.NONE
        _isStopperSealed.value = false
        _isPressureReliefOpen.value = false
        _vacuumPumpActive.value = false
        _compressorActive.value = false
        _currentPressure.value = 1.0
        _internalGasMoles.value = 0.0
        _containerHazardState.value = ContainerHazardState.INTACT
        _hazardMessage.value = null
        _currentPh.value = 7.0
        _isHeating.value = false
        _isElectricityActive.value = false
        _isCentrifuging.value = false
        _hasPrecipitate.value = false
        _hasUnreactedSolid.value = false
        _unreactedSolidName.value = null
        _liquidAlpha.value = 0.78f
        _reactionAlert.value = null
        _liquidColorHex.value = 0xAA38BDF8
        chemistryEngine.clear()
        chemistryEngine.updateEnvironment(_activeTool.value, 25.0, false, false, false, false)
    }

    fun loadReactionReactants(reaction: Reaction) {
        val map = reaction.reactantIds.associateWith { 50 }
        _activeChemicals.value = map

        val dispensedMap = mutableMapOf<String, com.example.data.model.DispensedChemical>()
        reaction.reactantIds.forEach { rId ->
            val chem = ChemicalCatalog.getChemical(rId)
            if (chem != null) {
                val app = when {
                    chem.category == com.example.data.model.ChemicalCategory.GAS -> com.example.data.model.DispenserApparatusType.GAS_SYRINGE
                    chem.physicalState.contains("Liquid") || chem.physicalState.contains("Aqueous") || chem.category == com.example.data.model.ChemicalCategory.ACID || chem.category == com.example.data.model.ChemicalCategory.BASE -> com.example.data.model.DispenserApparatusType.GRADUATED_CYLINDER
                    else -> com.example.data.model.DispenserApparatusType.ANALYTICAL_BALANCE
                }
                val amount = when (app) {
                    com.example.data.model.DispenserApparatusType.ANALYTICAL_BALANCE -> 10.0
                    com.example.data.model.DispenserApparatusType.GRADUATED_CYLINDER -> 50.0
                    com.example.data.model.DispenserApparatusType.GAS_SYRINGE -> 2.0
                }
                val moles = com.example.engine.stoichiometry.StoichiometryEngine.calculateMoles(
                    chemical = chem,
                    apparatusType = app,
                    amountValue = amount,
                    molarity = if (app == com.example.data.model.DispenserApparatusType.GRADUATED_CYLINDER) 1.0 else null
                )
                dispensedMap[rId] = com.example.data.model.DispensedChemical(
                    chemical = chem,
                    apparatusType = app,
                    amountValue = amount,
                    molarity = if (app == com.example.data.model.DispenserApparatusType.GRADUATED_CYLINDER) 1.0 else null,
                    moles = moles
                )
            }
        }
        _dispensedChemicals.value = dispensedMap
        _latestStoichiometryResult.value = null

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

    // Temperature & Thermodynamics State
    private val _currentTemperature = MutableStateFlow(25.0)
    val currentTemperature: StateFlow<Double> = _currentTemperature.asStateFlow()

    private val _targetTemperature = MutableStateFlow(25.0)
    val targetTemperature: StateFlow<Double> = _targetTemperature.asStateFlow()

    private val _thermalApparatus = MutableStateFlow(ThermalApparatus.NONE)
    val thermalApparatus: StateFlow<ThermalApparatus> = _thermalApparatus.asStateFlow()

    private val _currentPressure = MutableStateFlow(1.0)
    val currentPressure: StateFlow<Double> = _currentPressure.asStateFlow()

    private val _isStopperSealed = MutableStateFlow(false)
    val isStopperSealed: StateFlow<Boolean> = _isStopperSealed.asStateFlow()

    private val _isPressureReliefOpen = MutableStateFlow(false)
    val isPressureReliefOpen: StateFlow<Boolean> = _isPressureReliefOpen.asStateFlow()

    private val _vacuumPumpActive = MutableStateFlow(false)
    val vacuumPumpActive: StateFlow<Boolean> = _vacuumPumpActive.asStateFlow()

    private val _compressorActive = MutableStateFlow(false)
    val compressorActive: StateFlow<Boolean> = _compressorActive.asStateFlow()

    private val _internalGasMoles = MutableStateFlow(0.0)
    val internalGasMoles: StateFlow<Double> = _internalGasMoles.asStateFlow()

    private val _containerHazardState = MutableStateFlow(ContainerHazardState.INTACT)
    val containerHazardState: StateFlow<ContainerHazardState> = _containerHazardState.asStateFlow()

    private val _hazardMessage = MutableStateFlow<String?>(null)
    val hazardMessage: StateFlow<String?> = _hazardMessage.asStateFlow()

    private val _tempUnitCelsius = MutableStateFlow(true)
    val tempUnitCelsius: StateFlow<Boolean> = _tempUnitCelsius.asStateFlow()

    private val _pressureUnitAtm = MutableStateFlow(true)
    val pressureUnitAtm: StateFlow<Boolean> = _pressureUnitAtm.asStateFlow()

    private val _thermalHistory = MutableStateFlow<List<ThermodynamicReading>>(emptyList())
    val thermalHistory: StateFlow<List<ThermodynamicReading>> = _thermalHistory.asStateFlow()

    private var telemetryTickCounter = 0

    fun toggleTempUnit() {
        _tempUnitCelsius.value = !_tempUnitCelsius.value
    }

    fun togglePressureUnit() {
        _pressureUnitAtm.value = !_pressureUnitAtm.value
    }

    fun selectThermalApparatus(apparatus: ThermalApparatus) {
        _thermalApparatus.value = apparatus
        _targetTemperature.value = apparatus.defaultTargetTemp
        _isHeating.value = apparatus == ThermalApparatus.BUNSEN_BURNER || apparatus == ThermalApparatus.HOT_PLATE
        recalculateLiquidProperties()
    }

    fun setTargetTemperature(temp: Double) {
        val clamped = temp.coerceIn(-196.0, 1500.0)
        _targetTemperature.value = clamped
        if (clamped > 50.0 && _thermalApparatus.value == ThermalApparatus.NONE) {
            _thermalApparatus.value = if (clamped > 550.0) ThermalApparatus.BUNSEN_BURNER else ThermalApparatus.HOT_PLATE
            _isHeating.value = true
        } else if (clamped < 0.0 && _thermalApparatus.value == ThermalApparatus.NONE) {
            _thermalApparatus.value = if (clamped < -78.5) ThermalApparatus.LIQUID_NITROGEN_BATH else ThermalApparatus.ICE_BATH
            _isHeating.value = false
        }
    }

    fun setTemperature(temp: Double) {
        val clamped = temp.coerceIn(-196.0, 1500.0)
        _currentTemperature.value = clamped
        _targetTemperature.value = clamped
        if (clamped > 50.0 && !_isHeating.value) {
            _isHeating.value = true
        }
        recalculateLiquidProperties()
    }

    fun setPressure(atm: Double) {
        _currentPressure.value = atm.coerceIn(0.0, 100.0)
    }

    fun toggleStopperSealed() {
        val nextSealed = !_isStopperSealed.value
        _isStopperSealed.value = nextSealed
        if (!nextSealed) {
            _isPressureReliefOpen.value = false
            _vacuumPumpActive.value = false
            _compressorActive.value = false
            _currentPressure.value = 1.0
            _internalGasMoles.value = 0.0
        }
        recalculateLiquidProperties()
    }

    fun togglePressureRelief() {
        _isPressureReliefOpen.value = !_isPressureReliefOpen.value
    }

    fun toggleVacuumPump() {
        if (!_isStopperSealed.value) _isStopperSealed.value = true
        _vacuumPumpActive.value = !_vacuumPumpActive.value
        if (_vacuumPumpActive.value) {
            _compressorActive.value = false
        }
    }

    fun toggleCompressor() {
        if (!_isStopperSealed.value) _isStopperSealed.value = true
        _compressorActive.value = !_compressorActive.value
        if (_compressorActive.value) {
            _vacuumPumpActive.value = false
        }
    }

    fun replaceGlassware() {
        _containerHazardState.value = ContainerHazardState.INTACT
        _hazardMessage.value = null
        _currentPressure.value = 1.0
        _internalGasMoles.value = 0.0
        _isStopperSealed.value = false
        _isPressureReliefOpen.value = false
        _vacuumPumpActive.value = false
        _compressorActive.value = false
        _currentTemperature.value = 25.0
        _targetTemperature.value = 25.0
        _thermalApparatus.value = ThermalApparatus.NONE
        _isHeating.value = false
        clearWorkbench()
    }

    private val _isHeating = MutableStateFlow(false)
    val isHeating: StateFlow<Boolean> = _isHeating.asStateFlow()

    fun toggleHeating() {
        val newState = !_isHeating.value
        _isHeating.value = newState
        if (newState) {
            _thermalApparatus.value = ThermalApparatus.BUNSEN_BURNER
            _targetTemperature.value = 350.0
            if (_currentTemperature.value < 100.0) {
                _currentTemperature.value = 115.0
            }
        } else {
            _thermalApparatus.value = ThermalApparatus.NONE
            _targetTemperature.value = 25.0
            if (_currentTemperature.value > 60.0) {
                _currentTemperature.value = 25.0
            }
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

    private val _hasUnreactedSolid = MutableStateFlow(false)
    val hasUnreactedSolid: StateFlow<Boolean> = _hasUnreactedSolid.asStateFlow()

    private val _unreactedSolidColorHex = MutableStateFlow(0xFF94A3B8L)
    val unreactedSolidColorHex: StateFlow<Long> = _unreactedSolidColorHex.asStateFlow()

    private val _unreactedSolidName = MutableStateFlow<String?>(null)
    val unreactedSolidName: StateFlow<String?> = _unreactedSolidName.asStateFlow()

    private val _liquidAlpha = MutableStateFlow(0.78f)
    val liquidAlpha: StateFlow<Float> = _liquidAlpha.asStateFlow()

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

    val compounds: StateFlow<List<Compound>> =
        repository.compounds.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ChemicalCatalog.ALL_CHEMICALS.map { it.toCompound(discovered = it.isPreUnlocked) }
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
            // Check if reaction exists but requires higher thermal activation energy
            val potentialRx = ChemicalCatalog.REACTIONS.find { rx ->
                rx.reactantIds.all { rId ->
                    val norm = ChemicalCatalog.normalizeReactant(rId)
                    rId in reactants || norm in reactants ||
                    reactants.any { it.equals(rId, true) || it.equals(norm, true) }
                }
            }

            if (potentialRx != null && _currentTemperature.value < potentialRx.minTemp) {
                val cur = _currentTemperature.value.roundToInt()
                val req = potentialRx.minTemp.roundToInt()
                val pct = ((_currentTemperature.value / potentialRx.minTemp) * 100).toInt().coerceIn(0, 99)
                _reactionAlert.value = ReactionAlert(
                    equation = "${potentialRx.equation} (Activation Energy Required)",
                    observation = "Heat Required: Reached ${cur}°C / Target ${req}°C ($pct% Activation Energy). Thermal activation threshold not met. Use the Bunsen Burner or Hot Plate to heat the vessel to at least ${req}°C."
                )
                return
            }

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

            // 1. Calculate Real-Time Stoichiometry (Limiting & Excess Reagents)
            val stoich = com.example.engine.stoichiometry.StoichiometryEngine.calculateStoichiometry(
                reaction = rx,
                initialDispensed = _dispensedChemicals.value
            )
            _latestStoichiometryResult.value = stoich

            // 2. Thermodynamic temperature adjustment (Exothermic heat spike or Endothermic chill)
            val newTemp = (_currentTemperature.value + rx.tempChange).coerceIn(-196.0, 1500.0)
            _currentTemperature.value = newTemp
            _targetTemperature.value = newTemp

            // 3. Container Contents: Retain unreacted excess + add synthesized products
            val updatedDispensed = mutableMapOf<String, com.example.data.model.DispensedChemical>()
            val updatedActive = mutableMapOf<String, Int>()

            // Keep leftover unreacted excess reagents
            stoich.remainingQuantities.forEach { (rId, rem) ->
                if (rem.moles > 0.0001) {
                    updatedDispensed[rId] = rem
                    updatedActive[rId] = rem.amountValue.toInt().coerceIn(5, 250)
                }
            }

            // Add formed products
            stoich.productYields.forEach { (pId, yield) ->
                val chem = yield.chemical
                val appType = when {
                    chem.category == com.example.data.model.ChemicalCategory.GAS -> com.example.data.model.DispenserApparatusType.GAS_SYRINGE
                    chem.physicalState.contains("Liquid") || chem.physicalState.contains("Aqueous") || chem.category == com.example.data.model.ChemicalCategory.ACID || chem.category == com.example.data.model.ChemicalCategory.BASE -> com.example.data.model.DispenserApparatusType.GRADUATED_CYLINDER
                    else -> com.example.data.model.DispenserApparatusType.ANALYTICAL_BALANCE
                }
                val amount = when (appType) {
                    com.example.data.model.DispenserApparatusType.ANALYTICAL_BALANCE -> yield.massGrams
                    com.example.data.model.DispenserApparatusType.GRADUATED_CYLINDER -> yield.volumeMl ?: yield.massGrams
                    com.example.data.model.DispenserApparatusType.GAS_SYRINGE -> yield.molesProduced * 22.414
                }
                updatedDispensed[pId] = com.example.data.model.DispensedChemical(
                    chemical = chem,
                    apparatusType = appType,
                    amountValue = amount,
                    molarity = if (appType == com.example.data.model.DispenserApparatusType.GRADUATED_CYLINDER) (yield.molesProduced / (amount / 1000.0).coerceAtLeast(0.01)) else null,
                    moles = yield.molesProduced
                )
                updatedActive[pId] = amount.toInt().coerceIn(10, 250)
            }

            // If sealed with stopper, gaseous product moles increase internal pressure (PV = nRT)
            val gasMolesEvolved = stoich.productYields.values.filter {
                it.chemical.category == ChemicalCategory.GAS || it.chemical.physicalState.contains("Gas", true)
            }.sumOf { it.molesProduced }
            if (gasMolesEvolved > 0.0 && _isStopperSealed.value) {
                _internalGasMoles.value += gasMolesEvolved
            }

            _dispensedChemicals.value = updatedDispensed
            _activeChemicals.value = updatedActive

            // 4. Stoichiometric Solution Blending (Color, pH, Transparency, Precipitate & Excess Solids)
            val blended = com.example.engine.stoichiometry.StoichiometryEngine.calculateBlendedSolution(rx, stoich)
            _hasPrecipitate.value = blended.hasSettledPrecipitate
            _hasUnreactedSolid.value = blended.hasUnreactedSolid
            _unreactedSolidColorHex.value = blended.unreactedSolidColorHex
            _unreactedSolidName.value = blended.unreactedSolidName
            _liquidAlpha.value = blended.transparencyAlpha
            _currentPh.value = blended.blendedPh
            _liquidColorHex.value = blended.blendedColorHex

            // 5. Save to Room DB and check for newly discovered compounds
            val alreadyDiscovered = discoveredEntities.value.map { it.id }.toSet()
            val newlyDiscoveredIds = repository.recordReaction(
                reaction = rx,
                currentTemp = newTemp,
                currentPh = blended.blendedPh,
                alreadyDiscoveredIds = alreadyDiscovered
            )

            val newlyDiscoveredNames = newlyDiscoveredIds.mapNotNull { id ->
                ChemicalCatalog.getChemical(id)?.name
            }

            // Notification alert with thermodynamic details and limiting reagent clarity
            val limitingChem = ChemicalCatalog.getChemical(stoich.limitingReagentId)
            val thermoTag = if (rx.isExothermic) "Exothermic (ΔH < 0, +${rx.tempChange.toInt()}°C)" else "Endothermic (ΔH > 0, -${kotlin.math.abs(rx.tempChange).toInt()}°C)"
            val alertObservation = if (stoich.isExactStoichiometricRatio) {
                "${rx.observation} [$thermoTag] (Stoichiometrically Balanced: 100% of reactants consumed)."
            } else {
                "${rx.observation} [$thermoTag] [Limiting Reagent: ${limitingChem?.formula ?: stoich.limitingReagentId}]. ${stoich.unreactedExcessDescriptions.joinToString("; ")}"
            }

            _reactionAlert.value = ReactionAlert(
                equation = rx.equation,
                observation = alertObservation,
                newlyDiscoveredNames = newlyDiscoveredNames
            )

            // Cease gas bubbling animations immediately when limiting reagent is exhausted
            _isReacting.value = false
            recalculateLiquidProperties(skipColorOverride = true)
        }
    }

    fun stepSimulation(dt: Float = 0.016f) {
        val currentT = _currentTemperature.value
        val targetT = _targetTemperature.value
        val app = _thermalApparatus.value

        // 1. Dynamic Heat Transfer based on Thermal Apparatus
        val heatRate = when (app) {
            ThermalApparatus.BUNSEN_BURNER -> 0.12 // Fast aggressive roaring flame
            ThermalApparatus.HOT_PLATE -> 0.05 // Controlled electric surface heating
            ThermalApparatus.ICE_BATH -> 0.06 // Ice slurry cooling
            ThermalApparatus.DRY_ICE_BATH -> 0.08 // Deep dry-ice bath
            ThermalApparatus.LIQUID_NITROGEN_BATH -> 0.14 // Rapid cryogenic quenching
            ThermalApparatus.NONE -> 0.008 // Passive ambient heat loss towards 25°C
        }
        val targetEnvelope = if (app == ThermalApparatus.NONE) 25.0 else targetT
        val deltaT = (targetEnvelope - currentT) * heatRate * (dt * 15f)
        val nextT = (currentT + deltaT).coerceIn(-196.0, 1500.0)
        _currentTemperature.value = nextT

        // 2. Dynamic Pressure Simulation (PV = nRT & Vapor Pressure)
        if (!_isStopperSealed.value) {
            _currentPressure.value = 1.0
            _internalGasMoles.value = 0.0
        } else {
            val kelvin = max(1.0, nextT + 273.15)
            val thermalP = kelvin / 298.15 // Thermal expansion of trapped air

            val liquidsPresent = _activeChemicals.value.keys.any { id ->
                val chem = ChemicalCatalog.getChemical(id)
                chem != null && (chem.physicalState.contains("Liquid", true) || chem.physicalState.contains("Aqueous", true) || id == "H2O")
            }
            // Liquid boiling vapor pressure:
            val boilingP = if (liquidsPresent && nextT >= 95.0) {
                val excess = (nextT - 95.0) / 20.0
                excess.pow(1.9) * 0.45
            } else 0.0

            val gasP = _internalGasMoles.value * 2.8 * (kelvin / 298.15)

            val pumpMod = when {
                _vacuumPumpActive.value -> -0.92 // Drawing near-vacuum (~0.08 atm)
                _compressorActive.value -> 4.5 // Compressing gas
                else -> 0.0
            }

            var calculatedP = (thermalP + boilingP + gasP + pumpMod).coerceIn(0.04, 100.0)

            if (_isPressureReliefOpen.value) {
                // Pressure relief valve rapidly vents pressure down towards atmospheric
                calculatedP = max(1.0, calculatedP - (dt * 4.0))
                _internalGasMoles.value = max(0.0, _internalGasMoles.value - (dt * 0.2))
            }

            _currentPressure.value = calculatedP
        }

        // 3. Container Safety & Hazard Mechanics
        val tool = _activeTool.value
        val safeT = tool.maxSafeTempC
        val crackT = tool.thermalCrackTempC
        val safeP = tool.maxSafePressureAtm
        val burstP = tool.rupturePressureAtm

        if (_containerHazardState.value != ContainerHazardState.RUPTURED_EXPLODED) {
            when {
                _currentPressure.value >= burstP -> {
                    // Container Explosion / Rupture
                    triggerContainerExplosion(
                        "💥 CATASTROPHIC GLASS EXPLOSION! Internal pressure reached ${String.format("%.1f", _currentPressure.value)} atm, exceeding ${tool.title} burst limit (${String.format("%.1f", burstP)} atm). Glass shattered and contents spilled!"
                    )
                }
                nextT >= crackT -> {
                    if (_containerHazardState.value != ContainerHazardState.CRACKED) {
                        _containerHazardState.value = ContainerHazardState.CRACKED
                        _hazardMessage.value = "⚠️ THERMAL FRACTURE: ${tool.title} heated to ${nextT.roundToInt()}°C! Glass walls have cracked under thermal stress."
                    }
                }
                _currentPressure.value > safeP -> {
                    _containerHazardState.value = ContainerHazardState.PRESSURE_WARNING
                    _hazardMessage.value = "⚡ HIGH PRESSURE WARNING: ${String.format("%.1f", _currentPressure.value)} atm exceeds safe limit (${String.format("%.1f", safeP)} atm). Open relief valve!"
                }
                nextT > safeT -> {
                    _containerHazardState.value = ContainerHazardState.THERMAL_STRESS_WARNING
                    _hazardMessage.value = "🔥 THERMAL STRESS: ${tool.title} heated to ${nextT.roundToInt()}°C (Safe limit: ${safeT.toInt()}°C)."
                }
                else -> {
                    if (_containerHazardState.value != ContainerHazardState.CRACKED) {
                        _containerHazardState.value = ContainerHazardState.INTACT
                        _hazardMessage.value = null
                    }
                }
            }
        }

        // 4. Record Live Thermodynamic Curves Telemetry Buffer
        telemetryTickCounter++
        if (telemetryTickCounter % 15 == 0) {
            val list = _thermalHistory.value.toMutableList()
            if (list.size >= 60) list.removeAt(0)
            list.add(
                ThermodynamicReading(
                    timestampMs = System.currentTimeMillis(),
                    temperatureCelsius = nextT,
                    pressureAtm = _currentPressure.value,
                    hazardState = _containerHazardState.value
                )
            )
            _thermalHistory.value = list
        }

        // 5. Advance 2D Physics Step
        chemistryEngine.updateEnvironment(
            tool = _activeTool.value,
            temperature = nextT,
            heating = _isHeating.value,
            electricity = _isElectricityActive.value,
            centrifuging = _isCentrifuging.value,
            stopperSealed = _isStopperSealed.value
        )
        chemistryEngine.step(dt)
    }

    fun triggerContainerExplosion(reason: String) {
        _containerHazardState.value = ContainerHazardState.RUPTURED_EXPLODED
        _hazardMessage.value = reason
        _isStopperSealed.value = false
        _currentPressure.value = 1.0
        _internalGasMoles.value = 0.0
        _activeChemicals.value = emptyMap()
        _dispensedChemicals.value = emptyMap()
        _latestStoichiometryResult.value = null
        _hasPrecipitate.value = false
        _hasUnreactedSolid.value = false
        chemistryEngine.clear()
    }

    private fun recalculateLiquidProperties(skipColorOverride: Boolean = false) {
        chemistryEngine.updateEnvironment(
            tool = _activeTool.value,
            temperature = _currentTemperature.value,
            heating = _isHeating.value,
            electricity = _isElectricityActive.value,
            centrifuging = _isCentrifuging.value,
            stopperSealed = _isStopperSealed.value
        )
        chemistryEngine.syncFromChemicalMap(_activeChemicals.value)

        val chems = _activeChemicals.value.keys.mapNotNull { ChemicalCatalog.getChemical(it) }
        if (chems.isEmpty()) {
            _currentPh.value = 7.0
            _liquidColorHex.value = 0xAA38BDF8L
            _hasPrecipitate.value = false
            _hasUnreactedSolid.value = false
            _unreactedSolidName.value = null
            _liquidAlpha.value = 0.78f
            return
        }

        if (!skipColorOverride) {
            val dispensedMap = _dispensedChemicals.value
            val hasDispensed = dispensedMap.isNotEmpty()

            val totalMoles = if (hasDispensed) dispensedMap.values.sumOf { it.moles } else chems.size.toDouble()
            val weightedPh = if (hasDispensed && totalMoles > 0.0) {
                dispensedMap.values.sumOf { it.moles * it.chemical.ph } / totalMoles
            } else {
                chems.map { it.ph }.average()
            }
            _currentPh.value = ((weightedPh * 10).roundToInt() / 10.0).coerceIn(0.0, 14.0)

            val hasSolid = if (hasDispensed) {
                dispensedMap.values.any {
                    it.chemical.physicalState.contains("Solid", ignoreCase = true) ||
                    it.chemical.physicalState.contains("Precipitate", ignoreCase = true) ||
                    it.apparatusType == com.example.data.model.DispenserApparatusType.ANALYTICAL_BALANCE ||
                    it.chemical.category == com.example.data.model.ChemicalCategory.SALT ||
                    it.chemical.elementSeries?.contains("Metal") == true
                }
            } else {
                chems.any { it.physicalState.contains("Solid") || it.physicalState.contains("Precipitate") }
            }
            _hasPrecipitate.value = hasSolid
            _hasUnreactedSolid.value = hasSolid

            val nonWaterDispensed = dispensedMap.values.filter { it.chemical.id != "H2O" }
            if (nonWaterDispensed.isNotEmpty()) {
                var wr = 0.0
                var wg = 0.0
                var wb = 0.0
                var wMoles = 0.0
                nonWaterDispensed.forEach { d ->
                    val c = d.chemical.colorHex
                    val m = d.moles
                    wr += ((c shr 16) and 0xFF) * m
                    wg += ((c shr 8) and 0xFF) * m
                    wb += (c and 0xFF) * m
                    wMoles += m
                }
                if (wMoles > 0.0) {
                    val fr = (wr / wMoles).toInt().coerceIn(0, 255)
                    val fg = (wg / wMoles).toInt().coerceIn(0, 255)
                    val fb = (wb / wMoles).toInt().coerceIn(0, 255)
                    _liquidColorHex.value = (0xFFL shl 24) or (fr.toLong() shl 16) or (fg.toLong() shl 8) or fb.toLong()
                } else {
                    _liquidColorHex.value = nonWaterDispensed.first().chemical.colorHex
                }
            } else {
                val nonWater = chems.find { it.id != "H2O" }
                _liquidColorHex.value = nonWater?.colorHex ?: 0xAA38BDF8L
            }
            _liquidAlpha.value = if (hasSolid) 0.92f else 0.76f
        }
    }
}
