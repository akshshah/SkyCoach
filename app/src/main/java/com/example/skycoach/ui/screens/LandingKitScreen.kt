package com.example.skycoach.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skycoach.R
import com.example.skycoach.ui.theme.SkyCoachTheme

data class LandingKitData(
    val passengerName: String = "Mr. John Doe",
    val fromCode: String = "JFK",
    val fromCity: String = "NEW YORK",
    val toCode: String = "MAD",
    val toCity: String = "MADRID",
    val language: String = "Spanish",
    val phraseCount: Int = 47,
    val levelAchieved: String = "Local",
    val flightNumber: String = "SK 2401",
    val airline: String = "SkyCoach Air"
)
// ── LandingKitScreen ──────────────────────────────────────────────────────────
@Composable
fun LandingKitScreen(
    data: LandingKitData = LandingKitData(),
    onFinish: () -> Unit
) {
    // Entrance animation — whole ticket fades and scales in
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        // Subtle background dot grid for depth
        DotGridBackground()

        AnimatedVisibility(
            visible = visible,
            enter = scaleIn(tween(500, easing = FastOutSlowInEasing), initialScale = 0.92f) + fadeIn(tween(500))
        ) {
            Column(
                modifier = Modifier.scale(1.15f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(60.dp)
            ) {
                // Header label
                Text(
                    text = "YOUR LANDING KIT IS READY",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        letterSpacing = 4.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                )

                // The boarding ticket
                BoardingTicket(data = data)

                // Finish button
                Button(
                    onClick = onFinish,
                    modifier = Modifier.height(64.dp).width(220.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = CircleShape
                ) {
                    Text(
                        text = "FINISH",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun DotGridBackground(
    modifier: Modifier = Modifier,
    dotColor: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f), // Higher alpha so it's visible
    dotRadius: Dp = 2.dp, // Slightly larger dot for a sharper tech/modern look
    gridSpacing: Dp = 32.dp // Closer spacing looks cleaner on mobile screens
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val spacingPx = gridSpacing.toPx()
        val radiusPx = dotRadius.toPx()

        val cols = (size.width / spacingPx).toInt() + 1
        val rows = (size.height / spacingPx).toInt() + 1

        for (col in 0..cols) {
            for (row in 0..rows) {
                drawCircle(
                    color = dotColor,
                    radius = radiusPx,
                    center = Offset(col * spacingPx, row * spacingPx)
                )
            }
        }
    }
}

// ── BoardingTicket ────────────────────────────────────────────────────────────

@Composable
fun BoardingTicket(data: LandingKitData) {
    // Outer shadow container
    Box(
        modifier = Modifier
            .width(1500.dp)
            .height(520.dp)
            .shadow(elevation = 40.dp, shape = RoundedCornerShape(28.dp), ambientColor = Color.Black.copy(alpha = 0.6f))
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White,
            shape = RoundedCornerShape(28.dp)
        ) {
            Row(modifier = Modifier.fillMaxSize()) {

                // ── LEFT STUB — Branding ──────────────────────────────────────
                TicketLeftStub(data = data)

                // ── PERFORATION — Classic ticket tear line with notches ───────
                TicketPerforation()

                // ── CENTER — Passenger, route, stats ─────────────────────────
                TicketCenter(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    data = data
                )

                // ── PERFORATION — Second tear line before QR section ──────────
                TicketPerforation()

                // ── RIGHT STUB — Two QR codes ─────────────────────────────────
                TicketQRStub(data = data)
            }
        }
    }
}

// ── TicketLeftStub ────────────────────────────────────────────────────────────

@Composable
fun TicketLeftStub(data: LandingKitData) {
    val planePainter = rememberVectorPainter(image = Icons.Default.Flight)
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(350.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(topStart = 28.dp, bottomStart = 28.dp))
            .padding(36.dp)
    ) {
        // Faint world map watermark
        Icon(
            imageVector = Icons.Default.Public,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.03f),
            modifier = Modifier
                .fillMaxSize()
                .scale(1.9f)
                .align(Alignment.Center)
                .offset(x = 20.dp, y = 0.dp)
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // Plane icon as logo mark
            Icon(
                imageVector = Icons.Default.AirplanemodeActive,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Brand name
            Text(
                text = "SKYCOACH",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 3.sp
                ),
                color = Color.White
            )
            Text(
                text = "LANDING KIT",
                style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 6.sp),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Gold divider line
            Box(
                modifier = Modifier
                    .width(320.dp)
                    .height(2.dp)
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xFFD4AF37), Color(0xFFD4AF37).copy(alpha = 0f)))
                    )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Language + level achieved
            TicketDataField(
                label = "LANGUAGE",
                value = data.language.uppercase(),
                valueColor = Color.White
            )
            Spacer(modifier = Modifier.height(20.dp))
            TicketDataField(
                label = "LEVEL ACHIEVED",
                value = data.levelAchieved.uppercase(),
                valueColor = Color(0xFFD4AF37)
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier.width(320.dp), // Set a constrained width
                contentAlignment = Alignment.CenterStart // Aligns everything inside to the left
            ) {
                // 1. Tagline (Bottom Layer)
                Text(
                    text = "Boarded a tourist,\nLanded a local",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontStyle = FontStyle.Italic,
                        lineHeight = 24.sp
                    ),
                    color = Color.White.copy(alpha = 0.6f),
                )

                // 2. The Line (Top Layer - Overlapping)
                Box(
                    modifier = Modifier
                        .width(245.dp)
                        .height(2.dp)
                        .align(Alignment.CenterStart) // Pins the line to the top of the box
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFD4AF37).copy(alpha = 0f), Color(0xFFD4AF37))
                            )
                        )
                )
                Icon(
                    imageVector = Icons.Default.AirplanemodeActive,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp).align(Alignment.CenterEnd).rotate(90f)
                )
            }
        }
    }
}

// ── TicketPerforation ─────────────────────────────────────────────────────────

@Composable
fun TicketPerforation() {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(24.dp)
    ) {
        // Notch cutouts at top and bottom — classic boarding pass tear line
        // Top semicircle notch
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Dashed center line
            drawLine(
                color = Color(0x80000000),
                start = Offset(center.x, 0.dp.toPx()),
                end = Offset(center.x, size.height),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
                strokeWidth = 2.dp.toPx()
            )
            // Top-notch — arc cut into the ticket from above
            drawArc(
                color = Color(0xFF0A192F),
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(0f, -12.dp.toPx()),
                size = Size(24.dp.toPx(), 24.dp.toPx())
            )
            // Bottom notch
            drawArc(
                color = Color(0xFF0A192F),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(0f, size.height - 12.dp.toPx()),
                size = Size(24.dp.toPx(), 24.dp.toPx())
            )
        }
    }
}

// ── TicketCenter ──────────────────────────────────────────────────────────────

@Composable
fun TicketCenter(modifier: Modifier, data: LandingKitData) {
    // Faint dot-pattern background on the cream section
    Box(modifier = modifier.background(Color(0xFFF9F7F2))) {

        // Subtle dot watermark
        Canvas(modifier = Modifier.fillMaxSize()) {
            val s = 28.dp.toPx()
            val cols = (size.width / s).toInt() + 1
            val rows = (size.height / s).toInt() + 1
            for (col in 0..cols) {
                for (row in 0..rows) {
                    drawCircle(
                        color = Color(0xFF0A192F).copy(alpha = 0.25f),
                        radius = 1.dp.toPx(),
                        center = Offset(col * s, row * s)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp, vertical = 40.dp)
        ) {
            // ── Passenger row ──
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(28.dp),
                            color = Color(0xFF0A192F).copy(alpha = 0.08f),
                            shape = CircleShape
                        ) {
                            Icon(
                                Icons.Default.Person, contentDescription = null,
                                tint = Color(0xFF0A192F).copy(alpha = 0.5f),
                                modifier = Modifier.padding(5.dp)
                            )
                        }
                        Text(
                            text = "PASSENGER",
                            style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 2.sp),
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = data.passengerName,
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF0A192F)
                    )
                }

                // Flight number pill
                Surface(
                    color = Color(0xFF0A192F).copy(alpha = 0.08f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "FLIGHT",
                            style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 2.sp),
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray
                        )
                        Text(
                            text = data.flightNumber,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF0A192F)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // ── Gold thin divider ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFFD4AF37).copy(alpha = 0.1f),
                                Color(0xFFD4AF37).copy(alpha = 0.7f),
                                Color(0xFFD4AF37).copy(alpha = 0.1f)
                            )
                        )
                    )
            )

            Spacer(modifier = Modifier.height(48.dp))

            // ── Route row ──
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Origin
                Column {
                    Text(
                        text = "FROM",
                        style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 2.sp),
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                    Text(
                        text = data.fromCode,
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black),
                        color = Color(0xFF0A192F)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = data.fromCity,
                        style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.sp),
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                }

                // Flight path — dashes + plane icon
                Box(
                    modifier = Modifier.weight(1f).padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxWidth().height(2.dp)) {
                        drawLine(
                            color = Color(0xFFD4AF37).copy(alpha = 0.5f),
                            start = Offset(0f, center.y),
                            end = Offset(size.width, center.y),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f),
                            strokeWidth = 1.5.dp.toPx()
                        )
                    }
                    Icon(
                        Icons.Default.AirplanemodeActive,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp).rotate(90f)
                    )
                }

                // Destination
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "TO",
                        style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 2.sp),
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                    Text(
                        text = data.toCode,
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black),
                        color = Color(0xFF0A192F)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = data.toCity,
                        style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.sp),
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // ── Stats row — phrases learned, level, language ──
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StatChip(
                    icon = Icons.Default.School,
                    label = "PHRASES LEARNED",
                    value = "${data.phraseCount}"
                )
                StatChip(
                    icon = Icons.Default.EmojiEvents,
                    label = "LEVEL REACHED",
                    value = data.levelAchieved
                )
                StatChip(
                    icon = Icons.Default.Translate,
                    label = "LANGUAGE",
                    value = data.language
                )
            }
        }
    }
}

@Composable
fun StatChip(icon: ImageVector, label: String, value: String) {
    Surface(
        color = MaterialTheme.colorScheme.background.copy(alpha = 0.06f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.background.copy(alpha = 0.8f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp),
                    color = Color.Gray
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.background
                )
            }
        }
    }
}

// ── TicketQRStub ──────────────────────────────────────────────────────────────

@Composable
fun TicketQRStub(data: LandingKitData) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(340.dp)
            .background(
                Color(0xFFF9F7F2),
                RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── QR 1 — Cheat Sheet ──
            QRCodeBlock(
                title = "REPORT & CHEAT SHEET",
                subtitle = "Scan to save all ${data.phraseCount} phrases\noffline on your phone",
                accentColor = Color(0xFFE91E3C)
            )

            // Gold divider between QRs
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Icon(
                    Icons.Default.AirplanemodeActive,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }

            // ── QR 2 — Certificate ──
            QRCodeBlock(
                title = "YOUR CERTIFICATE",
                subtitle = "You learned ${data.phraseCount} phrases in\n${data.language} — share it",
                accentColor = Color(0xFF673AB7)
            )
        }
    }
}

// ── QRCodeBlock ────────────────────────────────────────────────────────────────

@Composable
fun QRCodeBlock(
    title: String,
    subtitle: String,
    accentColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Label pill
        Surface(
            color = accentColor.copy(alpha = 0.08f),
            shape = CircleShape,
            border = BorderStroke(1.dp, accentColor.copy(alpha = 0.2f))
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = accentColor,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
            )
        }

        // QR frame — uses the actual image asset
        // Replace R.drawable.qr_code with your actual drawable resource name
        Surface(
            modifier = Modifier.size(148.dp),
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(2.dp, accentColor.copy(alpha = 1f)),
            shadowElevation = 4.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painterResource(R.drawable.passenger_report_qr),
                    contentDescription = "qr_code"
                )
            }
        }

        // Subtitle
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelLarge.copy(lineHeight = 16.sp),
            color = Color.Gray,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

// ── TicketDataField ───────────────────────────────────────────────────────────

@Composable
fun TicketDataField(label: String, value: String, valueColor: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 2.sp),
            color = Color.White.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = valueColor
        )
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(widthDp = 1920, heightDp = 1080)
@Composable
fun LandingKitScreenPreview() {
    SkyCoachTheme {
        LandingKitScreen {}
    }
}