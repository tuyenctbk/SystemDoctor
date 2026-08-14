package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SettingsRemote
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.DocTab
import com.example.R
import com.example.model.RemoteInfo
import com.example.ui.theme.CardBorderNavy
import com.example.ui.theme.CriticalRed
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.HealthyGreen
import com.example.ui.theme.MedicalGlowBlue
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SurfaceNavy
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber

@Composable
fun Modifier.tvFocusable(
    shape: Shape = RoundedCornerShape(12.dp),
    glowColor: Color = CyanPrimary,
    scaleOnFocus: Float = 1.05f
): Modifier {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isFocused) scaleOnFocus else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )
    return this
        .onFocusChanged { isFocused = it.isFocused }
        .scale(scale)
        .border(
            width = if (isFocused) 2.dp else 0.dp,
            color = if (isFocused) glowColor else Color.Transparent,
            shape = shape
        )
        .focusable()
}

@Composable
fun Modifier.tvClickable(
    shape: Shape = RoundedCornerShape(12.dp),
    glowColor: Color = CyanPrimary,
    scaleOnFocus: Float = 1.05f,
    onClick: () -> Unit
): Modifier {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isFocused) scaleOnFocus else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )
    return this
        .onFocusChanged { isFocused = it.isFocused }
        .scale(scale)
        .border(
            width = if (isFocused) 2.5.dp else 0.dp,
            color = if (isFocused) glowColor else Color.Transparent,
            shape = shape
        )
        .clickable { onClick() }
}

@Composable
fun HeaderWidget(
    remoteInfo: RemoteInfo,
    onRefresh: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App title & glowing cyber medical identity
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(CyanPrimary.copy(alpha = 0.35f), MedicalGlowBlue)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .border(1.5.dp, CyanPrimary, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Healing,
                    contentDescription = null,
                    tint = CyanPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = stringResource(R.string.app_name).uppercase(),
                    color = PureWhite,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = stringResource(R.string.app_subtitle),
                    color = CyanPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Header Action: Remote Info & Refresh
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Remote battery & signal capsule
            Row(
                modifier = Modifier
                    .background(SurfaceNavy, RoundedCornerShape(12.dp))
                    .border(1.dp, CardBorderNavy, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SettingsRemote,
                    contentDescription = stringResource(R.string.remote_signal),
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = stringResource(R.string.remote_label),
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${remoteInfo.batteryPct}%",
                    color = when {
                        remoteInfo.batteryPct > 50 -> HealthyGreen
                        remoteInfo.batteryPct > 20 -> WarningAmber
                        else -> CriticalRed
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(
                            if (remoteInfo.isConnected) HealthyGreen else CriticalRed,
                            CircleShape
                        )
                )
            }

            // Refresh CTA button
            IconButton(
                onClick = onRefresh,
                modifier = Modifier
                    .background(SurfaceNavy, CircleShape)
                    .border(1.dp, CardBorderNavy, CircleShape)
                    .size(38.dp)
                    .tvFocusable(shape = CircleShape)
                    .testTag("header_refresh_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.refresh_stats),
                    tint = CyanPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun BentoCard(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconColor: Color = CyanPrimary,
    content: @Composable BoxScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .tvFocusable(shape = RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.weight(1f)) {
                    content()
                }
            }
        }
    }
}

@Composable
fun SystemDoctorBottomBar(
    currentTab: DocTab,
    onTabSelected: (DocTab) -> Unit
) {
    NavigationBar(
        containerColor = SurfaceNavy,
        tonalElevation = 0.dp,
        modifier = Modifier
            .border(BorderStroke(1.dp, CardBorderNavy))
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        NavigationBarItem(
            selected = currentTab == DocTab.DASHBOARD,
            onClick = { onTabSelected(DocTab.DASHBOARD) },
            icon = { Icon(imageVector = Icons.Default.Dashboard, contentDescription = stringResource(R.string.nav_dashboard)) },
            label = { Text(stringResource(R.string.nav_dashboard), fontSize = 9.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CyanPrimary,
                selectedTextColor = CyanPrimary,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = MedicalGlowBlue
            ),
            modifier = Modifier
                .tvFocusable(shape = RoundedCornerShape(16.dp), scaleOnFocus = 1.08f)
                .testTag("nav_dashboard")
        )
        NavigationBarItem(
            selected = currentTab == DocTab.STORAGE_HUNTER,
            onClick = { onTabSelected(DocTab.STORAGE_HUNTER) },
            icon = { Icon(imageVector = Icons.Default.Storage, contentDescription = stringResource(R.string.nav_hunter)) },
            label = { Text(stringResource(R.string.nav_hunter), fontSize = 9.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CyanPrimary,
                selectedTextColor = CyanPrimary,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = MedicalGlowBlue
            ),
            modifier = Modifier
                .tvFocusable(shape = RoundedCornerShape(16.dp), scaleOnFocus = 1.08f)
                .testTag("nav_storage")
        )
        NavigationBarItem(
            selected = currentTab == DocTab.APP_MANAGER,
            onClick = { onTabSelected(DocTab.APP_MANAGER) },
            icon = { Icon(imageVector = Icons.Default.AppRegistration, contentDescription = stringResource(R.string.nav_apps)) },
            label = { Text(stringResource(R.string.nav_apps), fontSize = 9.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CyanPrimary,
                selectedTextColor = CyanPrimary,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = MedicalGlowBlue
            ),
            modifier = Modifier
                .tvFocusable(shape = RoundedCornerShape(16.dp), scaleOnFocus = 1.08f)
                .testTag("nav_apps")
        )
        NavigationBarItem(
            selected = currentTab == DocTab.DIAGNOSTICS,
            onClick = { onTabSelected(DocTab.DIAGNOSTICS) },
            icon = { Icon(imageVector = Icons.Default.DeveloperMode, contentDescription = stringResource(R.string.nav_lab)) },
            label = { Text(stringResource(R.string.nav_lab), fontSize = 9.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CyanPrimary,
                selectedTextColor = CyanPrimary,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = MedicalGlowBlue
            ),
            modifier = Modifier
                .tvFocusable(shape = RoundedCornerShape(16.dp), scaleOnFocus = 1.08f)
                .testTag("nav_diagnostics")
        )
    }
}
