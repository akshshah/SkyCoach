package com.example.skycoach.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skycoach.R
import com.example.skycoach.data.DestMadrid
import com.example.skycoach.ui.components.TimeToDestination
import kotlinx.coroutines.delay

@Composable
fun GeneratingScreen(
    onComplete: () -> Unit
) {
    var statusIndex by remember { mutableIntStateOf(0) }
    val statuses = listOf(
        "Analyzing Madrid destination context...",
        "Optimizing for Business & Leisure...",
        "Prioritizing Fine Dining phrases...",
        "Finalizing your Spanish curriculum..."
    )

    LaunchedEffect(Unit) {
        // Cycle through statuses
        while (statusIndex < statuses.size - 1) {
            delay(1500)
            statusIndex++
        }
        delay(1500)
        onComplete()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Cinematic Blend Background (Consistent with Onboarding)
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .align(Alignment.CenterEnd),
                alpha = 0.5f,
                painter = painterResource(R.drawable.spanish_background),
                contentDescription = "Madrid"
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            0.3f to Color(0xFF0A192F),
                            0.7f to Color.Transparent
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                DestMadrid.mainColor.copy(alpha = 0.1f),
                                Color.Transparent
                            ),
                            radius = 2500f
                        )
                    )
            )
        }

        // Main Layout
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(80.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // LEFT SIDE: ANIMATION HUB
            Column(
                modifier = Modifier.weight(0.45f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(240.dp)
                ) {
                    val infiniteTransition = rememberInfiniteTransition(label = "Orbit")

                    val pulseScale by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.2f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1500, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "PulseScale"
                    )

                    val pulseAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 0.8f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1500, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "PulseAlpha"
                    )

                    // Inner Glow
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .scale(pulseScale)
                            .alpha(pulseAlpha)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(MaterialTheme.colorScheme.primary, Color.Transparent)
                                )
                            )
                    )

                    // Orbital Rings
                    repeat(3) { i ->
                        val rotation by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 360f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(2000 + i * 500, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "RingRotation"
                        )

                        Box(
                            modifier = Modifier
                                .size(180.dp + (i * 30).dp)
                                .drawBehind {
                                    drawArc(
                                        color = Color(0xFFD4AF37).copy(alpha = 0.4f),
                                        startAngle = rotation,
                                        sweepAngle = 90f,
                                        useCenter = false,
                                        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                                    )
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(64.dp))

                // Status Messages
                AnimatedContent(
                    targetState = statusIndex,
                    transitionSpec = {
                        fadeIn() + slideInVertically { it / 2 } togetherWith fadeOut() + slideOutVertically { -it / 2 }
                    },
                    label = "StatusTransition"
                ) { index ->
                    Text(
                        text = statuses[index],
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "AI Personalization in progress...",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.width(100.dp))

            // RIGHT SIDE: AMBIENT INFO
            TimeToDestination(modifier = Modifier.weight(0.8f))
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(widthDp = 1920, heightDp = 1080)
@Composable
fun GeneratingScreenPreview() {
    com.example.skycoach.ui.theme.SkyCoachTheme {
        GeneratingScreen {}
    }
}
