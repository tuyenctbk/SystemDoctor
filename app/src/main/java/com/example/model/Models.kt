package com.example.model

enum class HealthStatus {
    HEALTHY,
    WARNING,
    CRITICAL
}

data class StorageInfo(
    val totalBytes: Long,
    val freeBytes: Long,
    val videoBytes: Long,
    val apkBytes: Long,
    val cacheBytes: Long,
    val ghostBytes: Long
) {
    val usedBytes: Long get() = totalBytes - freeBytes
    val freePercentage: Float get() = if (totalBytes > 0) freeBytes.toFloat() / totalBytes else 1f
    val usedPercentage: Float get() = if (totalBytes > 0) usedBytes.toFloat() / totalBytes else 0f
    val healthScore: Int get() = ((freePercentage * 70f) + (if (ghostBytes < 1024 * 1024 * 500) 30f else 15f)).toInt().coerceIn(10, 100)
}

data class MemoryInfo(
    val totalBytes: Long,
    val usedBytes: Long,
    val activeProcessesCount: Int
) {
    val freeBytes: Long get() = totalBytes - usedBytes
    val usedPercentage: Float get() = if (totalBytes > 0) usedBytes.toFloat() / totalBytes else 0f
    val activeProcessCount: Int get() = activeProcessesCount
}

data class ConnectionInfo(
    val downloadSpeedMbps: Double,
    val pingMs: Int,
    val jitterMs: Int,
    val packetLossPercentage: Double,
    val isConnected: Boolean
) {
    val packetLossPct: Double get() = packetLossPercentage

    val streamCapability: String
        get() = when {
            !isConnected -> "Offline"
            downloadSpeedMbps >= 25.0 -> "Ready for 4K Dolby Vision"
            downloadSpeedMbps >= 15.0 -> "Sufficient for 4K UHD"
            downloadSpeedMbps >= 5.0 -> "Sufficient for 1080p FHD"
            else -> "Limited to 720p HD / SD"
        }

    val streamingSuggestion: String
        get() = when {
            !isConnected -> "Offline"
            packetLossPercentage > 1.5 || pingMs > 100 || jitterMs > 20 -> "Basic: 720p HD (High Packet Loss/Latency)"
            downloadSpeedMbps >= 25.0 && pingMs <= 30 && jitterMs <= 5 && packetLossPercentage <= 0.1 -> "Optimal: 4K Dolby Vision / HDR"
            downloadSpeedMbps >= 15.0 && pingMs <= 50 && jitterMs <= 10 && packetLossPercentage <= 0.5 -> "Excellent: 4K UHD Streaming"
            downloadSpeedMbps >= 5.0 && pingMs <= 80 && jitterMs <= 15 && packetLossPercentage <= 1.0 -> "Good: 1080p Full HD Streaming"
            else -> "Sufficient: 720p HD Streaming"
        }

    val streamingStability: String get() = streamingSuggestion
}

// Alias for compatibility if needed
typealias NetworkInfo = ConnectionInfo
typealias RemoteInfo = BatteryInfo

data class AppInfo(
    val packageName: String,
    val appLabel: String,
    val sizeBytes: Long,
    val lastUsedTimestamp: Long,
    val isSystemApp: Boolean,
    var isSelected: Boolean = false
)

data class LargeFile(
    val filePath: String,
    val fileName: String,
    val sizeBytes: Long,
    val fileType: String // "Video", "APK", "Cache", "OBB", "Other"
)

data class CpuInfo(
    val usagePercentage: Int,
    val coreCount: Int,
    val clockSpeedGhz: Double,
    val temperatureC: Double,
    val architecture: String,
    val loadAverage: String
)

data class CacheDirectoryInfo(
    val directoryPath: String,
    val directoryName: String,
    val sizeBytes: Long,
    val fileCount: Int,
    val isSystemCache: Boolean
)

data class PerformanceOptimizationResult(
    val isOptimizing: Boolean = false,
    val reclaimedRamBytes: Long = 0,
    val stoppedProcessCount: Int = 0,
    val summaryText: String = ""
)

data class BatteryInfo(
    val percentage: Int,
    val deviceName: String,
    val isConnected: Boolean
) {
    val batteryPct: Int get() = percentage
    val modelName: String get() = deviceName
    val signalStrengthDbm: Int get() = -52
}

data class DisplayStats(
    val width: Int,
    val height: Int,
    val refreshRateHz: Float,
    val activeHdrFormat: String // "Dolby Vision", "HDR10+", "HDR10", "HLG", "SDR"
)

data class PermissionAudit(
    val permissionName: String,
    val friendlyName: String,
    val grantedApps: List<AppInfo>
)

enum class ScanPhase {
    IDLE,
    STORAGE_CHECK,
    MEM_ANALYZE,
    NETWORK_PING,
    GHOST_HUNT,
    COMPLETED
}

data class QuickScanResult(
    val phase: ScanPhase = ScanPhase.IDLE,
    val progress: Float = 0f,
    val reclaimableBytes: Long = 0,
    val networkLatencyMs: Int = 0,
    val diagnosticSummary: String = ""
)
