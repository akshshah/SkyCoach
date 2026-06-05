package com.example.skycoach.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

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

enum class FlashcardType { STANDARD, DIVIDER, TIP }

data class Flashcard(
    val id: Int,
    val type: FlashcardType = FlashcardType.STANDARD,
    val target: String = "",
    val phonetic: String = "",
    val translation: String = "",
    val example: String = "",
    val highlightedWord: String = "",
    val audioUrl: String = "",
    val audioSlowUrl: String = "",
    val category: String = "",
    val categoryIcon: ImageVector? = null,
    val tip: String = ""
)

val MadridDeck = listOf(
    Flashcard(id = 1, type = FlashcardType.DIVIDER, category = "Dining Out", categoryIcon = Icons.Default.Restaurant),
    Flashcard(id = 2, target = "La cuenta, por favor", phonetic = "lah kwehn-tah, por fah-bor", translation = "The bill, please", example = "When you finish your meal, say: La cuenta, por favor.", highlightedWord = "La cuenta, por favor."),
    Flashcard(id = 3, target = "Una mesa para dos", phonetic = "oo-nah meh-sah pah-rah dohs", translation = "A table for two", example = "Arriving at a restaurant: Una mesa para dos.", highlightedWord = "Una mesa para dos."),
    Flashcard(id = 4, type = FlashcardType.TIP, tip = "In Spain, dinner usually starts after 9 PM. Don't be surprised if restaurants are empty at 7 PM!"),
    Flashcard(id = 5, type = FlashcardType.DIVIDER, category = "Business Etiquette", categoryIcon = Icons.Default.Work),
    Flashcard(id = 6, target = "Mucho gusto", phonetic = "moo-choh goos-toh", translation = "Pleasure to meet you", example = "When meeting a business partner: Mucho gusto.", highlightedWord = "Mucho gusto"),
    Flashcard(id = 7, target = "¿Podemos empezar?", phonetic = "poh-deh-mohs ehm-peh-thar", translation = "Can we start?", example = "At the beginning of a meeting: ¿Podemos empezar?", highlightedWord = "¿Podemos empezar?")
)