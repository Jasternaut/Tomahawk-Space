package com.tomahawk.space.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.tomahawk.space.R

val Geologica = FontFamily(
    Font(R.font.geologica_bold, FontWeight.Bold),
    Font(R.font.geologica_bold_italic,
        FontWeight.Bold, FontStyle.Italic)
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = Geologica,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    )
)