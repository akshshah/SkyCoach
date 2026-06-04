package com.example.skycoach.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skycoach.R
import com.example.skycoach.data.AllDestinations
import com.example.skycoach.data.DestMadrid
import com.example.skycoach.ui.components.FlightRouteWidget
import com.example.skycoach.ui.components.GlassCard
import com.example.skycoach.ui.components.TimeToDestination

@Composable
fun WelcomeScreen(
    onStartOnboarding: () -> Unit
) {
    var selectedDest by remember { mutableStateOf(DestMadrid) }
    var showDropdown by remember { mutableStateOf(false) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Dynamic Background (Deeper/More Immersive)
        AnimatedContent(
            targetState = selectedDest,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "BackgroundTransition"
        ) { dest ->
            val background = when(dest.language){
                "Spanish" -> {
                    R.drawable.spanish_background
                }
                "Japanese" -> {
                    R.drawable.japanese_background
                }
                "French" -> {
                    R.drawable.france_background
                }
                "German" -> {
                    R.drawable.germany_background
                }
                else -> {
                    R.drawable.spanish_background
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    alpha = 0.45f,
                    painter = painterResource(background),
                    contentDescription = dest.language
                )
            }
        }

        // Top Branding
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 100.dp, vertical = 56.dp),
        ) {
            Text(
                text = "SKYCOACH",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 8.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Text(
                text = "Boarded a tourist. Landed a local.",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = MaterialTheme.colorScheme.onSurface
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
            // LEFT SIDE: THE ACTION HUB
            Column(
                modifier = Modifier.weight(1.2f),
                verticalArrangement = Arrangement.Center
            ) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Welcome,\nMr. Anderson",
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        // Integrated Language Selection Context
                        Column {
                            Text(
                                text = "You are landing in Madrid.",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Ready to start learning ",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                
                                // Interactive Language Selector within text
                                Box {
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { showDropdown = !showDropdown },
                                        color = Color.Black.copy(alpha = 0.6f),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = selectedDest.language,
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            )
                                            Icon(
                                                Icons.Default.KeyboardArrowDown,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    DropdownMenu(
                                        expanded = showDropdown,
                                        onDismissRequest = { showDropdown = false },
                                        modifier = Modifier
                                            .width(200.dp)
                                            .background(MaterialTheme.colorScheme.surface)
                                    ) {
                                        AllDestinations.forEach { dest ->
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        dest.language,
                                                        style = MaterialTheme.typography.labelLarge.copy(
                                                            fontWeight = FontWeight.Bold,
                                                        )
                                                    )
                                                },
                                                onClick = {
                                                    selectedDest = dest
                                                    showDropdown = false
                                                }
                                            )
                                        }
                                    }
                                }
                                
                                Text(
                                    text = "?",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(56.dp))
                        
                        // Unified Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            Button(
                                onClick = onStartOnboarding,
                                modifier = Modifier
                                    .height(72.dp)
                                    .weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = CircleShape,
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "START LEARNING",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            letterSpacing = 1.sp
                                        ),
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                                }
                            }

                            OutlinedButton(
                                onClick = { /* Resume Action */ },
                                modifier = Modifier
                                    .height(72.dp)
                                    .weight(1f),
                                border = BorderStroke(
                                    1.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                ),
                                shape = CircleShape,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "RESUME LAST SESSION",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            letterSpacing = 1.sp
                                        ),
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(100.dp))

            // RIGHT SIDE: AMBIENT FLIGHT INFO
            TimeToDestination(modifier = Modifier.weight(0.8f))
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(widthDp = 1920, heightDp = 1080)
@Composable
fun WelcomeScreenPreview() {
    com.example.skycoach.ui.theme.SkyCoachTheme {
        WelcomeScreen {}
    }
}
