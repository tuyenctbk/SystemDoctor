package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.tvFocusable
import com.example.ui.theme.CardBorderNavy
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DeepBackground
import com.example.ui.theme.HealthyGreen
import com.example.ui.theme.MedicalGlowBlue
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SurfaceNavy
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class OnboardingPage(
    val titleRes: Int,
    val descRes: Int,
    val icon: ImageVector,
    val iconColor: Color
)

@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val pages = remember {
        listOf(
            OnboardingPage(
                titleRes = R.string.onboarding_title_welcome,
                descRes = R.string.onboarding_desc_welcome,
                icon = Icons.Default.Healing,
                iconColor = CyanPrimary
            ),
            OnboardingPage(
                titleRes = R.string.onboarding_title_bento,
                descRes = R.string.onboarding_desc_bento,
                icon = Icons.Default.AutoAwesome,
                iconColor = HealthyGreen
            ),
            OnboardingPage(
                titleRes = R.string.onboarding_title_hunter,
                descRes = R.string.onboarding_desc_hunter,
                icon = Icons.Default.Storage,
                iconColor = CyanPrimary
            ),
            OnboardingPage(
                titleRes = R.string.onboarding_title_ready,
                descRes = R.string.onboarding_desc_ready,
                icon = Icons.Default.Tv,
                iconColor = HealthyGreen
            )
        )
    }

    var currentPageIndex by remember { mutableIntStateOf(0) }
    val page = pages[currentPageIndex]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBackground)
            .padding(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxWidth()
                .background(SurfaceNavy, RoundedCornerShape(28.dp))
                .border(1.5.dp, CardBorderNavy, RoundedCornerShape(28.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedContent(
                targetState = page,
                transitionSpec = {
                    fadeIn(animationSpec = tween(350)) togetherWith fadeOut(animationSpec = tween(300))
                },
                label = "onboarding_slide"
            ) { slide ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Glowing Icon Container
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(slide.iconColor.copy(alpha = 0.3f), MedicalGlowBlue)
                                ),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .border(1.5.dp, slide.iconColor, RoundedCornerShape(24.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = slide.icon,
                            contentDescription = null,
                            tint = slide.iconColor,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = stringResource(slide.titleRes),
                        color = PureWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(slide.descRes),
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Slide Indicators
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                pages.forEachIndexed { idx, _ ->
                    Box(
                        modifier = Modifier
                            .size(if (idx == currentPageIndex) 16.dp else 8.dp, 8.dp)
                            .background(
                                if (idx == currentPageIndex) CyanPrimary else CardBorderNavy,
                                CircleShape
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Focusable Next CTA button
            Button(
                onClick = {
                    if (currentPageIndex < pages.size - 1) {
                        currentPageIndex++
                    } else {
                        onFinished()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(44.dp)
                    .tvFocusable(shape = RoundedCornerShape(12.dp))
                    .testTag("onboarding_cta_button")
            ) {
                Text(
                    text = if (currentPageIndex < pages.size - 1) {
                        stringResource(R.string.onboarding_action_next)
                    } else {
                        stringResource(R.string.onboarding_action_start)
                    },
                    color = Color.Black,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
