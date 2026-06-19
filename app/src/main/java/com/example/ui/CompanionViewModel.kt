package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CompanionRepository
import com.example.data.DropZone
import com.example.data.TacticalBuild
import com.example.data.Weapon
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Screen navigation enum matching the Immersive Tab Design
enum class AppTab {
    LOBBY,
    ARMORY,      // "Shop" slot
    MAP_TACTICS, // "Gear" interactive map
    LOADOUTS,    // Custom setups
    STATS        // "Season"
}

// Custom weapon attachment types
sealed class Attachment(val name: String, val category: String) {
    class Scope(name: String, val magnification: Int) : Attachment(name, "Scope")
    class Muzzle(name: String, val recoilRed: Float, val stealthBonus: Float) : Attachment(name, "Muzzle")
    class Grip(name: String, val horizontalRed: Float, val verticalRed: Float) : Attachment(name, "Grip")
    class Magazine(name: String, val capacityBonus: Int, val reloadRed: Float) : Attachment(name, "Magazine")
}

class CompanionViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = CompanionRepository(database.tacticalBuildDao())

    // UI state streams
    private val _currentTab = MutableStateFlow(AppTab.LOBBY)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    // Map choice state: Erangel or Miramar
    private val _selectedMap = MutableStateFlow("Erangel")
    val selectedMap: StateFlow<String> = _selectedMap.asStateFlow()

    // Weapon list & search filters
    val allWeapons = repository.weapons
    private val _weaponSearchQuery = MutableStateFlow("")
    val weaponSearchQuery: StateFlow<String> = _weaponSearchQuery.asStateFlow()

    private val _selectedWeapon = MutableStateFlow(repository.weapons.first())
    val selectedWeapon: StateFlow<Weapon> = _selectedWeapon.asStateFlow()

    // Custom built attachments stats additions
    private val _selectedScope = MutableStateFlow("No Scope")
    val selectedScope: StateFlow<String> = _selectedScope.asStateFlow()

    private val _selectedMuzzle = MutableStateFlow("No Muzzle")
    val selectedMuzzle: StateFlow<String> = _selectedMuzzle.asStateFlow()

    private val _selectedGrip = MutableStateFlow("No Grip")
    val selectedGrip: StateFlow<String> = _selectedGrip.asStateFlow()

    private val _selectedMagazine = MutableStateFlow("No Magazine")
    val selectedMagazine: StateFlow<String> = _selectedMagazine.asStateFlow()

    // Saved builds Room list
    val savedBuilds = repository.savedBuilds

    // Combat simulations & Matchmaking
    private val _isMatchmaking = MutableStateFlow(false)
    val isMatchmaking: StateFlow<Boolean> = _isMatchmaking.asStateFlow()

    private val _matchmakingStatusText = MutableStateFlow("FINDING SQUAD...")
    val matchmakingStatusText: StateFlow<String> = _matchmakingStatusText.asStateFlow()

    private val _matchmakingTime = MutableStateFlow(0)
    val matchmakingTime: StateFlow<Int> = _matchmakingTime.asStateFlow()

    private var matchmakingJob: Job? = null

    // Canvas Flight Simulation plane path vectors (start relative percentage coordinates, end coordinates)
    private val _planeStartX = MutableStateFlow(10f)
    private val _planeStartY = MutableStateFlow(15f)
    private val _planeEndX = MutableStateFlow(90f)
    private val _planeEndY = MutableStateFlow(85f)
    
    val planeStartX: StateFlow<Float> = _planeStartX.asStateFlow()
    val planeStartY: StateFlow<Float> = _planeStartY.asStateFlow()
    val planeEndX: StateFlow<Float> = _planeEndX.asStateFlow()
    val planeEndY: StateFlow<Float> = _planeEndY.asStateFlow()

    // Selected map dropzone hotspot
    private val _selectedDropZone = MutableStateFlow<DropZone?>(repository.dropZones.first())
    val selectedDropZone: StateFlow<DropZone?> = _selectedDropZone.asStateFlow()

    // Season Battle Stat values
    val bpCurrency = MutableStateFlow(14250)
    val ucCurrency = MutableStateFlow(840)
    val levelProgress = MutableStateFlow(0.72f)
    val userLevel = MutableStateFlow(42)
    val killsCount = MutableStateFlow(324)
    val winsCount = MutableStateFlow(18)
    val kdRatio = MutableStateFlow(4.82f)
    val matchesPlayed = MutableStateFlow(68)

    // Load static drops of active map
    fun getDropZonesForSelectedMap(mapName: String): List<DropZone> {
        return repository.dropZones.filter { it.mapName.equals(mapName, ignoreCase = true) }
    }

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun setMap(map: String) {
        _selectedMap.value = map
        // update plane path to feel organic
        if (map == "Erangel") {
            _planeStartX.value = 10f
            _planeStartY.value = 15f
            _planeEndX.value = 90f
            _planeEndY.value = 85f
            _selectedDropZone.value = repository.dropZones.firstOrNull { it.mapName == "Erangel" }
        } else {
            _planeStartX.value = 80f
            _planeStartY.value = 10f
            _planeEndX.value = 20f
            _planeEndY.value = 90f
            _selectedDropZone.value = repository.dropZones.firstOrNull { it.mapName == "Miramar" }
        }
    }

    fun searchWeapons(query: String) {
        _weaponSearchQuery.value = query
    }

    fun selectWeapon(weapon: Weapon) {
        _selectedWeapon.value = weapon
        // Reset attachments to default
        _selectedScope.value = "No Scope"
        _selectedMuzzle.value = "No Muzzle"
        _selectedGrip.value = "No Grip"
        _selectedMagazine.value = "No Magazine"
    }

    fun selectAttachment(category: String, name: String) {
        when (category) {
            "Scope" -> _selectedScope.value = name
            "Muzzle" -> _selectedMuzzle.value = name
            "Grip" -> _selectedGrip.value = name
            "Magazine" -> _selectedMagazine.value = name
        }
    }

    fun selectDropZone(zone: DropZone) {
        _selectedDropZone.value = zone
    }

    // Dynamic stats modification based on weapon attachments combinations
    fun calculateModifiedStats(weapon: Weapon): Map<String, Float> {
        var baseDamage = weapon.damage
        var baseStability = weapon.stability
        var baseRange = weapon.range
        var baseFireRate = weapon.fireRate

        // Scopes modifier
        when (_selectedScope.value) {
            "2x Aimpoint" -> { baseRange += 8f; baseStability -= 2f }
            "4x ACOG" -> { baseRange += 18f; baseStability -= 5f }
            "6x High Velocity" -> { baseRange += 28f; baseStability -= 8f }
            "8x CQB Scope" -> { baseRange += 40f; baseStability -= 12f }
        }

        // Muzzle Recoil modifier
        when (_selectedMuzzle.value) {
            "Compensator" -> baseStability += 18f
            "Flash Hider" -> baseStability += 10f
            "Suppressor" -> { baseStability += 5f; baseRange -= 2f }
        }

        // Foregrip recoil modifier
        when (_selectedGrip.value) {
            "Vertical Foregrip" -> baseStability += 15f
            "Angled Grip" -> { baseStability += 10f; baseFireRate += 3f }
            "Half Grip" -> { baseStability += 12f; baseFireRate += 1f }
            "Light Grip" -> { baseStability += 14f }
        }

        // Magazine capacity / fast reload
        when (_selectedMagazine.value) {
            "Extended Mag" -> baseFireRate += 2f
            "Quickdraw Mag" -> baseFireRate += 5f
            "Ext. Quickdraw Mag" -> { baseFireRate += 8f; baseStability += 3f }
        }

        // Keep inside bounds 0 to 100
        return mapOf(
            "Damage" to baseDamage.coerceIn(0f, 100f),
            "FireRate" to baseFireRate.coerceIn(0f, 100f),
            "Stability" to baseStability.coerceIn(0f, 100f),
            "Range" to baseRange.coerceIn(0f, 100f)
        )
    }

    // Room Database CRUD operations
    fun saveCustomBuild(buildName: String, secondaryWeapon: String, notes: String) {
        viewModelScope.launch {
            val currentWep = _selectedWeapon.value
            val build = TacticalBuild(
                name = buildName.ifBlank { "${currentWep.name} custom tactical" },
                primaryWeapon = currentWep.name,
                primaryScope = _selectedScope.value,
                primaryMuzzle = _selectedMuzzle.value,
                primaryGrip = _selectedGrip.value,
                primaryMagazine = _selectedMagazine.value,
                secondaryWeapon = secondaryWeapon.ifBlank { "Pistol / Unarmed" },
                notes = notes
            )
            repository.saveBuild(build)
        }
    }

    fun deleteTacticalBuild(build: TacticalBuild) {
        viewModelScope.launch {
            repository.deleteBuild(build)
        }
    }

    // High Fidelity Custom Flight Path plane vectors mapping generator
    fun randomizePlanePath() {
        _planeStartX.value = (10..90).random().toFloat()
        _planeStartY.value = (10..90).random().toFloat()
        _planeEndX.value = (10..90).random().toFloat()
        _planeEndY.value = (10..90).random().toFloat()
    }

    // Immersive Interactive Matchmaking simulator Flow
    fun startSimulatedMatchmaking() {
        if (_isMatchmaking.value) {
            // Cancel matching
            matchmakingJob?.cancel()
            _isMatchmaking.value = false
            _matchmakingTime.value = 0
            return
        }

        _isMatchmaking.value = true
        _matchmakingTime.value = 0
        _matchmakingStatusText.value = "CONNECTING TO MATCH SERVICE..."

        matchmakingJob = viewModelScope.launch {
            // Step 1: Connecting
            delay(1500)
            if (!_isMatchmaking.value) return@launch
            _matchmakingStatusText.value = "FINDING MATCH PATTERNS (ASIA)..."
            _matchmakingTime.value = 2

            // Step 2: Registering players
            delay(2000)
            if (!_isMatchmaking.value) return@launch
            _matchmakingStatusText.value = "[SECURE LOBBY] PLAYERS: 42/100"
            _matchmakingTime.value = 4

            delay(1500)
            if (!_isMatchmaking.value) return@launch
            _matchmakingStatusText.value = "[SECURE LOBBY] PLAYERS: 89/100"
            _matchmakingTime.value = 6

            // Step 3: Server handshaking
            delay(1500)
            if (!_isMatchmaking.value) return@launch
            _matchmakingStatusText.value = "PREPARING SPATIAL ERANGEL INSTANCE..."
            _matchmakingTime.value = 7

            // Step 4: Ready
            delay(1500)
            if (!_isMatchmaking.value) return@launch
            _matchmakingStatusText.value = "CHICKEN DINNER DEPLOYMENT SUCCESSFUL!"
            _matchmakingTime.value = 9
            bpCurrency.value += 180 // Simulation prize

            delay(2000)
            _isMatchmaking.value = false
        }
    }
}
