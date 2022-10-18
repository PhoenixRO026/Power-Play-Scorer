package com.phoenix.powerplayscorer.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight

// Set of Material typography styles to start with
val Typography = Typography(
    headlineLarge = Typography().headlineLarge.copy(
        fontWeight = FontWeight.Medium
    ),
    headlineMedium = Typography().headlineMedium.copy(
        fontWeight = FontWeight.Medium
    ),
    headlineSmall = Typography().headlineSmall.copy(
        fontWeight = FontWeight.Medium
    ),
    titleMedium = Typography().titleMedium.copy(
        fontWeight = Typography().titleLarge.fontWeight
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)