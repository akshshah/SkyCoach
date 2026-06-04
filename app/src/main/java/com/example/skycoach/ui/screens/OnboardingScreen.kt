package com.example.skycoach.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skycoach.R
import com.example.skycoach.data.DestMadrid
import com.example.skycoach.ui.components.FlightRouteWidget
import com.example.skycoach.ui.components.GlassCard
import com.example.skycoach.ui.components.SelectionTile
import com.example.skycoach.ui.components.TimeToDestination

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit
) {
    var currentStep by remember { mutableStateOf(1) }
    val totalSteps = 3
    
    // Selection States
    var selectedLevel by remember { mutableStateOf("") }
    var selectedPurpose by remember { mutableStateOf("") }
    val selectedActivities = remember { mutableStateListOf<String>() }

    Box(modifier = Modifier.fillMaxSize()) {
        // Cinematic Blend Background
        Box(modifier = Modifier.fillMaxSize()) {
            // Right Side Image
            Image(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .align(Alignment.CenterEnd),
                alpha = 0.5f,
                painter = painterResource(R.drawable.spanish_background),
                contentDescription = "Madrid"
            )
            
            // Left Side Solid Color & Gradient Blend
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
            
            // Overall Radial Glow for depth
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
                .padding(horizontal = 100.dp, vertical = 80.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // LEFT SIDE: SELECTION HUB
            Column(
                modifier = Modifier.weight(1.2f),
                verticalArrangement = Arrangement.Center
            ) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Progress Indicator
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            repeat(totalSteps) { step ->
                                Box(
                                    modifier = Modifier
                                        .height(4.dp)
                                        .weight(1f)
                                        .clip(CircleShape)
                                        .background(
                                            if (step + 1 <= currentStep) MaterialTheme.colorScheme.primary 
                                            else Color.White.copy(alpha = 0.1f)
                                        )
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(40.dp))

                        // Question Title
                        AnimatedContent(
                            targetState = currentStep,
                            transitionSpec = {
                                fadeIn() + slideInHorizontally { it / 2 } togetherWith fadeOut() + slideOutHorizontally { -it / 2 }
                            },
                            label = "QuestionTransition"
                        ) { step ->
                            val (title, subtitle) = when(step) {
                                1 -> "What is your current language level?" to "We'll tailor the vocabulary to your expertise."
                                2 -> "What is the purpose of your trip?" to "This helps us prioritize situational phrases."
                                3 -> "What are your top activities?" to "Select up to 3 to build your landing kit."
                                else -> "" to ""
                            }
                            Column {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = subtitle,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(48.dp))

                        // Selection Area
                        Box(modifier = Modifier.height(440.dp)) {
                            AnimatedContent(
                                targetState = currentStep,
                                transitionSpec = {
                                    fadeIn() togetherWith fadeOut()
                                },
                                label = "SelectionTransition"
                            ) { step ->
                                when(step) {
                                    1 -> LevelSelection(selectedLevel) { selectedLevel = it }
                                    2 -> PurposeSelection(selectedPurpose) { selectedPurpose = it }
                                    3 -> ActivitySelection(selectedActivities)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Action Button
                        Button(
                            onClick = {
                                if (currentStep < totalSteps) {
                                    currentStep++
                                } else {
                                    onComplete()
                                }
                            },
                            modifier = Modifier
                                .height(72.dp)
                                .align(Alignment.End)
                                .width(240.dp),
                            enabled = when(currentStep) {
                                1 -> selectedLevel.isNotEmpty()
                                2 -> selectedPurpose.isNotEmpty()
                                3 -> selectedActivities.isNotEmpty()
                                else -> false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = CircleShape
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (currentStep == totalSteps) "FINISH" else "NEXT",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 2.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(100.dp))

            TimeToDestination(modifier = Modifier.weight(0.8f))
        }
    }
}

@Composable
fun LevelSelection(selected: String, onSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SelectionTile("🌱 Grounded", selected == "Grounded", { onSelect("Grounded") }, subtitle = "Survival basics (Yes, No, Help)")
        SelectionTile("✈ Airborne", selected == "Airborne", { onSelect("Airborne") }, subtitle = "Situational phrases (Ordering food, Check-in)")
        SelectionTile("🏙 Local", selected == "Local", { onSelect("Local") }, subtitle = "Advanced communication (Requests, Negotiation)")
        SelectionTile("🌟 Native Soul", selected == "Native", { onSelect("Native") }, subtitle = "Expert level (Slang, Regional nuances)")
    }
}

@Composable
fun PurposeSelection(selected: String, onSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SelectionTile("Business", selected == "Business", { onSelect("Business") }, icon = Icons.Default.BusinessCenter)
        SelectionTile("Leisure", selected == "Leisure", { onSelect("Leisure") }, icon = Icons.Default.BeachAccess)
        SelectionTile("Family Visit", selected == "Family", { onSelect("Family") }, icon = Icons.Default.People)
        SelectionTile("Study Abroad", selected == "Study", { onSelect("Study") }, icon = Icons.Default.School)
        SelectionTile("Relocation", selected == "Relocation", { onSelect("Relocation") }, icon = Icons.Default.Home)
    }
}

@Composable
fun ActivitySelection(selected: MutableList<String>) {
    val activities = listOf(
        "Fine Dining" to Icons.Default.Restaurant,
        "Hiking" to Icons.Default.Terrain,
        "Sightseeing" to Icons.Default.CameraAlt,
        "Business Meetings" to Icons.Default.Groups,
        "Shopping" to Icons.Default.ShoppingBag,
        "Local Culture" to Icons.Default.TheaterComedy,
        "Nightlife" to Icons.Default.Nightlife,
        "Art & Museums" to Icons.Default.Palette,
        "Wellness & Spa" to Icons.Default.Spa,
        "Sports Events" to Icons.Default.EmojiEvents
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        activities.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { (name, icon) ->
                    SelectionTile(
                        title = name,
                        isSelected = selected.contains(name),
                        onClick = {
                            if (selected.contains(name)) selected.remove(name)
                            else if (selected.size < 3) selected.add(name)
                        },
                        icon = icon,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(widthDp = 1920, heightDp = 1080)
@Composable
fun OnboardingScreenPreview() {
    com.example.skycoach.ui.theme.SkyCoachTheme {
        OnboardingScreen {}
    }
}
