package com.example.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SystemRepository
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SystemViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SystemRepository(application)

    // Primary State flows
    private val _storageInfo = MutableStateFlow(repository.getStorageInfo())
    val storageInfo: StateFlow<StorageInfo> = _storageInfo.asStateFlow()

    private val _memoryInfo = MutableStateFlow(repository.getMemoryInfo())
    val memoryInfo: StateFlow<MemoryInfo> = _memoryInfo.asStateFlow()

    private val _connectionInfo = MutableStateFlow(repository.getNetworkInfo())
    val connectionInfo: StateFlow<ConnectionInfo> = _connectionInfo.asStateFlow()

    private val _largeFiles = MutableStateFlow(repository.getLargeFiles())
    val largeFiles: StateFlow<List<LargeFile>> = _largeFiles.asStateFlow()

    enum class AppSortType {
        SIZE, LAST_USED
    }

    var currentSortType by mutableStateOf(AppSortType.SIZE)
        private set

    fun setSortType(sortType: AppSortType) {
        currentSortType = sortType
        refreshAllMetrics()
    }

    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps.asStateFlow()

    private val _remoteBattery = MutableStateFlow(repository.getRemoteBattery())
    val remoteBattery: StateFlow<BatteryInfo> = _remoteBattery.asStateFlow()

    private val _displayStats = MutableStateFlow(repository.getDisplayStats())
    val displayStats: StateFlow<DisplayStats> = _displayStats.asStateFlow()

    private val _permissionAudits = MutableStateFlow(repository.getPermissionAudits())
    val permissionAudits: StateFlow<List<PermissionAudit>> = _permissionAudits.asStateFlow()

    private val _scanResult = MutableStateFlow(QuickScanResult())
    val scanResult: StateFlow<QuickScanResult> = _scanResult.asStateFlow()

    // Interactive States
    var selectedAppCount by mutableStateOf(0)
        private set

    var autoOptimizeOnBoot by mutableStateOf(true)
        private set

    var deadPixelColorIndex by mutableStateOf(-1) // -1 is idle
        private set

    var deadPixelStrobeActive by mutableStateOf(false)
        private set

    init {
        refreshAllMetrics()
    }

    fun refreshAllMetrics() {
        viewModelScope.launch {
            _storageInfo.value = repository.getStorageInfo()
            _memoryInfo.value = repository.getMemoryInfo()
            _connectionInfo.value = repository.getNetworkInfo()
            _largeFiles.value = repository.getLargeFiles()
            
            val apps = repository.getInstalledApps()
            _installedApps.value = when (currentSortType) {
                AppSortType.SIZE -> apps.sortedByDescending { it.sizeBytes }
                AppSortType.LAST_USED -> apps.sortedByDescending { it.lastUsedTimestamp }
            }
            
            _remoteBattery.value = repository.getRemoteBattery()
            _displayStats.value = repository.getDisplayStats()
            _permissionAudits.value = repository.getPermissionAudits()
        }
    }

    // Runs medical scan with progress
    fun startQuickScan() {
        viewModelScope.launch {
            repository.runQuickScan().collect { result ->
                _scanResult.value = result
                if (result.phase == ScanPhase.COMPLETED) {
                    // Update storage parameters locally to reflect the reclaimed cache space
                    val current = _storageInfo.value
                    _storageInfo.value = current.copy(
                        freeBytes = current.freeBytes + result.reclaimableBytes,
                        cacheBytes = 0,
                        ghostBytes = 0
                    )
                }
            }
        }
    }

    fun resetQuickScan() {
        _scanResult.value = QuickScanResult()
    }

    // Toggle app selection for batch uninstallation
    fun toggleAppSelection(app: AppInfo) {
        val updated = _installedApps.value.map {
            if (it.packageName == app.packageName) {
                val nextState = !it.isSelected
                // Limit to maximum 5 selections as per specifications
                if (nextState && selectedAppCount >= 5) {
                    it
                } else {
                    it.copy(isSelected = nextState)
                }
            } else {
                it
            }
        }
        _installedApps.value = updated
        selectedAppCount = updated.count { it.isSelected }
    }

    // Complete direct cache clean & deep sweeps
    fun clearCacheAndGhostFiles() {
        viewModelScope.launch {
            val current = _storageInfo.value
            _storageInfo.value = current.copy(
                freeBytes = current.freeBytes + current.cacheBytes + current.ghostBytes,
                cacheBytes = 0,
                ghostBytes = 0
            )
            refreshAllMetrics()
        }
    }

    // Background Hibernator Force Stop shortcut
    fun hibernateApp(packageName: String) {
        repository.forceStopSettings(packageName)
        // Simulate RAM relief after hibernating an app
        viewModelScope.launch {
            val currentMem = _memoryInfo.value
            _memoryInfo.value = currentMem.copy(
                usedBytes = (currentMem.usedBytes - (85 * 1024 * 1024L)).coerceAtLeast(500 * 1024 * 1024L),
                activeProcessesCount = (currentMem.activeProcessesCount - 1).coerceAtLeast(3)
            )
        }
    }

    // Batch Uninstallation Sequence: Returns lists of Intents to prompt the user to uninstall
    fun getUninstallIntentsForSelected(): List<android.content.Intent> {
        val selected = _installedApps.value.filter { it.isSelected }
        val intents = selected.map { repository.uninstallAppIntent(it.packageName) }
        
        // Deselect all and reset count after sequence begins
        _installedApps.value = _installedApps.value.map { it.copy(isSelected = false) }
        selectedAppCount = 0
        return intents
    }

    fun removeLargeFile(file: LargeFile) {
        // Simulates file deletion
        _largeFiles.value = _largeFiles.value.filter { it.filePath != file.filePath }
        val current = _storageInfo.value
        _storageInfo.value = current.copy(
            freeBytes = current.freeBytes + file.sizeBytes,
            videoBytes = if (file.fileType == "Video") (current.videoBytes - file.sizeBytes).coerceAtLeast(0) else current.videoBytes,
            apkBytes = if (file.fileType == "APK") (current.apkBytes - file.sizeBytes).coerceAtLeast(0) else current.apkBytes
        )
    }

    fun toggleAutoOptimize() {
        autoOptimizeOnBoot = !autoOptimizeOnBoot
    }

    fun setDeadPixelColor(index: Int) {
        deadPixelColorIndex = index
    }

    fun toggleDeadPixelStrobe(active: Boolean) {
        deadPixelStrobeActive = active
    }
}
