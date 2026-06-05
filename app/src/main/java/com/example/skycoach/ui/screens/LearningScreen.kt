package com.example.skycoach.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skycoach.data.Flashcard
import com.example.skycoach.data.FlashcardType
import com.example.skycoach.data.MadridDeck
import com.example.skycoach.ui.components.ConfettiCanvasOverlay
import com.example.skycoach.ui.components.FlashcardWidget
import com.example.skycoach.ui.components.GlassCard
import kotlinx.coroutines.delay

@Composable
fun LearningScreen(
    deck: List<Flashcard> = MadridDeck,
    levelName: String = "Grounded",
    isRetake: Boolean = false,
    onReadyForQuiz: () -> Unit
) {
    // ── State ──
    var currentIndex by remember { mutableIntStateOf(0) }
    var navigatingForward by remember { mutableStateOf(true) }
    val viewedIndices = remember { mutableStateSetOf<Int>() }
    var showConfetti by remember { mutableStateOf(false) }
    var showOnRoll by remember { mutableStateOf(false) }
    var onRollShown by remember { mutableStateOf(false) }
    var consecutiveCount by remember { mutableIntStateOf(0) }
    var isAmbientOn by remember { mutableStateOf(false) }

    val allStandardViewed = remember(viewedIndices.size) {
        deck.filter { it.type == FlashcardType.STANDARD }.all { viewedIndices.contains(it.id) }
    }

    // Mark standard cards as viewed when index changes
    LaunchedEffect(currentIndex) {
        val card = deck[currentIndex]
        if (card.type == FlashcardType.STANDARD) {
            viewedIndices.add(card.id)
            consecutiveCount++
            if (consecutiveCount >= 3 && !onRollShown) {
                showOnRoll = true
                onRollShown = true
                delay(1500)
                showOnRoll = false
            }
        } else {
            consecutiveCount = 0
        }
    }

    // Smooth progress — counts only standard cards viewed
    val standardTotal = deck.count { it.type == FlashcardType.STANDARD }
    val standardViewed = viewedIndices.size
    val progress by animateFloatAsState(
        targetValue = if (standardTotal == 0) 0f else standardViewed.toFloat() / standardTotal,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "DeckProgress"
    )

    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = MaterialTheme.colorScheme.background)) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 100.dp, vertical = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Top bar ──
            LearningTopBar(
                levelName = levelName,
                progress = progress,
                currentIndex = currentIndex,
                total = deck.size,
                isAmbientOn = isAmbientOn,
                onAmbientToggle = { isAmbientOn = !isAmbientOn }
            )

            Spacer(modifier = Modifier.weight(1f))

            // ── Card area ──
            Box(modifier = Modifier.weight(4f), contentAlignment = Alignment.Center) {
                AnimatedContent(
                    targetState = currentIndex,
                    transitionSpec = {
                        if (navigatingForward) {
                            (slideInHorizontally(tween(300, easing = FastOutSlowInEasing)) { it } + fadeIn(tween(300)))
                                .togetherWith(slideOutHorizontally(tween(300, easing = FastOutSlowInEasing)) { -it } + fadeOut(tween(300)))
                        } else {
                            (slideInHorizontally(tween(300, easing = FastOutSlowInEasing)) { -it } + fadeIn(tween(300)))
                                .togetherWith(slideOutHorizontally(tween(300, easing = FastOutSlowInEasing)) { it } + fadeOut(tween(300)))
                        }
                    },
                    label = "CardSlide"
                ) { index ->
                    val card = deck[index]
                    when (card.type) {
                        FlashcardType.STANDARD -> FlashcardWidget(flashcard = card)
                        FlashcardType.DIVIDER -> CategoryDividerView(card = card) {
                            if (currentIndex < deck.size - 1) {
                                navigatingForward = true
                                currentIndex++
                            }
                        }
                        FlashcardType.TIP -> CulturalTipView(card = card) {
                            if (currentIndex < deck.size - 1) {
                                navigatingForward = true
                                currentIndex++
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // ── Navigation row (Back / Forward) for standard cards ──
            if (deck[currentIndex].type == FlashcardType.STANDARD) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            if (currentIndex > 0) {
                                navigatingForward = false
                                currentIndex--
                            }
                        },
                        enabled = currentIndex > 0
                    ) {
                        Icon(
                            modifier = Modifier.size(38.dp),
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous",
                            tint = if (currentIndex > 0) Color.White.copy(alpha = 0.7f) else Color.Transparent
                        )
                    }
                    IconButton(
                        onClick = {
                            if (currentIndex < deck.size - 1) {
                                navigatingForward = true
                                currentIndex++
                            }
                        },
                        enabled = currentIndex < deck.size - 1
                    ) {
                        Icon(
                            modifier = Modifier.size(38.dp),
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next",
                            tint = if (currentIndex < deck.size - 1) Color.White.copy(alpha = 0.7f) else Color.Transparent
                        )
                    }
                }
            }

            if (deck.size - 1 == currentIndex) {
                LearningCTAButton(
                    isEnabled = allStandardViewed,
                    isRetake = isRetake,
                    onClick = {
                        if (allStandardViewed) {
                            showConfetti = true
                        }
                    }
                )
            }
        }

        // ── Confetti / completion overlay ──
        if (showConfetti) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.94f)),
                contentAlignment = Alignment.Center
            ){
                ConfettiCanvasOverlay(
                    onDismiss = {
                        showConfetti = false
                        onReadyForQuiz()
                    }
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = levelName,
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$standardTotal phrases mastered",
                        fontSize = 24.sp,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

// ── LearningTopBar ─────────────────────────────────────────────────────────────

@Composable
fun LearningTopBar(
    levelName: String,
    progress: Float,
    currentIndex: Int,
    total: Int,
    isAmbientOn: Boolean,
    onAmbientToggle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Level pill
        Surface(
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            shape = CircleShape,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(vertical = 15.dp, horizontal = 30.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "CURRENT STAGE: Learning",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "LEVEL: $levelName",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.5.sp
                    )
                }
            }
        }

        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "Card ${currentIndex + 1} of $total",
                fontSize = 24.sp,
                style = MaterialTheme.typography.bodyLarge.copy(letterSpacing = 1.sp),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .width(500.dp)
                    .height(10.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        )
                )
            }
        }
    }
}

// ── AmbientAudioToggle ────────────────────────────────────────────────────────

@Composable
fun AmbientAudioToggle(isOn: Boolean, onToggle: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "Ripple")
    val rippleScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.6f,
        animationSpec = infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Restart),
        label = "RippleScale"
    )
    val rippleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Restart),
        label = "RippleAlpha"
    )

    Box(
        modifier = Modifier
            .size(50.dp)
            .clickable(onClick = onToggle),
        contentAlignment = Alignment.Center
    ) {
        // Ripple rings when on
        if (isOn) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .scale(rippleScale)
                    .alpha(rippleAlpha)
                    .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )
        }
        // Crossfade between icon states
        Crossfade(targetState = isOn, animationSpec = tween(200), label = "AmbientIcon") { on ->
            Icon(
                imageVector = if (on) Icons.Default.Headphones else Icons.AutoMirrored.Filled.VolumeOff,
                contentDescription = if (on) "Ambient audio on" else "Ambient audio off",
                tint = if (on) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 1f),
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

// ── CategoryDividerView ────────────────────────────────────────────────────────
// FIX: onSkip guard prevents double-fire on recomposition

@Composable
fun CategoryDividerView(card: Flashcard, onSkip: () -> Unit) {
    var skipProgress by remember { mutableFloatStateOf(0f) }
    var hasSkipped by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animate(initialValue = 0f, targetValue = 1f, animationSpec = tween(2000, easing = LinearEasing)) { v, _ ->
            skipProgress = v
        }
        if (!hasSkipped) {
            hasSkipped = true
            onSkip()
        }
    }

    // Fade-in entrance (not slide — chapter break feel)
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(400))
    ) {
        GlassCard(modifier = Modifier.size(500.dp)) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "NEXT TOPIC",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(38.dp))
                card.categoryIcon?.let {
                    Icon(it, contentDescription = null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary)
                }
                Text(text = card.category, style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(48.dp))
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { skipProgress },
                        modifier = Modifier.size(64.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp,
                        trackColor = Color.White.copy(alpha = 0.1f)
                    )
                    TextButton(onClick = {
                        if (!hasSkipped) { hasSkipped = true; onSkip() }
                    }) {
                        Text("Skip", style = MaterialTheme.typography.labelLarge, color = Color.White)
                    }
                }
            }
        }
    }
}

// ── CulturalTipView ────────────────────────────────────────────────────────────
// FIX: scale+fade entrance (surfaces from behind), guarded auto-skip

@Composable
fun CulturalTipView(card: Flashcard, onSkip: () -> Unit) {
    var hasSkipped by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(4000)
        if (!hasSkipped) { hasSkipped = true; onSkip() }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "TipGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.05f, targetValue = 0.18f,
        animationSpec = infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "GlowAlpha"
    )

    // Scale + fade entrance
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(tween(350, easing = FastOutSlowInEasing), initialScale = 0.85f) + fadeIn(tween(350))
    ) {
        Surface(
            modifier = Modifier.size(500.dp),
            color = Color(0xFF112240),
            shape = RoundedCornerShape(32.dp),
            border = BorderStroke(1.dp, Color(0xFFD4AF37).copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color(0xFFD4AF37).copy(alpha = glowAlpha),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.padding(48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFD4AF37), modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = card.tip,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 40.sp
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    TextButton(onClick = {
                        if (!hasSkipped) { hasSkipped = true; onSkip() }
                    }) {
                        Text("Skip", style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }
        }
    }
}

// ── LearningCTAButton ─────────────────────────────────────────────────────────
// FIX: full animated transition from disabled → enabled (color + elevation + text)

@Composable
fun LearningCTAButton(
    isEnabled: Boolean,
    isRetake: Boolean,
    onClick: () -> Unit
) {
    val containerColor by animateColorAsState(
        targetValue = if (isEnabled) MaterialTheme.colorScheme.primary else Color.DarkGray.copy(alpha = 0.3f),
        animationSpec = tween(400),
        label = "CTAContainer"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isEnabled) MaterialTheme.colorScheme.onPrimary else Color.Gray,
        animationSpec = tween(400),
        label = "CTAContent"
    )
    val elevation by animateDpAsState(
        targetValue = if (isEnabled) 8.dp else 0.dp,
        animationSpec = tween(400),
        label = "CTAElevation"
    )

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "CTAPress"
    )

    Button(
        onClick = onClick,
        enabled = isEnabled,
        modifier = Modifier
            .width(500.dp)
            .height(80.dp)
            .scale(pressScale),
        interactionSource = interactionSource,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = elevation),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor,
            disabledContentColor = contentColor
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (isRetake) "RETAKE THE QUIZ" else "I'M READY FOR THE QUIZ",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp
                )
            )
            Spacer(modifier = Modifier.width(16.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(widthDp = 1920, heightDp = 1080)
@Composable
fun LearningScreenPreview() {
    com.example.skycoach.ui.theme.SkyCoachTheme {
        LearningScreen {}
    }
}
