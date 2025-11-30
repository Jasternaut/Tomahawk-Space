package com.example.tomahawkspace.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String? = null, val icon: ImageVector? = null) {
    object Welcome : Screen("welcome")
    
    object Home : Screen("home", "Главная", Icons.Default.Home)
    object Gallery : Screen("gallery", "Галерея", Icons.Default.PhotoLibrary)
    object Settings : Screen("settings", "Настройки", Icons.Default.Settings)

    // Вложенные экраны настроек
    object ApiSettings : Screen("settings/api", "NASA API Ключ")
    object AppearanceSettings : Screen("settings/appearance", "Оформление")
    object ProxySettings : Screen("settings/proxy", "Прокси")

    // Экран деталей
    object Details : Screen("details", "Детали")
}

// Используем функцию, чтобы гарантировать инициализацию объектов Screen при вызове
fun getBottomNavItems(): List<Screen> = listOf(
    Screen.Home,
    Screen.Gallery,
    Screen.Settings
)
