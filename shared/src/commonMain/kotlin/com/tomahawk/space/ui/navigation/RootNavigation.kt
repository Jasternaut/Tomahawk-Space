package com.tomahawk.space.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tomahawk.space.getPlatform
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
val LocalFloatingNavInset = compositionLocalOf { 0.dp }

sealed class Screen(val route: String, val labelRes: StringResource, val icon: ImageVector) {
    object General : Screen("general", Res.string.nav_general, Icons.Default.Home)
    object Gallery : Screen("gallery", Res.string.nav_gallery, Icons.Default.Menu)
    object Settings : Screen("settings", Res.string.nav_settings, Icons.Default.Settings)
}

fun getRouteOrder(route: String?): Int {
    if (route == null) return 0
    return when {
        route == Screen.General.route -> 10
        route == Screen.Gallery.route -> 20
        route.startsWith("gallery_detail") -> 25
        route == Screen.Settings.route -> 30
        route == "settings_general" -> 31
        route == "settings_api" -> 32
        else -> 0
    }
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
    val isDesktop = remember { getPlatform().name.contains("Java") }

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
        isDesktop = isDesktop,
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
            val slideSpec = tween<IntOffset>(durationMillis = 320, easing = FastOutSlowInEasing)

            NavHost(
                navController = navController,
                startDestination = Screen.General.route,
                enterTransition = {
                    val initialOrder = getRouteOrder(initialState.destination.route)
                    val targetOrder = getRouteOrder(targetState.destination.route)

                    if (targetOrder >= initialOrder) {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = slideSpec
                        )
                    } else {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = slideSpec
                        )
                    }
                },
                exitTransition = {
                    val initialOrder = getRouteOrder(initialState.destination.route)
                    val targetOrder = getRouteOrder(targetState.destination.route)

                    if (targetOrder >= initialOrder) {
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = slideSpec
                        )
                    } else {
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> fullWidth },
                            animationSpec = slideSpec
                        )
                    }
                },
                popEnterTransition = {
                    val initialOrder = getRouteOrder(initialState.destination.route)
                    val targetOrder = getRouteOrder(targetState.destination.route)

                    if (targetOrder >= initialOrder) {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = slideSpec
                        )
                    } else {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = slideSpec
                        )
                    }
                },
                popExitTransition = {
                    val initialOrder = getRouteOrder(initialState.destination.route)
                    val targetOrder = getRouteOrder(targetState.destination.route)

                    if (targetOrder >= initialOrder) {
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = slideSpec
                        )
                    } else {
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> fullWidth },
                            animationSpec = slideSpec
                        )
                    }
                }
            ) {
                composable(Screen.General.route) {
                    AnimatedCardWrapper({
                        val mainViewModel: MainViewModel = viewModel {
                            MainViewModel(authRepository, apodRepository, galleryRepository)
                        }
                        MainScreen(
                            viewModel = mainViewModel,
                            contentPadding = contentPadding
                        )
                    })
                }
                composable(Screen.Gallery.route) {
                    AnimatedCardWrapper({
                        val galleryViewModel: GalleryViewModel = viewModel {
                            GalleryViewModel(galleryRepository)
                        }
                        GalleryScreen(
                            viewModel = galleryViewModel,
                            onNavigateToDetail = { date ->
                                navController.navigate("gallery_detail/$date")
                            }
                        )
                    })
                }
                composable(
                    route = "gallery_detail/{date}",
                    arguments = listOf(navArgument("date") { type = NavType.StringType })
                ) { backStackEntry ->
                    AnimatedCardWrapper({
                        val date = backStackEntry.savedStateHandle.get<String>("date") ?: ""
                        val detailViewModel: GalleryDetailViewModel = viewModel {
                            GalleryDetailViewModel(
                                date,
                                galleryRepository,
                                apodRepository,
                                authRepository
                            )
                        }
                        GalleryDetailScreen(
                            viewModel = detailViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    })
                }
                composable(Screen.Settings.route) {
                    AnimatedCardWrapper({
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
                    })
                }
                composable("settings_general") {
                    AnimatedCardWrapper({
                        val settingsViewModel: SettingsViewModel = viewModel {
                            SettingsViewModel(authRepository)
                        }
                        GeneralSettingsScreen(
                            viewModel = settingsViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    })
                }
                composable("settings_api") {
                    AnimatedCardWrapper({
                        val settingsViewModel: SettingsViewModel = viewModel {
                            SettingsViewModel(authRepository)
                        }
                        ApiSettingsScreen(
                            viewModel = settingsViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    })
                }
            }
        }
    }
}

@Composable
fun TomahawkMainScaffold(
    isFloatingNavBar: Boolean,
    isDesktop: Boolean,
    showNavBar: Boolean,
    currentScreen: Screen,
    items: List<Screen>,
    onScreenSelected: (Screen) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val density = LocalDensity.current
    var floatingNavBarHeight by remember { mutableStateOf(0.dp) }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (showNavBar && isDesktop) {
            NavigationRail(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier.fillMaxHeight()
            ) {
                Spacer(Modifier.height(16.dp))
                items.forEach { screen ->
                    val isSelected = currentScreen == screen
                    NavigationRailItem(
                        selected = isSelected,
                        onClick = { onScreenSelected(screen) },
                        icon = { Icon(screen.icon, contentDescription = stringResource(screen.labelRes)) },
                        label = { Text(stringResource(screen.labelRes)) }
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Scaffold(
                bottomBar = {
                    if (showNavBar && !isFloatingNavBar && !isDesktop) {
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
                val currentBottomInset = if (showNavBar && !isDesktop) {
                    if (isFloatingNavBar) floatingNavBarHeight else innerPadding.calculateBottomPadding()
                } else {
                    0.dp
                }

                CompositionLocalProvider(
                    LocalFloatingNavInset provides currentBottomInset
                ) {
                    content(innerPadding)
                }
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = showNavBar && !isDesktop && isFloatingNavBar,
                enter = slideInVertically { it },
                exit = slideOutVertically { it },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .onSizeChanged { size ->
                        floatingNavBarHeight = with(density) { size.height.toDp() }
                    }
            ) {
                FloatingNavigationDock(
                    items = items,
                    currentScreen = currentScreen,
                    onScreenSelected = onScreenSelected
                )
            }
        }
    }
}

@Composable
fun FloatingNavigationDock(
    items: List<Screen>,
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .shadow(
                elevation = 12.dp,
                shape = CircleShape,
                ambientColor = Color.Black.copy(alpha = 0.2f),
                spotColor = Color.Black.copy(alpha = 0.2f)
            )
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.88f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                shape = CircleShape
            ),
        shape = CircleShape,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .padding(6.dp)
                .animateContentSize(
                    animationSpec = spring(
                        stiffness = Spring.StiffnessMediumLow,
                        dampingRatio = Spring.DampingRatioLowBouncy
                    )
                ),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { screen ->
                val isSelected = currentScreen == screen
                FloatingDockItem(
                    screen = screen,
                    isSelected = isSelected,
                    onClick = { onScreenSelected(screen) }
                )
            }
        }
    }
}

@Composable
private fun FloatingDockItem(
    screen: Screen,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1.0f,
        animationSpec = spring(
            stiffness = Spring.StiffnessMediumLow,
            dampingRatio = Spring.DampingRatioLowBouncy
        ),
        label = "itemScale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "itemBgColor"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "itemContentColor"
    )

    val horizontalPadding by animateDpAsState(
        targetValue = if (isSelected) 16.dp else 12.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
        label = "itemHorizPadding"
    )

    Row(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = horizontalPadding, vertical = 10.dp)
            .animateContentSize(
                animationSpec = spring(
                    stiffness = Spring.StiffnessMediumLow,
                    dampingRatio = Spring.DampingRatioLowBouncy
                )
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = screen.icon,
            contentDescription = stringResource(screen.labelRes),
            tint = contentColor,
            modifier = Modifier.size(22.dp)
        )
        if (isSelected) {
            Text(
                text = stringResource(screen.labelRes),
                color = contentColor,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun AnimatedCardWrapper(
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        content()
    }
}