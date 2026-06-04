package com.example.skycoach

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.skycoach.ui.navigation.Screen
import com.example.skycoach.ui.screens.OnboardingScreen
import com.example.skycoach.ui.screens.WelcomeScreen
import com.example.skycoach.ui.theme.SkyCoachTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Force Landscape for Tablet Demo
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        
        enableEdgeToEdge()
        setContent {
            SkyCoachTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    SkyCoachAppNavigation()
                }
            }
        }
    }
}

@Composable
fun SkyCoachAppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen {
                navController.navigate(Screen.Onboarding.route)
            }
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen {
                navController.navigate(Screen.Generating.route)
            }
        }
        composable(Screen.Generating.route) {
            // Screen to be implemented
        }
        composable(Screen.Learning.route) {
            // Screen to be implemented
        }
        composable(Screen.Quiz.route) {
            // Screen to be implemented
        }
        composable(Screen.LandingKit.route) {
            // Screen to be implemented
        }
    }
}
