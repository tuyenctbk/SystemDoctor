package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.OfflineBolt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SettingsRemote
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.DisplayStats
import com.example.model.MemoryInfo
import com.example.model.NetworkInfo
import com.example.model.QuickScanResult
import com.example.model.RemoteInfo
import com.example.model.ScanPhase
import com.example.model.StorageInfo
import com.example.ui.components.BentoCard
import com.example.ui.components.tvClickable
import com.example.ui.components.tvFocusable
import com.example.ui.theme.CardBorderNavy
import com.example.ui.theme.CriticalRed
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DeepBackground
import com.example.ui.theme.HealthyGreen
import com.example.ui.theme.MedicalGlowBlue
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SurfaceNavy
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber

@Composable
fun DashboardScreen(
    storageInfo: StorageInfo,
    memoryInfo: MemoryInfo,
    cpuInfo: com.example.model.CpuInfo,
    networkInfo: NetworkInfo,
    remoteInfo: RemoteInfo,
    displayStats: DisplayStats,
    scanResult: QuickScanResult,
    onStartScan: () -> Unit,
    onResetScan: () -> Unit,
    onPurgeCache: () -> Unit,
    onQuickOptimize: () -> Unit,
    onQuickScan: () -> Unit
) {
    var showGaugeHelpDialog by remember { mutableStateOf(false) }

    // Dynamic aggregated System Health Score Calculation
    val memoryUsedPct = if (memoryInfo.totalBytes > 0) (memoryInfo.usedBytes.toFloat() / memoryInfo.totalBytes * 100f) else 45f
    val memoryHealth = (100f - memoryUsedPct).coerceIn(0f, 100f)
    val cpuHealth = (100f - cpuInfo.usagePercentage).coerceIn(0f, 100f)
    val storageUsedPct = if (storageInfo.totalBytes > 0) ((storageInfo.totalBytes - storageInfo.freeBytes).toFloat() / storageInfo.totalBytes * 100f) else 35f
    val storageHealth = (100f - storageUsedPct).coerceIn(0f, 100f)
    
    // Aggregated Score: 40% memory weight, 30% cpu weight, 30% storage weight
    val aggregatedHealthScore = (memoryHealth * 0.4f + cpuHealth * 0.3f + storageHealth * 0.3f).toInt().coerceIn(0, 100)

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 75.dp), // Make space for the floating quick actions bar
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
        // TOP SECTION: REALTIME DIAGNOSTIC GAUGES
        item(span = { GridItemSpan(2) }) {
            RealtimeGaugesSection(
                storageInfo = storageInfo,
                memoryInfo = memoryInfo,
                networkInfo = networkInfo,
                onInfoClick = { showGaugeHelpDialog = !showGaugeHelpDialog }
            )
        }

        // Help explanation card for TV diagnostic gauges
        if (showGaugeHelpDialog) {
            item(span = { GridItemSpan(2) }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .tvFocusable(shape = RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                    border = BorderStroke(1.dp, CyanPrimary)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.gauge_help_title),
                                color = CyanPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "✕",
                                color = TextSecondary,
                                modifier = Modifier.clickable { showGaugeHelpDialog = false }
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.gauge_help_storage_title),
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Text(
                            text = stringResource(R.string.gauge_help_storage_desc),
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stringResource(R.string.gauge_help_memory_title),
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Text(
                            text = stringResource(R.string.gauge_help_memory_desc),
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stringResource(R.string.gauge_help_network_title),
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Text(
                            text = stringResource(R.string.gauge_help_network_desc),
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // HERO ITEM: QUICK SCAN RADAR
        item(span = { GridItemSpan(2) }) {
            BentoQuickScanHero(
                scanResult = scanResult,
                onStartScan = onStartScan,
                onResetScan = onResetScan
            )
        }

        // BENTO ITEM: REAL-TIME HEALTH INDEX
        item {
            val statusColor = when {
                aggregatedHealthScore > 80 -> HealthyGreen
                aggregatedHealthScore > 50 -> WarningAmber
                else -> CriticalRed
            }
            val statusText = when {
                aggregatedHealthScore > 80 -> stringResource(R.string.health_status_good)
                aggregatedHealthScore > 50 -> stringResource(R.string.health_status_warning)
                else -> stringResource(R.string.health_status_critical)
            }

            BentoCard(
                title = stringResource(R.string.health_score_title),
                modifier = Modifier.height(145.dp),
                icon = Icons.Default.AutoAwesome
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$aggregatedHealthScore%",
                            color = statusColor,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    statusColor.copy(alpha = 0.2f),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = statusText,
                                color = statusColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = if (aggregatedHealthScore > 80) stringResource(R.string.health_optimal) else stringResource(R.string.health_suboptimal),
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // BENTO ITEM: NETWORK SPEED & LATENCY
        item {
            BentoCard(
                title = stringResource(R.string.card_network_speed),
                modifier = Modifier.height(145.dp),
                icon = Icons.Default.Wifi
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${networkInfo.downloadSpeedMbps}",
                            color = TextPrimary,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Mbps", color = CyanPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = stringResource(R.string.stream_velocity), color = TextSecondary, fontSize = 9.sp)
                            Text(text = "${networkInfo.pingMs} ms ping", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = stringResource(R.string.packet_loss), color = TextSecondary, fontSize = 9.sp)
                            Text(text = "${networkInfo.packetLossPct}%", color = HealthyGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // BENTO ITEM: FLASH STORAGE PARTITION
        item {
            val usedGb = (storageInfo.totalBytes - storageInfo.freeBytes).toFloat() / (1024 * 1024 * 1024)
            val totalGb = storageInfo.totalBytes.toFloat() / (1024 * 1024 * 1024)
            val storageProgress = if (totalGb > 0) (usedGb / totalGb).coerceIn(0f, 1f) else 0f

            BentoCard(
                title = stringResource(R.string.card_storage_capacity),
                modifier = Modifier.height(145.dp),
                icon = Icons.Default.Storage
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = String.format("%.1f", usedGb),
                            color = TextPrimary,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = " / ${String.format("%.0f", totalGb)} GB",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }

                    LinearProgressIndicator(
                        progress = { storageProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = if (storageProgress > 0.85f) CriticalRed else CyanPrimary,
                        trackColor = CardBorderNavy
                    )

                    Text(
                        text = "${String.format("%.1f", storageInfo.freeBytes.toFloat() / (1024 * 1024 * 1024))} GB ${stringResource(R.string.storage_left)}",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // BENTO ITEM: CPU PERFORMANCE
        item {
            val cpuProgress = (cpuInfo.usagePercentage / 100f).coerceIn(0f, 1f)

            BentoCard(
                title = stringResource(R.string.card_cpu_usage),
                modifier = Modifier.height(145.dp),
                icon = Icons.Default.Memory
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${cpuInfo.usagePercentage}%",
                            color = if (cpuProgress > 0.8f) CriticalRed else CyanPrimary,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${cpuInfo.coreCount} Cores @ ${String.format("%.1f", cpuInfo.clockSpeedGhz)}GHz",
                                color = TextPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${cpuInfo.temperatureC}°C",
                                color = HealthyGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    LinearProgressIndicator(
                        progress = { cpuProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = if (cpuProgress > 0.8f) CriticalRed else CyanPrimary,
                        trackColor = CardBorderNavy
                    )

                    Text(
                        text = "Arch: ${cpuInfo.architecture}  •  Load: ${cpuInfo.loadAverage.take(12)}",
                        color = TextSecondary,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // BENTO ITEM: RAM ALLOCATION
        item {
            val memoryProgress = if (memoryInfo.totalBytes > 0)
                (memoryInfo.usedBytes.toFloat() / memoryInfo.totalBytes).coerceIn(0f, 1f)
            else 0f

            BentoCard(
                title = stringResource(R.string.card_memory_allocation),
                modifier = Modifier.height(145.dp),
                icon = Icons.Default.Memory
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${(memoryProgress * 100).toInt()}%",
                            color = if (memoryProgress > 0.8f) CriticalRed else CyanPrimary,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "${String.format("%.1f", memoryInfo.usedBytes.toFloat() / (1024 * 1024 * 1024))} / ${String.format("%.1f", memoryInfo.totalBytes.toFloat() / (1024 * 1024 * 1024))} GB",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    LinearProgressIndicator(
                        progress = { memoryProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = if (memoryProgress > 0.8f) CriticalRed else CyanPrimary,
                        trackColor = CardBorderNavy
                    )

                    Text(
                        text = "${memoryInfo.activeProcessCount} ${stringResource(R.string.active_processes)}",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // BENTO ITEM: REMOTE CONTROL BATTERY
        item {
            BentoCard(
                title = stringResource(R.string.card_remote_battery),
                modifier = Modifier.height(130.dp),
                icon = Icons.Default.SettingsRemote
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${remoteInfo.batteryPct}%",
                            color = when {
                                remoteInfo.batteryPct > 50 -> HealthyGreen
                                remoteInfo.batteryPct > 20 -> WarningAmber
                                else -> CriticalRed
                            },
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Box(
                            modifier = Modifier
                                .background(
                                    if (remoteInfo.isConnected) HealthyGreen.copy(alpha = 0.2f) else CriticalRed.copy(alpha = 0.2f),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (remoteInfo.isConnected) stringResource(R.string.online) else stringResource(R.string.offline),
                                color = if (remoteInfo.isConnected) HealthyGreen else CriticalRed,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = "${remoteInfo.modelName} • ${remoteInfo.signalStrengthDbm} dBm",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // BENTO ITEM: VIDEO OUTPUT
        item {
            BentoCard(
                title = stringResource(R.string.card_video_output),
                modifier = Modifier.height(130.dp),
                icon = Icons.Default.Tv
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${displayStats.width}x${displayStats.height}",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${stringResource(R.string.refresh_rate)}: ${String.format("%.0f", displayStats.refreshRateHz)}Hz",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                        Text(
                            text = displayStats.activeHdrFormat,
                            color = CyanPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // FAST PURGE ACTION CARD
        item(span = { GridItemSpan(2) }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .tvClickable(
                        shape = RoundedCornerShape(18.dp),
                        onClick = onPurgeCache
                    )
                    .testTag("fast_purge_card"),
                colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, CardBorderNavy)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(MedicalGlowBlue, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CleaningServices,
                                contentDescription = null,
                                tint = CyanPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = stringResource(R.string.fast_purge_title), color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = stringResource(R.string.fast_purge_desc), color = TextSecondary, fontSize = 10.sp)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .background(MedicalGlowBlue, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(text = stringResource(R.string.purge_action), color = CyanPrimary, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }

        // FLOATING QUICK ACTIONS DASHBOARD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .border(1.5.dp, CyanPrimary, RoundedCornerShape(22.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceNavy.copy(alpha = 0.96f)),
                shape = RoundedCornerShape(22.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.quick_actions_title),
                        color = CyanPrimary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        modifier = Modifier.weight(0.9f)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = onQuickOptimize,
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .tvFocusable(shape = RoundedCornerShape(10.dp))
                                .testTag("quick_optimize_button"),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Speed, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = stringResource(R.string.quick_action_optimize), color = Color.Black, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold)
                        }

                        Button(
                            onClick = onQuickScan,
                            colors = ButtonDefaults.buttonColors(containerColor = HealthyGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .tvFocusable(shape = RoundedCornerShape(10.dp))
                                .testTag("quick_scan_button"),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Healing, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = stringResource(R.string.quick_action_scan), color = Color.Black, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RealtimeGaugesSection(
    storageInfo: StorageInfo,
    memoryInfo: MemoryInfo,
    networkInfo: NetworkInfo,
    onInfoClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .tvFocusable(shape = RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, CardBorderNavy)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.gauges_title),
                        color = CyanPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(HealthyGreen, CircleShape)
                    )
                }

                IconButton(
                    onClick = onInfoClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Gauges Guide",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Gauge Triplet Display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val storageScore = storageInfo.healthScore.toFloat()
                val memoryScore = if (memoryInfo.totalBytes > 0)
                    ((1f - (memoryInfo.usedBytes.toFloat() / memoryInfo.totalBytes)) * 100f).coerceIn(0f, 100f)
                else 80f
                val networkScore = (100f - networkInfo.pingMs.coerceIn(0, 100)).coerceIn(10f, 100f)

                GaugeCard(
                    title = stringResource(R.string.storage_health),
                    score = storageScore,
                    unit = "%",
                    color = if (storageScore > 75) CyanPrimary else WarningAmber,
                    modifier = Modifier.weight(1f)
                )

                GaugeCard(
                    title = stringResource(R.string.memory_load),
                    score = (100f - memoryScore),
                    unit = "%",
                    color = if (memoryScore > 40) HealthyGreen else CriticalRed,
                    modifier = Modifier.weight(1f)
                )

                GaugeCard(
                    title = stringResource(R.string.network_quality),
                    score = networkScore,
                    unit = "%",
                    color = if (networkScore > 70) CyanPrimary else WarningAmber,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom dynamic status summary
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DeepBackground, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${stringResource(R.string.stream_recommendation_prefix)}: ${networkInfo.streamingStability}",
                    color = HealthyGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${networkInfo.pingMs}ms / ${networkInfo.jitterMs}ms jitter",
                    color = TextSecondary,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun GaugeCard(
    title: String,
    score: Float,
    unit: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val animatedScore by animateFloatAsState(
        targetValue = score,
        animationSpec = tween(1200, easing = LinearEasing),
        label = "GaugeScore"
    )

    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 6.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2
                val center = Offset(size.width / 2, size.height / 2)

                // Background gauge track
                drawArc(
                    color = CardBorderNavy,
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Active animated gauge sweep
                val sweep = (animatedScore / 100f) * 270f
                drawArc(
                    brush = Brush.sweepGradient(
                        listOf(color.copy(alpha = 0.5f), color)
                    ),
                    startAngle = 135f,
                    sweepAngle = sweep.coerceIn(0f, 270f),
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${score.toInt()}$unit",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            color = TextSecondary,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun BentoQuickScanHero(
    scanResult: QuickScanResult,
    onStartScan: () -> Unit,
    onResetScan: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "HeroGlow")
    val borderGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HeroGlowAlpha"
    )

    val scanLineProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ScanLineProgress"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(SurfaceNavy, DeepBackground)
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .drawBehind {
                val gridSpacing = 40.dp.toPx()
                val gridColor = CardBorderNavy.copy(alpha = 0.35f)

                var y = 0f
                while (y < size.height) {
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f
                    )
                    y += gridSpacing
                }

                var x = 0f
                while (x < size.width) {
                    drawLine(
                        color = gridColor,
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = 1f
                    )
                    x += gridSpacing
                }

                if (scanResult.phase != ScanPhase.IDLE && scanResult.phase != ScanPhase.COMPLETED) {
                    val lineY = size.height * scanLineProgress
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                CyanPrimary.copy(alpha = 0.25f),
                                CyanPrimary,
                                CyanPrimary.copy(alpha = 0.25f),
                                Color.Transparent
                            ),
                            startY = lineY - 10.dp.toPx(),
                            endY = lineY + 10.dp.toPx()
                        ),
                        topLeft = Offset(0f, lineY - 10.dp.toPx()),
                        size = Size(size.width, 20.dp.toPx())
                    )
                }
            }
            .border(
                1.dp,
                if (scanResult.phase != ScanPhase.IDLE && scanResult.phase != ScanPhase.COMPLETED)
                    CyanPrimary.copy(alpha = borderGlowAlpha)
                else CardBorderNavy,
                RoundedCornerShape(24.dp)
            )
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.OfflineBolt,
                contentDescription = null,
                tint = CyanPrimary,
                modifier = Modifier
                    .size(42.dp)
                    .drawBehind {
                        drawCircle(
                            color = CyanPrimary.copy(alpha = 0.2f),
                            radius = size.minDimension * 0.9f
                        )
                    }
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (scanResult.phase == ScanPhase.IDLE) {
                Text(
                    text = stringResource(R.string.ready_for_quick_scan),
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.quick_scan_desc),
                    color = TextSecondary,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onStartScan,
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(46.dp)
                        .tvFocusable(shape = RoundedCornerShape(16.dp))
                        .testTag("quick_scan_button")
                ) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.start_quick_scan),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            } else if (scanResult.phase != ScanPhase.COMPLETED) {
                Text(
                    text = when (scanResult.phase) {
                        ScanPhase.STORAGE_CHECK -> stringResource(R.string.scan_phase_storage)
                        ScanPhase.MEM_ANALYZE -> stringResource(R.string.scan_phase_memory)
                        ScanPhase.NETWORK_PING -> stringResource(R.string.scan_phase_network)
                        ScanPhase.GHOST_HUNT -> stringResource(R.string.scan_phase_ghost)
                        else -> stringResource(R.string.scan_phase_init)
                    },
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { scanResult.progress },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(6.dp)
                        .clip(CircleShape),
                    color = CyanPrimary,
                    trackColor = CardBorderNavy
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = scanResult.diagnosticSummary,
                    color = CyanPrimary,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center
                )
            } else {
                // Completed!
                Text(
                    text = stringResource(R.string.scan_report_healthy),
                    color = HealthyGreen,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.scan_report_healthy_desc),
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 14.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.scan_report_network_desc),
                    color = TextSecondary,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 14.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onResetScan,
                        border = BorderStroke(1.dp, CardBorderNavy),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .tvFocusable(shape = RoundedCornerShape(12.dp))
                    ) {
                        Text(text = stringResource(R.string.reset_report), color = TextSecondary, fontSize = 11.sp)
                    }
                    Button(
                        onClick = onStartScan,
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .tvFocusable(shape = RoundedCornerShape(12.dp))
                    ) {
                        Text(text = stringResource(R.string.re_run_scan), color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
