package com.example.skycoach.ui.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Onboarding : Screen("onboarding")
    object Generating : Screen("generating")
    object Learning : Screen("learning")
    object Quiz : Screen("quiz")
    object LandingKit : Screen("landing_kit")
}
