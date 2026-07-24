package com.tomahawk.space.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tomahawk.space.R
import com.tomahawk.space.data.ApodRepository
import com.tomahawk.space.data.AuthRepository
import com.tomahawk.space.data.GalleryRepository
import com.tomahawk.space.ui.screens.gallery.GalleryDetailScreen
import com.tomahawk.space.ui.screens.gallery.GalleryDetailViewModel
import com.tomahawk.space.ui.screens.gallery.GalleryDetailViewModelFactory
import com.tomahawk.space.ui.screens.gallery.GalleryScreen
import com.tomahawk.space.ui.screens.gallery.GalleryViewModel
import com.tomahawk.space.ui.screens.gallery.GalleryViewModelFactory
import com.tomahawk.space.ui.screens.main.MainScreen
import com.tomahawk.space.ui.screens.main.MainViewModel
import com.tomahawk.space.ui.screens.main.MainViewModelFactory
import com.tomahawk.space.ui.screens.settings.ApiSettingsScreen
import com.tomahawk.space.ui.screens.settings.SettingsMainScreen
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
    apodRepository: ApodRepository,
    galleryRepository: GalleryRepository
) {
    val navController = rememberNavController()
    val items = listOf(
        Screen.General,
        Screen.Gallery,
        Screen.Settings
    )

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val showBottomBar = currentDestination?.route in items.map { it.route }

            if (showBottomBar) {
                NavigationBar {
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
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.General.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.General.route) {
                val viewModel: MainViewModel = viewModel(
                    factory = MainViewModelFactory(authRepository, apodRepository, galleryRepository)
                )
                MainScreen(viewModel)
            }
            composable(Screen.Gallery.route) {
                val viewModel: GalleryViewModel = viewModel(
                    factory = GalleryViewModelFactory(galleryRepository)
                )
                GalleryScreen(
                    viewModel = viewModel,
                    onNavigateToDetail = { date ->
                        navController.navigate("gallery_detail/$date")
                    }
                )
            }
            composable(
                route = "gallery_detail/{date}",
                arguments = listOf(navArgument("date") { type = NavType.StringType }),
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = tween(400)
                    ) + fadeIn(animationSpec = tween(400))
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = tween(400)
                    ) + fadeOut(animationSpec = tween(400))
                },
                popEnterTransition = {
                    fadeIn(animationSpec = tween(400))
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = tween(400)
                    ) + fadeOut(animationSpec = tween(400))
                }
            ) { backStackEntry ->
                val date = backStackEntry.arguments?.getString("date") ?: ""
                val viewModel: GalleryDetailViewModel = viewModel(
                    factory = GalleryDetailViewModelFactory(date, galleryRepository)
                )
                GalleryDetailScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Settings.route) {
                SettingsMainScreen(
                    onNavigateToApi = {
                        navController.navigate("settings_api")
                    }
                )
            }
            composable(
                route = "settings_api",
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = tween(400)
                    ) + fadeIn(animationSpec = tween(400))
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = tween(400)
                    ) + fadeOut(animationSpec = tween(400))
                },
                popEnterTransition = {
                    fadeIn(animationSpec = tween(400))
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = tween(400)
                    ) + fadeOut(animationSpec = tween(400))
                }
            ) {
                val viewModel: SettingsViewModel = viewModel(
                    factory = SettingsViewModelFactory(authRepository)
                )
                ApiSettingsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
