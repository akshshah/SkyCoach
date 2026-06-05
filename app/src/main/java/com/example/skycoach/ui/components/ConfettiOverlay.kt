package com.example.skycoach.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

// ── ConfettiOverlay ────────────────────────────────────────────────────────────
// FIX: replaced LaunchedEffect(animTime) with a proper withFrameMillis loop
// so particles update every frame and Canvas redraws reliably.

data class ConfettiPiece(
    val x: Float, val y: Float,
    val size: Float, val color: Color,
    val velocityX: Float, val velocityY: Float,
    val rotation: Float, val rotationSpeed: Float,
    val isCircle: Boolean
)

fun createConfettiPiece() = ConfettiPiece(
    x = Random.nextFloat(),
    y = -0.05f - Random.nextFloat() * 0.3f,
    size = 12f + Random.nextFloat() * 18f,
    color = listOf(Color(0xFFD4AF37), Color(0xFF64FFDA), Color(0xFFE2E8F0), Color(0xFF7F77DD), Color.White).random(),
    velocityX = (Random.nextFloat() - 0.5f) * 0.008f,
    velocityY = 0.008f + Random.nextFloat() * 0.015f,
    rotation = Random.nextFloat() * 360f,
    rotationSpeed = (Random.nextFloat() - 0.5f) * 8f,
    isCircle = Random.nextBoolean()
)

@Composable
fun ConfettiOverlay(level: String, phraseCount: Int, onDismiss: () -> Unit) {
    var particles by remember { mutableStateOf(List(80) { createConfettiPiece() }) }

    // Proper frame-driven animation loop — updates particles every vsync frame
    LaunchedEffect(Unit) {
        val startTime = withFrameMillis { it }
        var lastFrame = startTime
        while (true) {
            val now = withFrameMillis { it }
            val delta = (now - lastFrame).coerceAtMost(32L) / 16f  // normalise to ~60fps
            lastFrame = now
            particles = particles.map { p ->
                p.copy(
                    x = p.x + p.velocityX * delta,
                    y = p.y + (p.velocityY + 0.001f) * delta,  // gentle gravity
                    rotation = p.rotation + p.rotationSpeed * delta
                )
            }
            if (now - startTime > 4500L) break
        }
        onDismiss()
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.94f)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            particles.forEach { p ->
                val cx = p.x * size.width
                val cy = p.y * size.height
                if (cy < size.height + 50f) {
                    if (p.isCircle) {
                        drawCircle(color = p.color, radius = p.size / 2f, center = Offset(cx, cy))
                    } else {
                        withTransform({
                            rotate(p.rotation, Offset(cx, cy))
                        }) {
                            drawRect(
                                color = p.color,
                                topLeft = Offset(cx - p.size / 2f, cy - p.size / 2f),
                                size = Size(p.size, p.size)
                            )
                        }
                    }
                }
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "LEVEL COMPLETE",
                style = MaterialTheme.typography.bodyLarge.copy(letterSpacing = 4.sp),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = level, style = MaterialTheme.typography.displayLarge, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$phraseCount phrases mastered",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}