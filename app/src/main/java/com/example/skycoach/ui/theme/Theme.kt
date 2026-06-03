package com.example.skycoach.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Premium Light Color Scheme (Celestial Midnight & Gold)
// Note: We use lightColorScheme but with dark backgrounds for that "Dark Mode" premium feel by default.
private val PremiumColorScheme = lightColorScheme(
    primary = Gold500,
    onPrimary = Navy900,
    primaryContainer = Gold700,
    onPrimaryContainer = OffWhite,
    
    secondary = SkyBlue,
    onSecondary = Navy900,
    
    background = Navy900,
    onBackground = OffWhite,
    
    surface = Navy800,
    onSurface = OffWhite,
    surfaceVariant = Navy800,
    onSurfaceVariant = LightGray,
    
    outline = Gold300,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun SkyCoachTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PremiumColorScheme,
        typography = Typography,
        content = content
    )
}
