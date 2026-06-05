package com.example.skycoach.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skycoach.data.MadridQuiz
import com.example.skycoach.data.QuestionType
import com.example.skycoach.data.QuizQuestion
import com.example.skycoach.ui.components.ConfettiCanvasOverlay
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class QuizState { QUESTIONS, CONFIDENCE, RESULTS }

@Composable
fun QuizScreen(
    quiz: List<QuizQuestion> = MadridQuiz,
    levelName: String = "Grounded",
    onNextLevel: () -> Unit,
    onRetry: () -> Unit
) {
    var currentState by remember { mutableStateOf(QuizState.QUESTIONS) }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var selectedConfidence by remember { mutableStateOf("") }
    var navigatingForward by remember { mutableStateOf(true) }

    val totalQuestions = quiz.size
    val scorePercent = (score.toFloat() / totalQuestions * 100).toInt()

    val progress by animateFloatAsState(
        targetValue = (currentQuestionIndex + 1).toFloat() / totalQuestions,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "QuizProgress"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A192F))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 100.dp, vertical = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar — only shown during questions
            AnimatedVisibility(
                visible = currentState == QuizState.QUESTIONS,
                enter = fadeIn(tween(300)),
                exit = fadeOut(tween(300))
            ) {
                QuizTopBar(progress, currentQuestionIndex, totalQuestions)
            }

            Spacer(modifier = Modifier.weight(1f))

            AnimatedContent(
                targetState = currentState,
                transitionSpec = {
                    if (navigatingForward) {
                        (slideInHorizontally(tween(350, easing = FastOutSlowInEasing)) { it } + fadeIn(tween(350)))
                            .togetherWith(slideOutHorizontally(tween(350, easing = FastOutSlowInEasing)) { -it } + fadeOut(tween(300)))
                    } else {
                        (slideInHorizontally(tween(350, easing = FastOutSlowInEasing)) { -it } + fadeIn(tween(350)))
                            .togetherWith(slideOutHorizontally(tween(350, easing = FastOutSlowInEasing)) { it } + fadeOut(tween(300)))
                    }
                },
                label = "QuizStateTransition"
            ) { state ->
                when (state) {
                    QuizState.QUESTIONS -> {
                        AnimatedContent(
                            targetState = currentQuestionIndex,
                            transitionSpec = {
                                (slideInHorizontally(tween(300, easing = FastOutSlowInEasing)) { it } + fadeIn(tween(300)))
                                    .togetherWith(slideOutHorizontally(tween(300, easing = FastOutSlowInEasing)) { -it } + fadeOut(tween(300)))
                            },
                            label = "QuestionSlide"
                        ) { index ->
                            QuestionView(
                                question = quiz[index],
                                onAnswered = { isCorrect ->
                                    if (isCorrect) score++
                                    navigatingForward = true
                                    if (currentQuestionIndex < totalQuestions - 1) {
                                        currentQuestionIndex++
                                    } else {
                                        currentState = QuizState.CONFIDENCE
                                    }
                                }
                            )
                        }
                    }

                    QuizState.CONFIDENCE -> {
                        ConfidenceView(
                            onSelected = { confidence ->
                                selectedConfidence = confidence
                                navigatingForward = true
                                currentState = QuizState.RESULTS
                            }
                        )
                    }

                    QuizState.RESULTS -> {
                        ResultsView(
                            scorePercent = scorePercent,
                            levelName = levelName,
                            confidence = selectedConfidence,
                            onRetry = onRetry,
                            onNextLevel = onNextLevel
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun QuestionView(
    question: QuizQuestion,
    onAnswered: (Boolean) -> Unit
) {
    val typeLabel = when (question.type) {
        QuestionType.TRANSLATION -> "Translation"
        QuestionType.CONTEXT -> "Situation"
        QuestionType.PRONUNCIATION -> "Pronunciation"
    }

    val typeIcon = when (question.type) {
        QuestionType.TRANSLATION -> Icons.Default.Translate
        QuestionType.CONTEXT -> Icons.Default.Place
        QuestionType.PRONUNCIATION -> Icons.AutoMirrored.Filled.VolumeUp
    }

    var selectedOption by remember(question.id) { mutableStateOf<String?>(null) }
    var isLocked by remember(question.id) { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Global stack grouping the layout blocks vertically down the screen center
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start // Forces header to snap to extreme left boundary of the question text
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Contextual Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.wrapContentSize()
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                ) {
                    Icon(
                        imageVector = typeIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(12.dp)
                            .size(24.dp)
                    )
                }

                Text(
                    text = typeLabel,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Question Text (Defines the width of this entire local column container)
            Text(
                text = question.question,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color.White,
                lineHeight = 42.sp,
            )
        }

        Spacer(modifier = Modifier.height(56.dp))

        // ── Block 2: Independent Options (Always perfectly centered to the whole screen width) ──
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (question.type == QuestionType.PRONUNCIATION) {
                PronunciationOptions(
                    question = question,
                    selectedOption = selectedOption,
                    isLocked = isLocked,
                    onOptionSelected = { option ->
                        if (!isLocked) {
                            selectedOption = option
                            isLocked = true
                            val isCorrect = option == question.correctAnswer
                            scope.launch {
                                delay(1000)
                                onAnswered(isCorrect)
                            }
                        }
                    }
                )
            } else {
                TextOptions(
                    question = question,
                    selectedOption = selectedOption,
                    isLocked = isLocked,
                    onOptionSelected = { option ->
                        if (!isLocked) {
                            selectedOption = option
                            isLocked = true
                            val isCorrect = option == question.correctAnswer
                            scope.launch {
                                delay(900)
                                onAnswered(isCorrect)
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun TextOptions(
    question: QuizQuestion,
    selectedOption: String?,
    isLocked: Boolean,
    onOptionSelected: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.width(500.dp)
    ) {
        question.options.forEach { option ->
            val isSelected = selectedOption == option
            val isCorrect = option == question.correctAnswer
            val isWrongSelected = isSelected && !isCorrect

            // Determine visual state
            val borderColor by animateColorAsState(
                targetValue = when {
                    isLocked && isCorrect -> Color(0xFF64FFDA)        // Always highlight correct after lock
                    isWrongSelected -> Color(0xFFFF6B6B)              // Red for wrong selection
                    else -> Color.White.copy(alpha = 0.1f)
                },
                animationSpec = tween(300),
                label = "OptionBorder_$option"
            )
            val bgColor by animateColorAsState(
                targetValue = when {
                    isLocked && isCorrect -> Color(0xFF64FFDA).copy(alpha = 0.08f)
                    isWrongSelected -> Color(0xFFFF6B6B).copy(alpha = 0.08f)
                    isSelected -> Color.White.copy(alpha = 0.07f)
                    else -> Color.White.copy(alpha = 0.03f)
                },
                animationSpec = tween(300),
                label = "OptionBg_$option"
            )
            val textColor by animateColorAsState(
                targetValue = when {
                    isLocked && isCorrect -> Color(0xFF64FFDA)
                    isWrongSelected -> Color(0xFFFF6B6B)
                    else -> Color.White.copy(alpha = if (isLocked && !isCorrect) 0.35f else 0.9f)
                },
                animationSpec = tween(300),
                label = "OptionText_$option"
            )

            // Press scale — disabled after lock so it doesn't animate on locked taps
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val scale by animateFloatAsState(
                targetValue = if (isPressed && !isLocked) 0.98f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "OptionScale_$option"
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(scale)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        enabled = !isLocked
                    ) { onOptionSelected(option) },
                color = bgColor,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, borderColor)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 22.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyLarge,
                        color = textColor
                    )
                    // Show tick or cross icon after lock
                    AnimatedVisibility(
                        visible = isLocked && (isCorrect || isSelected),
                        enter = scaleIn(tween(250, easing = FastOutSlowInEasing)) + fadeIn(tween(250))
                    ) {
                        Icon(
                            imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = null,
                            tint = if (isCorrect) Color(0xFF64FFDA) else Color(0xFFFF6B6B),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PronunciationOptions(
    question: QuizQuestion,
    selectedOption: String?,
    isLocked: Boolean,
    onOptionSelected: (String) -> Unit
) {
    // Track which audio clip is currently playing
    var playingOption by remember(question.id) { mutableStateOf<String?>(null) }

    // Auto-stop playback after 2.5s mock duration
    LaunchedEffect(playingOption) {
        if (playingOption != null) {
            delay(2500)
            playingOption = null
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        // Instruction label
        Text(
            text = "Tap to listen, then select the correct pronunciation",
            style = MaterialTheme.typography.bodyLarge.copy(letterSpacing = 0.5.sp),
            color = Color.White.copy(alpha = 0.5f)
        )

        // 2x2 grid of audio option cards
        val chunked = question.options.chunked(2)
        chunked.forEach { rowOptions ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                rowOptions.forEach { option ->
                    PronunciationOptionCard(
                        label = option,
                        isPlaying = playingOption == option,
                        isSelected = selectedOption == option,
                        isLocked = isLocked,
                        isCorrect = option == question.correctAnswer,
                        onPlay = {
                            if (!isLocked) playingOption = if (playingOption == option) null else option
                        },
                        onSelect = { onOptionSelected(option) }
                    )
                }
            }
        }
    }
}

@Composable
fun PronunciationOptionCard(
    label: String,
    isPlaying: Boolean,
    isSelected: Boolean,
    isLocked: Boolean,
    isCorrect: Boolean,
    onPlay: () -> Unit,
    onSelect: () -> Unit
) {
    val isWrongSelected = isSelected && !isCorrect

    val borderColor by animateColorAsState(
        targetValue = when {
            isLocked && isCorrect -> Color(0xFF64FFDA)
            isWrongSelected -> Color(0xFFFF6B6B)
            isPlaying -> MaterialTheme.colorScheme.primary
            isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            else -> Color.White.copy(alpha = 0.1f)
        },
        animationSpec = tween(300),
        label = "PronBorder"
    )
    val bgColor by animateColorAsState(
        targetValue = when {
            isLocked && isCorrect -> Color(0xFF64FFDA).copy(alpha = 0.08f)
            isWrongSelected -> Color(0xFFFF6B6B).copy(alpha = 0.08f)
            isPlaying -> MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
            else -> Color.White.copy(alpha = 0.03f)
        },
        animationSpec = tween(300),
        label = "PronBg"
    )

    Surface(
        modifier = Modifier.size(320.dp, 150.dp),
        color = bgColor,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top row: label + result icon if locked
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge.copy(letterSpacing = 1.sp),
                    color = Color.White.copy(alpha = if (isLocked && !isCorrect && !isSelected) 0.35f else 0.9f)
                )
                AnimatedVisibility(
                    visible = isLocked && (isCorrect || isSelected),
                    enter = scaleIn(tween(250)) + fadeIn(tween(250))
                ) {
                    Icon(
                        imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        contentDescription = null,
                        tint = if (isCorrect) Color(0xFF64FFDA) else Color(0xFFFF6B6B),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Bottom row: play button + select button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play audio button
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(
                            color = if (isPlaying) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.White.copy(
                                alpha = 0.07f
                            ),
                            shape = CircleShape
                        )
                        .border(
                            1.dp,
                            if (isPlaying) MaterialTheme.colorScheme.primary else Color.White.copy(
                                alpha = 0.15f
                            ),
                            CircleShape
                        )
                        .pointerInput(Unit) { detectTapGestures { onPlay() } },
                    contentAlignment = Alignment.Center
                ) {
                    Crossfade(targetState = isPlaying, animationSpec = tween(150), label = "PronPlay") { playing ->
                        if (playing) {
                            // Inline 3-bar soundwave
                            PronSoundwave()
                        } else {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                // Select this option button — only enabled after listening
                val hasListened = remember(label) { mutableStateOf(false) }
                LaunchedEffect(isPlaying) { if (!isPlaying && hasListened.value.not()) hasListened.value = true }

                OutlinedButton(
                    onClick = onSelect,
                    enabled = !isLocked,
                    modifier = Modifier.height(54.dp),
                    shape = CircleShape,
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.2f)
                    ),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = if (isSelected) "Selected" else "Select",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun PronSoundwave() {
    val infiniteTransition = rememberInfiniteTransition(label = "PronWave")
    val barDefs = listOf(Triple(0.5f, 380, 0), Triple(1.0f, 260, 0), Triple(0.6f, 320, 0))
    Row(
        modifier = Modifier.height(24.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        barDefs.forEachIndexed { i, (target, dur, _) ->
            val h by infiniteTransition.animateFloat(
                initialValue = 0.2f, targetValue = target,
                animationSpec = infiniteRepeatable(tween(dur, easing = FastOutSlowInEasing), RepeatMode.Reverse),
                label = "PW$i"
            )
            Box(Modifier
                .width(4.dp)
                .fillMaxHeight(h)
                .background(MaterialTheme.colorScheme.primary, CircleShape))
        }
    }
}

@Composable
fun ConfidenceView(onSelected: (String) -> Unit) {
    var selectedConfidence by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "How confident do you feel about these phrases?",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            textAlign = TextAlign.Center,
            lineHeight = 44.sp
        )

        Spacer(modifier = Modifier.height(64.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            val options = listOf(
                Triple("😅", "Lucky", "Got through it\nbut not sure how"),
                Triple("🤔", "Unsure", "Some phrases felt\nfamiliar, some didn't"),
                Triple("😎", "Confident", "Felt solid on\nmost of them")
            )

            options.forEach { (emoji, label, subtitle) ->
                val isSelected = selectedConfidence == label
                val borderColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.1f),
                    animationSpec = tween(250),
                    label = "ConfBorder_$label"
                )
                val bgColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.03f),
                    animationSpec = tween(250),
                    label = "ConfBg_$label"
                )
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.04f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "ConfScale_$label"
                )

                Surface(
                    modifier = Modifier
                        .width(260.dp)
                        .scale(scale)
                        .clickable {
                            selectedConfidence = label
                            scope.launch {
                                delay(400) // Brief moment to see selection before advancing
                                onSelected(label)
                            }
                        },
                    color = bgColor,
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = emoji, fontSize = 40.sp)
                        Text(
                            text = label,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ResultsView(
    scorePercent: Int,
    levelName: String,
    confidence: String,
    onRetry: () -> Unit,
    onNextLevel: () -> Unit
) {
    val isPerfect = scorePercent >= 90
    val isPassed = scorePercent >= 70
    val scoreColor = when {
        scorePercent >= 90 -> Color(0xFF64FFDA)
        scorePercent >= 70 -> Color(0xFFD4AF37)
        else -> Color(0xFFFF6B6B)
    }

    var displayScore by remember { mutableIntStateOf(0) }
    LaunchedEffect(scorePercent) {
        animate(
            initialValue = 0f,
            targetValue = scorePercent.toFloat(),
            animationSpec = tween(1200, easing = FastOutSlowInEasing)
        ) { value, _ -> displayScore = value.toInt() }
    }

    // State variable to manage running visibility window
    var showConfetti by remember { mutableStateOf(isPerfect) }

    // Root overlay shell container
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        // 1. Confetti Background Engine Layer
        if (showConfetti) {
            ConfettiCanvasOverlay(onDismiss = { showConfetti = false })
        }

        // 2. Main Content Card Layout Surface
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isPassed) "QUIZ COMPLETE" else "KEEP PRACTISING",
                style = MaterialTheme.typography.headlineMedium.copy(letterSpacing = 4.sp),
                color = scoreColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$displayScore%",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 108.sp,
                    fontWeight = FontWeight.ExtraBold
                ),
                color = scoreColor
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = when {
                    scorePercent >= 90 -> "Outstanding — you're ready for the next level"
                    scorePercent >= 70 -> "Solid pass — a few phrases to review"
                    else -> "Not quite — revisit the phrases and try again"
                },
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 28.sp,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )

            if (isPerfect) {
                Spacer(modifier = Modifier.height(24.dp))
                PerfectLandingBadge()
            }

            Spacer(modifier = Modifier.height(64.dp))

            if (confidence.isNotEmpty()) {
                val confidenceEmoji = when (confidence) {
                    "Lucky" -> "😅"
                    "Unsure" -> "🤔"
                    "Confident" -> "😎"
                    else -> ""
                }
                Surface(
                    color = Color.White.copy(alpha = 0.04f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                    modifier = Modifier.padding(bottom = 40.dp)
                ) {
                    Text(
                        text = "$confidenceEmoji  You felt $confidence — your next session will adapt accordingly",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                OutlinedButton(
                    onClick = onRetry,
                    modifier = Modifier.height(72.dp).width(300.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                    shape = CircleShape,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "RETRY", style = MaterialTheme.typography.labelLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp))
                }

                Button(
                    onClick = onNextLevel,
                    modifier = Modifier.height(72.dp).width(300.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = CircleShape
                ) {
                    Text(text = if (isPassed) "NEXT LEVEL" else "CONTINUE ANYWAY", fontSize = 18.sp, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun PerfectLandingBadge() {
    val infiniteTransition = rememberInfiniteTransition(label = "BadgeGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "BadgeAlpha"
    )

    // Scale-in entrance on first composition
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(tween(400, easing = FastOutSlowInEasing), initialScale = 0.6f) + fadeIn(tween(400))
    ) {
        Surface(
            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = glowAlpha))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    Icons.Default.WorkspacePremium,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = "Perfect Landing",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun QuizTopBar(
    progress: Float,
    currentIndex: Int,
    total: Int,
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
                        text = "CURRENT STAGE: Quiz",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "LEVEL: Grounded",
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
                text = "Question ${currentIndex + 1} of $total",
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

@androidx.compose.ui.tooling.preview.Preview(widthDp = 1920, heightDp = 1080)
@Composable
fun QuizScreenPreview() {
    com.example.skycoach.ui.theme.SkyCoachTheme {
        QuizScreen(onNextLevel = {}, onRetry = {})
    }
}
