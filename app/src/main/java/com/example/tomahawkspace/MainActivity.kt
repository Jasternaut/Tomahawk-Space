package com.example.tomahawkspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.tomahawkspace.data.local.AppTheme
import com.example.tomahawkspace.data.local.SettingsManager
import com.example.tomahawkspace.ui.MainScreen
import com.example.tomahawkspace.ui.navigation.Screen
import com.example.tomahawkspace.ui.theme.TomahawkSpaceTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Определяем стартовый экран до отрисовки UI
        val startDestination = runBlocking {
            val apiKey = settingsManager.apiKey.first()
            if (apiKey.isNullOrBlank()) Screen.Welcome.route else Screen.Home.route
        }

        setContent {
            val themeMode by settingsManager.themeMode.collectAsState(initial = AppTheme.SYSTEM)
            val dynamicColors by settingsManager.dynamicColors.collectAsState(initial = true)

            val darkTheme = when (themeMode) {
                AppTheme.SYSTEM -> isSystemInDarkTheme()
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
            }

            TomahawkSpaceTheme(
                darkTheme = darkTheme,
                dynamicColor = dynamicColors
            ) {
                MainScreen(startDestination = startDestination)
            }
        }
    }
}
