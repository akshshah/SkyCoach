package com.example.skycoach.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.random.Random

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
fun ConfettiCanvasOverlay(onDismiss: () -> Unit) {
    var particles by remember { mutableStateOf(List(80) { createConfettiPiece() }) }

    // Frame-driven animation loop
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
            if (now - startTime > 3000L) break
        }
        onDismiss()
    }

    // Pointer input disabled ensures the background layer cannot intercept tap events
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
}