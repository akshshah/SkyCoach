package com.example.skycoach.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.skycoach.data.Flashcard
import kotlinx.coroutines.delay

// ── FlashcardWidget ───────────────────────────────────────────────────────────
// FIX: proper flip using two separate graphicsLayer blocks so back face content
// is counter-rotated and never appears mirrored. Visibility switches at 90°.

@Composable
fun FlashcardWidget(
    flashcard: Flashcard,
    modifier: Modifier = Modifier
) {
    var isFlipped by remember(flashcard.id) { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Drives 0f → 180f for flip
    val flipDegrees by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "CardFlip"
    )

    // Press lift
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "PressScale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth(0.45f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                cameraDistance = 12f * density
            }
            .clickable(interactionSource = interactionSource, indication = null) {
                isFlipped = !isFlipped
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            },
        contentAlignment = Alignment.Center
    ) {
        // FRONT
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(if (flipDegrees <= 90f) 1f else 0f)
                .graphicsLayer {
                    rotationY = flipDegrees
                    alpha = if (flipDegrees <= 90f) 1f else 0f
                    cameraDistance = 12f * density
                },
            contentAlignment = Alignment.Center
        ) {
            CardFront(flashcard)
        }

        // BACK
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(if (flipDegrees > 90f) 1f else 0f)
                .graphicsLayer {
                    rotationY = flipDegrees - 180f
                    alpha = if (flipDegrees > 90f) 1f else 0f
                    cameraDistance = 12f * density
                }
        ) {
            CardBack(flashcard)
        }
    }
}

// ── CardFront ─────────────────────────────────────────────────────────────────

@Composable
fun CardFront(flashcard: Flashcard) {
    // Audio playing state lives here — scoped to this card instance
    var isAudioPlaying by remember(flashcard.id) { mutableStateOf(false) }

    // Auto-stop after 2.5s to match a typical phrase pronunciation duration
    LaunchedEffect(isAudioPlaying) {
        if (isAudioPlaying) {
            delay(2500)
            isAudioPlaying = false
        }
    }

    GlassCard(modifier = Modifier.fillMaxSize(), cornerRadius = 32.dp) {
        // Box as root so audio button can be absolutely positioned over the card
        Box(modifier = Modifier.fillMaxSize()) {

            // ── Main card content — centered ──
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = flashcard.target,
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = flashcard.phonetic,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        letterSpacing = 1.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }

            // ── Pulsing "tap to reveal" hint — bottom center ──
            val infiniteTransition = rememberInfiniteTransition(label = "HintPulse")
            val hintAlpha by infiniteTransition.animateFloat(
                initialValue = 0.2f, targetValue = 0.8f,
                animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Reverse),
                label = "HintAlpha"
            )
            Text(
                text = "Tap to reveal",
                style = MaterialTheme.typography.bodyLarge.copy(
                    letterSpacing = 2.sp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = hintAlpha),
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            )

            // ── Audio button — bottom right, does NOT propagate tap to card flip ──
            CardAudioButton(
                isPlaying = isAudioPlaying,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp),
                onClick = { isAudioPlaying = !isAudioPlaying }
            )
        }
    }
}

// ── CardBack ──────────────────────────────────────────────────────────────────
// FIX: removed the inner graphicsLayer { rotationY = 180f } on Surface.
// The parent Box in FlashcardWidget already counter-rotates the whole back face.

@Composable
fun CardBack(flashcard: Flashcard) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        shape = RoundedCornerShape(32.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.05f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.03f)
                        )
                    )
                )
                .padding(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = flashcard.translation,
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = flashcard.phonetic,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        letterSpacing = 1.sp
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))

                // Highlighted example sentence
                val annotatedString = buildAnnotatedString {
                    val parts = flashcard.example.split(flashcard.highlightedWord)
                    if (parts.size >= 2) {
                        append(parts[0])
                        withStyle(
                            SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        ) { append(flashcard.highlightedWord) }
                        append(parts.drop(1).joinToString(flashcard.highlightedWord))
                    } else {
                        append(flashcard.example)
                    }
                }
                Text(
                    text = annotatedString,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 26.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    lineHeight = 32.sp
                )
            }
        }
    }
}

// ── CardAudioButton ────────────────────────────────────────────────────────────
// Standalone composable so the tap event is fully consumed here and never
// bubbles up to the FlashcardWidget clickable, preventing accidental card flips.

@Composable
fun CardAudioButton(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    // Animated border color — lights up primary when active
    val borderColor by animateColorAsState(
        targetValue = if (isPlaying) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
        animationSpec = tween(200),
        label = "AudioBorder"
    )
    // Subtle background fill on active state
    val bgColor by animateColorAsState(
        targetValue = if (isPlaying) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        else Color.Transparent,
        animationSpec = tween(200),
        label = "AudioBg"
    )
    // Press scale — spring feel
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "AudioPress"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .size(64.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
            }
            .border(
                width = 1.dp,
                color = borderColor,
                shape = CircleShape
            )
            .background(color = bgColor, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // Crossfade between soundwave (active) and speaker icon (idle)
        Crossfade(
            targetState = isPlaying,
            animationSpec = tween(150),
            label = "AudioIconSwap"
        ) { playing ->
            if (playing) {
                // Inline soundwave — compact 3-bar version for the button
                CardSoundwaveAnimation()
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Pronounce phrase",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

// ── CardSoundwaveAnimation ────────────────────────────────────────────────────
// Compact 3-bar wave — fits neatly inside the 52dp button without crowding.

@Composable
fun CardSoundwaveAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "CardWave")
    // Each bar has a distinct target height and speed — staggered so they never
    // all peak at the same time, which would look mechanical.
    val barDefs = listOf(
        Pair(0.5f, 380),
        Pair(1.0f, 260),
        Pair(0.6f, 320)
    )

    Row(
        modifier = Modifier.height(26.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        barDefs.forEachIndexed { index, (target, duration) ->
            val heightFraction by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = target,
                animationSpec = infiniteRepeatable(
                    animation = tween(duration, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "CardBar$index"
            )
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight(heightFraction)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            )
        }
    }
}