package com.tomahawk.space.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.tomahawk.space.PlatformTheme
import com.tomahawk.space.data.AuthRepository

@Composable
fun TomahawkSpaceTheme(
    authRepository: AuthRepository? = null,
    appTheme: Int = 0,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val themeFromRepo = authRepository?.appTheme?.collectAsState(initial = appTheme)?.value ?: appTheme
    val dynamicFromRepo = authRepository?.dynamicColors?.collectAsState(initial = dynamicColor)?.value ?: dynamicColor

    val darkTheme = when (themeFromRepo) {
        1 -> false
        2 -> true
        else -> isSystemInDarkTheme()
    }

    PlatformTheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicFromRepo,
        content = content
    )
}