package com.tomahawk.space.ui.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tomahawk.space.data.ApodRepository
import com.tomahawk.space.data.AuthRepository
import com.tomahawk.space.data.GalleryRepository
import com.tomahawk.space.ui.screens.auth.AuthScreen
import com.tomahawk.space.ui.screens.auth.AuthViewModel
import com.tomahawk.space.ui.screens.gallery.GalleryDetailScreen
import com.tomahawk.space.ui.screens.gallery.GalleryDetailViewModel
import com.tomahawk.space.ui.screens.gallery.GalleryScreen
import com.tomahawk.space.ui.screens.gallery.GalleryViewModel
import com.tomahawk.space.ui.screens.main.MainScreen
import com.tomahawk.space.ui.screens.main.MainViewModel
import com.tomahawk.space.ui.screens.settings.ApiSettingsScreen
import com.tomahawk.space.ui.screens.settings.GeneralSettingsScreen
import com.tomahawk.space.ui.screens.settings.SettingsMainScreen
import com.tomahawk.space.ui.screens.settings.SettingsViewModel
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import tomahawkspace.shared.generated.resources.Res
import tomahawkspace.shared.generated.resources.nav_gallery
import tomahawkspace.shared.generated.resources.nav_general
import tomahawkspace.shared.generated.resources.nav_settings

sealed class Screen(val route: String, val labelRes: StringResource, val icon: ImageVector) {
    object General : Screen("general", Res.string.nav_general, Icons.Default.Home)
    object Gallery : Screen("gallery", Res.string.nav_gallery, Icons.Default.Menu)
    object Settings : Screen("settings", Res.string.nav_settings, Icons.Default.Settings)
}

@Composable
fun RootNavigation(
    authRepository: AuthRepository,
    apodRepository: ApodRepository,
    galleryRepository: GalleryRepository
) {
    val navController = rememberNavController()
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val key = authRepository.getKey()
        startDestination = if (!key.isNullOrBlank()) "main" else "auth"
    }

    val currentStartDest = startDestination ?: return

    NavHost(
        navController = navController,
        startDestination = currentStartDest
    ) {
        composable("auth") {
            val authViewModel = viewModel { AuthViewModel(authRepository) }
            AuthScreen(
                viewModel = authViewModel,
                onAuthorized = {
                    navController.navigate("main") {
                        popUpTo("auth") {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable("main") {
            MainAppNavigation(
                authRepository = authRepository,
                apodRepository = apodRepository,
                galleryRepository = galleryRepository
            )
        }
    }
}

@Composable
fun MainAppNavigation(
    authRepository: AuthRepository,
    apodRepository: ApodRepository,
    galleryRepository: GalleryRepository
) {
    val navController = rememberNavController()
    val isFloatingNavBar by authRepository.isFloatingNavBar.collectAsState(initial = true)

    val items = listOf(
        Screen.General,
        Screen.Gallery,
        Screen.Settings
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route
    val showNavBar = currentRoute in items.map { it.route }

    val currentScreen = items.find { it.route == currentRoute } ?: Screen.General

    TomahawkMainScaffold(
        isFloatingNavBar = isFloatingNavBar,
        showNavBar = showNavBar,
        currentScreen = currentScreen,
        items = items,
        onScreenSelected = { screen ->
            navController.navigate(screen.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.General.route,
                enterTransition = {
                    val initialIndex = items.indexOfFirst { it.route == initialState.destination.route }
                    val targetIndex = items.indexOfFirst { it.route == targetState.destination.route }

                    if (initialIndex != -1 && targetIndex != -1 && initialIndex != targetIndex) {
                        if (targetIndex > initialIndex) {
                            slideInHorizontally(
                                initialOffsetX = { fullWidth -> fullWidth },
                                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
                            )
                        } else {
                            slideInHorizontally(
                                initialOffsetX = { fullWidth -> -fullWidth },
                                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
                            )
                        }
                    } else {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
                        )
                    }
                },
                exitTransition = {
                    val initialIndex = items.indexOfFirst { it.route == initialState.destination.route }
                    val targetIndex = items.indexOfFirst { it.route == targetState.destination.route }

                    if (initialIndex != -1 && targetIndex != -1 && initialIndex != targetIndex) {
                        if (targetIndex > initialIndex) {
                            scaleOut(
                                targetScale = 0.88f,
                                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
                            ) + slideOutHorizontally(
                                targetOffsetX = { fullWidth -> -fullWidth / 6 },
                                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
                            )
                        } else {
                            scaleOut(
                                targetScale = 0.88f,
                                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
                            ) + slideOutHorizontally(
                                targetOffsetX = { fullWidth -> fullWidth / 6 },
                                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
                            )
                        }
                    } else {
                        scaleOut(
                            targetScale = 0.88f,
                            animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
                        ) + slideOutHorizontally(
                            targetOffsetX = { fullWidth -> -fullWidth / 6 },
                            animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
                        )
                    }
                },
                popEnterTransition = {
                    val initialIndex = items.indexOfFirst { it.route == initialState.destination.route }
                    val targetIndex = items.indexOfFirst { it.route == targetState.destination.route }

                    if (initialIndex != -1 && targetIndex != -1 && initialIndex != targetIndex) {
                        if (targetIndex > initialIndex) {
                            scaleIn(
                                initialScale = 0.88f,
                                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
                            ) + slideInHorizontally(
                                initialOffsetX = { fullWidth -> fullWidth / 6 },
                                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
                            )
                        } else {
                            scaleIn(
                                initialScale = 0.88f,
                                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
                            ) + slideInHorizontally(
                                initialOffsetX = { fullWidth -> -fullWidth / 6 },
                                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
                            )
                        }
                    } else {
                        scaleIn(
                            initialScale = 0.88f,
                            animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
                        ) + slideInHorizontally(
                            initialOffsetX = { fullWidth -> -fullWidth / 6 },
                            animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
                        )
                    }
                },
                popExitTransition = {
                    val initialIndex = items.indexOfFirst { it.route == initialState.destination.route }
                    val targetIndex = items.indexOfFirst { it.route == targetState.destination.route }

                    if (initialIndex != -1 && targetIndex != -1 && initialIndex != targetIndex) {
                        if (targetIndex > initialIndex) {
                            slideOutHorizontally(
                                targetOffsetX = { fullWidth -> -fullWidth },
                                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
                            )
                        } else {
                            slideOutHorizontally(
                                targetOffsetX = { fullWidth -> fullWidth },
                                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
                            )
                        }
                    } else {
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
                        )
                    }
                }
            ) {
                composable(Screen.General.route) {
                    AnimatedCardWrapper {
                        val mainViewModel: MainViewModel = viewModel {
                            MainViewModel(authRepository, apodRepository, galleryRepository)
                        }
                        MainScreen(
                            viewModel = mainViewModel,
                            contentPadding = contentPadding
                        )
                    }
                }
                composable(Screen.Gallery.route) {
                    AnimatedCardWrapper {
                        val galleryViewModel: GalleryViewModel = viewModel {
                            GalleryViewModel(galleryRepository)
                        }
                        GalleryScreen(
                            viewModel = galleryViewModel,
                            onNavigateToDetail = { date ->
                                navController.navigate("gallery_detail/$date")
                            }
                        )
                    }
                }
                composable(
                    route = "gallery_detail/{date}",
                    arguments = listOf(navArgument("date") { type = NavType.StringType })
                ) { backStackEntry ->
                    AnimatedCardWrapper {
                        val date = backStackEntry.savedStateHandle.get<String>("date") ?: ""
                        val detailViewModel: GalleryDetailViewModel = viewModel {
                            GalleryDetailViewModel(date, galleryRepository, apodRepository, authRepository)
                        }
                        GalleryDetailScreen(
                            viewModel = detailViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
                composable(Screen.Settings.route) {
                    AnimatedCardWrapper {
                        val settingsViewModel: SettingsViewModel = viewModel {
                            SettingsViewModel(authRepository)
                        }
                        SettingsMainScreen(
                            viewModel = settingsViewModel,
                            onNavigateToGeneral = {
                                navController.navigate("settings_general")
                            },
                            onNavigateToApi = {
                                navController.navigate("settings_api")
                            }
                        )
                    }
                }
                composable("settings_general") {
                    AnimatedCardWrapper {
                        val settingsViewModel: SettingsViewModel = viewModel {
                            SettingsViewModel(authRepository)
                        }
                        GeneralSettingsScreen(
                            viewModel = settingsViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
                composable("settings_api") {
                    AnimatedCardWrapper {
                        val settingsViewModel: SettingsViewModel = viewModel {
                            SettingsViewModel(authRepository)
                        }
                        ApiSettingsScreen(
                            viewModel = settingsViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TomahawkMainScaffold(
    isFloatingNavBar: Boolean,
    showNavBar: Boolean,
    currentScreen: Screen,
    items: List<Screen>,
    onScreenSelected: (Screen) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Scaffold(
            bottomBar = {
                if (showNavBar && !isFloatingNavBar) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ) {
                        items.forEach { screen ->
                            NavigationBarItem(
                                selected = currentScreen == screen,
                                onClick = { onScreenSelected(screen) },
                                icon = { Icon(screen.icon, contentDescription = stringResource(screen.labelRes)) },
                                label = { Text(stringResource(screen.labelRes)) }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            val contentPadding = if (showNavBar && isFloatingNavBar) {
                PaddingValues(
                    top = innerPadding.calculateTopPadding(),
                    bottom = 104.dp
                )
            } else {
                innerPadding
            }

            content(contentPadding)
        }

        if (showNavBar && isFloatingNavBar) {
            FloatingNavigationCapsule(
                currentScreen = currentScreen,
                items = items,
                onScreenSelected = onScreenSelected,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp, vertical = 20.dp)
                    .navigationBarsPadding()
            )
        }
    }
}

@Composable
fun FloatingNavigationCapsule(
    currentScreen: Screen,
    items: List<Screen>,
    onScreenSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(68.dp)
            .fillMaxWidth(),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f),
        tonalElevation = 6.dp,
        shadowElevation = 12.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { screen ->
                val isSelected = currentScreen == screen
                val pillColor = if (isSelected) {
                    MaterialTheme.colorScheme.secondaryContainer
                } else {
                    Color.Transparent
                }
                val contentColor = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(0.82f)
                        .clip(CircleShape)
                        .background(pillColor)
                        .clickable { onScreenSelected(screen) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = stringResource(screen.labelRes),
                            tint = contentColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = stringResource(screen.labelRes),
                            style = MaterialTheme.typography.labelSmall,
                            color = contentColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedContentScope.AnimatedCardWrapper(
    content: @Composable () -> Unit
) {
    val isAnimating = transition.isRunning
    val targetRadius = if (isAnimating) 20.dp else 0.dp

    val animatedRadius by animateDpAsState(
        targetValue = targetRadius,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "cardCornerAnimation"
    )

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                shape = RoundedCornerShape(animatedRadius)
                clip = true
            },
        color = MaterialTheme.colorScheme.background
    ) {
        content()
    }
}
