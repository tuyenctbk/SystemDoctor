package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.DisplayStats
import com.example.model.PermissionAudit
import com.example.ui.components.tvFocusable
import com.example.ui.theme.CardBorderNavy
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DeepBackground
import com.example.ui.theme.HealthyGreen
import com.example.ui.theme.MedicalGlowBlue
import com.example.ui.theme.SurfaceNavy
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import kotlinx.coroutines.delay

import androidx.compose.material.icons.filled.Wifi
import com.example.model.ConnectionInfo

@Composable
fun DiagnosticsLabScreen(
    displayStats: DisplayStats,
    connectionInfo: ConnectionInfo,
    isMeasuringNetwork: Boolean,
    permissionAudits: List<PermissionAudit>,
    autoOptimize: Boolean,
    onToggleAutoOptimize: () -> Unit,
    onRunPingTest: () -> Unit,
    onTriggerPixelTest: (Int) -> Unit,
    onTriggerStrobe: () -> Unit,
    onTriggerColorCycle: () -> Unit,
    onRevokePermission: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // HTTP Network Health Ping Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorderNavy, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceNavy)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Wifi, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.network_ping_tool_title),
                                color = CyanPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        Button(
                            onClick = onRunPingTest,
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .tvFocusable(shape = RoundedCornerShape(10.dp))
                                .testTag("run_ping_test_button"),
                            enabled = !isMeasuringNetwork
                        ) {
                            Text(
                                text = if (isMeasuringNetwork) stringResource(R.string.pinging_server) else stringResource(R.string.run_ping_test),
                                color = Color.Black,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = stringResource(R.string.network_ping_tool_desc), color = TextSecondary, fontSize = 9.sp)
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = stringResource(R.string.latency_label), color = TextSecondary, fontSize = 10.sp)
                            Text(text = "${connectionInfo.pingMs} ms", color = HealthyGreen, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = stringResource(R.string.jitter_label), color = TextSecondary, fontSize = 10.sp)
                            Text(text = "${connectionInfo.jitterMs} ms", color = CyanPrimary, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = stringResource(R.string.speed_label), color = TextSecondary, fontSize = 10.sp)
                            Text(text = "${connectionInfo.downloadSpeedMbps} Mbps", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }
        // Video Output Validation
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorderNavy, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceNavy)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.video_output_validation),
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF1E293B), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = stringResource(R.string.live_stats), color = CyanPrimary, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = stringResource(R.string.physical_resolution), color = TextSecondary, fontSize = 10.sp)
                            Text(text = "${displayStats.width} x ${displayStats.height} 4K UHD", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = stringResource(R.string.dynamic_refresh_rate), color = TextSecondary, fontSize = 10.sp)
                            Text(text = "${String.format("%.1f", displayStats.refreshRateHz)} Hz", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = stringResource(R.string.active_hdr_standard), color = TextSecondary, fontSize = 10.sp)
                            Text(text = displayStats.activeHdrFormat, color = HealthyGreen, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                        }

                        Text(
                            text = stringResource(R.string.cable_signal_excellent),
                            color = TextSecondary,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Dead Pixel & Image Retention Repair tools
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorderNavy, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceNavy)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.screen_repair_lab),
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.screen_repair_desc),
                        color = TextSecondary,
                        fontSize = 9.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Color test boxes
                    Text(text = stringResource(R.string.dead_pixel_tester), color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val colors = listOf(Color.Red, Color.Green, Color.Blue, Color.White, Color.Black)
                        val names = listOf("R", "G", "B", "W", "K")

                        colors.forEachIndexed { idx, col ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .background(col, RoundedCornerShape(8.dp))
                                    .border(1.dp, CardBorderNavy, RoundedCornerShape(8.dp))
                                    .clickable { onTriggerPixelTest(idx) }
                                    .testTag("pixel_test_$idx"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = names[idx],
                                    color = if (col == Color.White) Color.Black else Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onTriggerStrobe,
                            colors = ButtonDefaults.buttonColors(containerColor = WarningAmber),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .tvFocusable(shape = RoundedCornerShape(12.dp))
                                .testTag("strobe_repair_button")
                        ) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = stringResource(R.string.strobe_repair_action), color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }

                        Button(
                            onClick = onTriggerColorCycle,
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .tvFocusable(shape = RoundedCornerShape(12.dp))
                                .testTag("color_cycle_button")
                        ) {
                            Icon(imageVector = Icons.Default.Autorenew, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = stringResource(R.string.color_cycle_action), color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // Privacy Permission Audits
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorderNavy, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceNavy)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.privacy_permission_audits),
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.privacy_audit_desc),
                        color = TextSecondary,
                        fontSize = 9.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    permissionAudits.forEach { audit ->
                        Column(modifier = Modifier.padding(bottom = 10.dp)) {
                            Text(text = audit.friendlyName, color = CyanPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))

                            audit.grantedApps.take(4).forEach { app ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .background(DeepBackground, RoundedCornerShape(12.dp))
                                        .border(1.dp, CardBorderNavy, RoundedCornerShape(12.dp))
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(WarningAmber, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(text = app.appLabel, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            Text(text = app.packageName, color = TextSecondary, fontSize = 8.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = { onRevokePermission(app.packageName) },
                                        border = BorderStroke(1.dp, CardBorderNavy),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0x1100E5FF)),
                                        modifier = Modifier
                                            .height(26.dp)
                                            .tvFocusable(shape = RoundedCornerShape(8.dp))
                                            .testTag("revoke_${app.packageName}"),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = stringResource(R.string.revoke_action), color = CyanPrimary, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Auto Optimize Config
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceNavy, RoundedCornerShape(20.dp))
                    .border(1.dp, CardBorderNavy, RoundedCornerShape(20.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = stringResource(R.string.auto_optimize_title), color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(text = stringResource(R.string.auto_optimize_desc), color = TextSecondary, fontSize = 9.sp)
                }
                Switch(
                    checked = autoOptimize,
                    onCheckedChange = { onToggleAutoOptimize() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = CyanPrimary,
                        checkedTrackColor = MedicalGlowBlue,
                        uncheckedThumbColor = TextSecondary,
                        uncheckedTrackColor = DeepBackground
                    ),
                    modifier = Modifier.testTag("auto_optimize_switch")
                )
            }
        }
    }
}

@Composable
fun DeadPixelTestOverlay(colorIndex: Int, onDismiss: () -> Unit) {
    val colors = listOf(Color.Red, Color.Green, Color.Blue, Color.White, Color.Black)
    val chosenColor = colors.getOrElse(colorIndex) { Color.Red }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(chosenColor)
            .clickable { onDismiss() }
            .testTag("dead_pixel_overlay"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.pixel_test_overlay_text),
            color = if (chosenColor == Color.White) Color.Black else Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun StrobeRepairOverlay(onDismiss: () -> Unit) {
    val transition = rememberInfiniteTransition(label = "Strobe")
    val strobeColorAlpha by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(120, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "StrobeFlash"
    )

    val color = if (strobeColorAlpha > 0.5f) Color.White else Color.Black

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .clickable { onDismiss() }
            .testTag("strobe_overlay"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Text(text = stringResource(R.string.strobe_overlay_title), color = WarningAmber, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = stringResource(R.string.strobe_overlay_desc), color = Color.White, fontSize = 12.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun ColorCycleOverlay(onDismiss: () -> Unit) {
    val colors = listOf(Color.Red, Color.Green, Color.Blue, Color.White, Color.Black)
    var activeIdx by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            activeIdx = (activeIdx + 1) % colors.size
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors[activeIdx])
            .clickable { onDismiss() }
            .testTag("color_cycle_overlay"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Text(text = stringResource(R.string.color_cycle_title), color = CyanPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = stringResource(R.string.color_cycle_desc), color = Color.White, fontSize = 12.sp, textAlign = TextAlign.Center)
        }
    }
}
