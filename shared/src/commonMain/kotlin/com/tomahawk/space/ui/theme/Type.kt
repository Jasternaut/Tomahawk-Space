package com.tomahawk.space.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import tomahawkspace.shared.generated.resources.Res
import tomahawkspace.shared.generated.resources.geologica_bold
import tomahawkspace.shared.generated.resources.geologica_bold_italic

@Composable
fun getGeologicaFontFamily(): FontFamily {
    return FontFamily(
        Font(Res.font.geologica_bold, FontWeight.Bold),
        Font(Res.font.geologica_bold_italic, FontWeight.Bold, FontStyle.Italic)
    )
}

@Composable
fun Typography(): Typography {
    return Typography(
        displayLarge = TextStyle(
            fontFamily = getGeologicaFontFamily(),
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp
        )
    )
}