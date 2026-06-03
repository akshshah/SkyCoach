package com.example.skycoach.data

import androidx.compose.ui.graphics.Color

data class Destination(
    val city: String,
    val code: String,
    val language: String,
    val country: String,
    val mainColor: Color
)

val DestMadrid = Destination(
    city = "MADRID",
    code = "MAD",
    language = "Spanish",
    country = "Spain",
    mainColor = Color(0xFFD4AF37) // Gold/Amber
)

val DestTokyo = Destination(
    city = "TOKYO",
    code = "HND",
    language = "Japanese",
    country = "Japan",
    mainColor = Color(0xFF64FFDA) // Sky Blue/Teal
)

val DestParis = Destination(
    city = "PARIS",
    code = "CDG",
    language = "French",
    country = "France",
    mainColor = Color(0xFF625b71) // Lavender/Purple
)

val DestBerlin = Destination(
    city = "BERLIN",
    code = "BER",
    language = "German",
    country = "Germany",
    mainColor = Color(0xFF90A4AE) // Blue Gray
)

val AllDestinations = listOf(DestMadrid, DestTokyo, DestParis, DestBerlin)
