package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.data.OptimizationWorker
import java.util.concurrent.TimeUnit
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.HeaderWidget
import com.example.ui.components.SystemDoctorBottomBar
import com.example.ui.screens.AppManagerScreen
import com.example.ui.screens.ColorCycleOverlay
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DeadPixelTestOverlay
import com.example.ui.screens.DiagnosticsLabScreen
import com.example.ui.screens.StorageHunterScreen
import com.example.ui.screens.StrobeRepairOverlay
import com.example.ui.screens.OnboardingScreen
import com.example.ui.theme.DeepBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.isAppDarkMode
import android.content.Context
import com.example.viewmodel.SystemViewModel

enum class DocTab {
    DASHBOARD,
    STORAGE_HUNTER,
    APP_MANAGER,
    DIAGNOSTICS
}

class MainActivity : ComponentActivity() {
    private val viewModel: SystemViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        try {
            val workRequest = PeriodicWorkRequestBuilder<OptimizationWorker>(
                1, TimeUnit.HOURS
            ).build()
            WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
                "system_doctor_periodic_opt",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        setContent {
            MyApplicationTheme {
                SystemDoctorApp(viewModel)
            }
        }
    }
}

@Composable
fun SystemDoctorApp(viewModel: SystemViewModel) {
    val context = LocalContext.current
    var onboardingCompleted by remember {
        mutableStateOf(
            context.getSharedPreferences("system_doctor_prefs", Context.MODE_PRIVATE)
                .getBoolean("onboarding_completed", false)
        )
    }

    if (!onboardingCompleted) {
        OnboardingScreen(
            onFinished = {
                onboardingCompleted = true
                context.getSharedPreferences("system_doctor_prefs", Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean("onboarding_completed", true)
                    .apply()
            }
        )
        return
    }

    var currentTab by remember { mutableStateOf(DocTab.DASHBOARD) }

    // State flows from ViewModel
    val storageInfo by viewModel.storageInfo.collectAsStateWithLifecycle()
    val memoryInfo by viewModel.memoryInfo.collectAsStateWithLifecycle()
    val cpuInfo by viewModel.cpuInfo.collectAsStateWithLifecycle()
    val connectionInfo by viewModel.connectionInfo.collectAsStateWithLifecycle()
    val cacheDirectories by viewModel.cacheDirectories.collectAsStateWithLifecycle()
    val optimizationResult by viewModel.optimizationResult.collectAsStateWithLifecycle()
    val isMeasuringNetwork by viewModel.isMeasuringNetwork.collectAsStateWithLifecycle()
    val largeFiles by viewModel.largeFiles.collectAsStateWithLifecycle()
    val installedApps by viewModel.installedApps.collectAsStateWithLifecycle()
    val remoteBattery by viewModel.remoteBattery.collectAsStateWithLifecycle()
    val displayStats by viewModel.displayStats.collectAsStateWithLifecycle()
    val permissionAudits by viewModel.permissionAudits.collectAsStateWithLifecycle()
    val scanResult by viewModel.scanResult.collectAsStateWithLifecycle()

    // Activity launcher for app uninstalls
    val uninstallLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.refreshAllMetrics()
    }

    // Fullscreen diagnostic overlays
    var activePixelTestIndex by remember { mutableIntStateOf(-1) }
    var activeStrobeOverlay by remember { mutableStateOf(false) }
    var activeColorCycle by remember { mutableStateOf(false) }

    if (activeColorCycle) {
        ColorCycleOverlay(onDismiss = { activeColorCycle = false })
        return
    }

    if (activePixelTestIndex >= 0) {
        DeadPixelTestOverlay(
            colorIndex = activePixelTestIndex,
            onDismiss = { activePixelTestIndex = -1 }
        )
        return
    }

    if (activeStrobeOverlay) {
        StrobeRepairOverlay(onDismiss = { activeStrobeOverlay = false })
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DeepBackground,
        bottomBar = {
            SystemDoctorBottomBar(
                currentTab = currentTab,
                onTabSelected = { currentTab = it }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DeepBackground)
        ) {
            // Header with remote info & refresh action
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                HeaderWidget(
                    remoteInfo = remoteBattery,
                    onRefresh = { viewModel.refreshAllMetrics() }
                )
            }

            // Tab Content with smooth animated transitions
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(150))
                    },
                    label = "TabContentTransition"
                ) { targetTab ->
                    when (targetTab) {
                        DocTab.DASHBOARD -> {
                            DashboardScreen(
                                storageInfo = storageInfo,
                                memoryInfo = memoryInfo,
                                cpuInfo = cpuInfo,
                                networkInfo = connectionInfo,
                                remoteInfo = remoteBattery,
                                displayStats = displayStats,
                                scanResult = scanResult,
                                onStartScan = { viewModel.startQuickScan() },
                                onResetScan = { viewModel.resetQuickScan() },
                                onPurgeCache = {
                                    viewModel.clearCacheAndGhostFiles()
                                    Toast.makeText(context, context.getString(R.string.toast_cache_cleaned), Toast.LENGTH_SHORT).show()
                                },
                                onQuickOptimize = {
                                    viewModel.runPerformanceOptimizer()
                                    Toast.makeText(context, context.getString(R.string.toast_memory_reclaimed), Toast.LENGTH_SHORT).show()
                                },
                                onQuickScan = {
                                    viewModel.startQuickScan()
                                }
                            )
                        }
                        DocTab.STORAGE_HUNTER -> {
                            StorageHunterScreen(
                                storageInfo = storageInfo,
                                cacheDirectories = cacheDirectories,
                                largeFiles = largeFiles,
                                onPurgeCache = {
                                    viewModel.clearCacheAndGhostFiles()
                                    Toast.makeText(context, context.getString(R.string.toast_ghost_cleared), Toast.LENGTH_SHORT).show()
                                },
                                onClearDirectory = { dir ->
                                    viewModel.clearCacheDirectory(dir)
                                    Toast.makeText(context, context.getString(R.string.toast_dir_cleared), Toast.LENGTH_SHORT).show()
                                },
                                onDeleteFile = { file ->
                                    viewModel.removeLargeFile(file)
                                    Toast.makeText(context, "${file.fileName} ${context.getString(R.string.toast_file_purged)}", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                        DocTab.APP_MANAGER -> {
                            AppManagerScreen(
                                installedApps = installedApps,
                                selectedCount = viewModel.selectedAppCount,
                                memoryInfo = memoryInfo,
                                optimizationResult = optimizationResult,
                                currentSortType = viewModel.currentSortType,
                                onSortTypeChange = { viewModel.setSortType(it) },
                                onOptimizeMemory = {
                                    viewModel.runPerformanceOptimizer()
                                    Toast.makeText(context, context.getString(R.string.toast_memory_reclaimed), Toast.LENGTH_SHORT).show()
                                },
                                onToggleApp = { viewModel.toggleAppSelection(it) },
                                onHibernate = { app ->
                                    viewModel.hibernateApp(app.packageName)
                                    Toast.makeText(context, context.getString(R.string.toast_hibernated), Toast.LENGTH_SHORT).show()
                                },
                                onUninstallBatch = {
                                    val intents = viewModel.getUninstallIntentsForSelected()
                                    if (intents.isEmpty()) {
                                        Toast.makeText(context, context.getString(R.string.toast_select_app_limit), Toast.LENGTH_SHORT).show()
                                    } else {
                                        intents.forEach { intent ->
                                            try {
                                                uninstallLauncher.launch(intent)
                                            } catch (e: Exception) {
                                                // Safe fallback
                                            }
                                        }
                                    }
                                }
                            )
                        }
                        DocTab.DIAGNOSTICS -> {
                            DiagnosticsLabScreen(
                                displayStats = displayStats,
                                connectionInfo = connectionInfo,
                                isMeasuringNetwork = isMeasuringNetwork,
                                permissionAudits = permissionAudits,
                                autoOptimize = viewModel.autoOptimizeOnBoot,
                                isDarkMode = isAppDarkMode,
                                onToggleAutoOptimize = { viewModel.toggleAutoOptimize() },
                                onToggleDarkMode = { isAppDarkMode = !isAppDarkMode },
                                onRunPingTest = { viewModel.runNetworkPingTest() },
                                onTriggerPixelTest = { colorIdx -> activePixelTestIndex = colorIdx },
                                onTriggerStrobe = { activeStrobeOverlay = true },
                                onTriggerColorCycle = { activeColorCycle = true },
                                onRevokePermission = { pkg ->
                                    viewModel.hibernateApp(pkg)
                                    Toast.makeText(context, context.getString(R.string.toast_revoke_permission), Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
