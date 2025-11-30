package com.example.tomahawkspace.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tomahawkspace.ui.screens.gallery.DetailsScreen
import com.example.tomahawkspace.ui.screens.gallery.GalleryScreen
import com.example.tomahawkspace.ui.screens.home.HomeScreen
import com.example.tomahawkspace.ui.screens.settings.ApiSettingsScreen
import com.example.tomahawkspace.ui.screens.settings.AppearanceSettingsScreen
import com.example.tomahawkspace.ui.screens.settings.SettingsScreen
import com.example.tomahawkspace.ui.screens.settings.SettingsViewModel
import com.example.tomahawkspace.ui.screens.welcome.WelcomeScreen
import com.example.tomahawkspace.ui.screens.welcome.WelcomeViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Welcome.route) {
            val viewModel: WelcomeViewModel = hiltViewModel()
            WelcomeScreen(
                onKeySaved = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                viewModel = viewModel
            )
        }
        composable(Screen.Home.route) {
            HomeScreen()
        }
        composable(Screen.Gallery.route) {
            GalleryScreen(navController = navController)
        }
        composable(Screen.Details.route + "/{date}") {
            DetailsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
        composable(Screen.ApiSettings.route) {
            val viewModel: SettingsViewModel = hiltViewModel()
            ApiSettingsScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.AppearanceSettings.route) {
            val viewModel: SettingsViewModel = hiltViewModel()
            AppearanceSettingsScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
