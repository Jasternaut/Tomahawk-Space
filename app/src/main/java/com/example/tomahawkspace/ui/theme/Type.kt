package com.example.tomahawkspace.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.tomahawkspace.R

val Geologica = FontFamily(
    Font(R.font.geologica_medium, FontWeight.Medium),
    Font(R.font.geologica_semibold_italic, FontWeight.SemiBold) // Нам нужен semibold italic
    // Можно добавить regular, если он есть
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)
