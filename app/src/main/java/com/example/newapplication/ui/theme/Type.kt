package com.example.newapplication.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.newapplication.R

// 1. Define the Font Families mapped to your res/font files
val RajdhaniFont = FontFamily(
    Font(R.font.rajdhani_bold, FontWeight.Bold)
)

val InterFont = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium)
)

// 2. Configure Material 3 Typography tokens
val Typography = Typography(
    // Large score/bet tracking numbers
    displayLarge = TextStyle(
        fontFamily = RajdhaniFont,
        fontWeight = FontWeight.Bold,
        fontSize = 50.sp
    ),
    // Screen Titles and Weather Events
    headlineMedium = TextStyle(
        fontFamily = RajdhaniFont,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp
    ),
    // Main read blocks / card content
    bodyLarge = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    // Smaller descriptions / subtext
    bodyMedium = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    // Interactive components (Button labels, tab bars)
    labelLarge = TextStyle(
        fontFamily = InterFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    )
)