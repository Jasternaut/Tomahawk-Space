package com.tomahawk.space.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tomahawk.space.R
import com.tomahawk.space.data.ApodRepository
import com.tomahawk.space.data.AuthRepository
import com.tomahawk.space.ui.screens.main.MainScreen
import com.tomahawk.space.ui.screens.main.MainViewModel
import com.tomahawk.space.ui.screens.main.MainViewModelFactory
import com.tomahawk.space.ui.screens.settings.SettingsScreen
import com.tomahawk.space.ui.screens.settings.SettingsViewModel
import com.tomahawk.space.ui.screens.settings.SettingsViewModelFactory

sealed class Screen(val route: String, val labelRes: Int, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object General : Screen("general", R.string.nav_general, Icons.Default.Home)
    object Gallery : Screen("gallery", R.string.nav_gallery, Icons.Default.Menu)
    object Settings : Screen("settings", R.string.nav_settings, Icons.Default.Settings)
}

@Composable
fun RootNavigation(
    authRepository: AuthRepository,
    apodRepository: ApodRepository
) {
    val navController = rememberNavController()
    val items = listOf(
        Screen.General,
        Screen.Gallery,
        Screen.Settings
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(stringResource(screen.labelRes)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.General.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.General.route) {
                val viewModel: MainViewModel = viewModel(
                    factory = MainViewModelFactory(authRepository, apodRepository)
                )
                MainScreen(viewModel)
            }
            composable(Screen.Gallery.route) {
                PlaceholderScreen(stringResource(R.string.nav_gallery))
            }
            composable(Screen.Settings.route) {
                val viewModel: SettingsViewModel = viewModel(
                    factory = SettingsViewModelFactory(authRepository)
                )
                SettingsScreen(viewModel)
            }
        }
    }
}

@Composable
fun PlaceholderScreen(name: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = name)
    }
}
